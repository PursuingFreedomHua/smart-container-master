package cn.fuguang.kefu.biz.impl;

import cn.fuguang.exception.ContainerException;
import cn.fuguang.kefu.biz.ChatBiz;
import cn.fuguang.kefu.pojo.bean.*;
import cn.fuguang.kefu.pojo.entity.PromptTemplateEntity;
import cn.fuguang.kefu.service.ConversationHistoryService;
import cn.fuguang.kefu.service.DeepSeekService;
import cn.fuguang.kefu.service.KnowledgeBaseService;
import cn.fuguang.kefu.service.PromptTemplateService;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 对话核心编排实现
 * 编排完整流程：加载历史 → RAG检索 → 渲染Prompt → 调用LLM → SSE推送 → 保存历史
 */
@Slf4j
@Service
public class ChatBizImpl implements ChatBiz {

    /** 系统 Prompt 模板编码 */
    private static final String RAG_SYSTEM_PROMPT_CODE = "RAG_SYSTEM_PROMPT";

    /** 默认 RAG 检索 TopK */
    private static final int DEFAULT_RAG_TOP_K = 5;

    /** 默认 RAG 最小相似度 */
    private static final double DEFAULT_RAG_MIN_SIMILARITY = 0.65;

    /** 默认历史轮数 */
    private static final int DEFAULT_HISTORY_ROUNDS = 10;

    /** SSE 超时时间（毫秒） */
    private static final long SSE_TIMEOUT_MS = 300_000L;

    /** 用于异步执行对话的线程池（有界线程池，防止 OOM） */
    private final ExecutorService executor = new ThreadPoolExecutor(
            4, 20, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(200),
            new ThreadPoolExecutor.CallerRunsPolicy());

    @Resource
    private ConversationHistoryService conversationHistoryService;

    @Resource
    private KnowledgeBaseService knowledgeBaseService;

    @Resource
    private DeepSeekService deepSeekService;

    @Resource
    private PromptTemplateService promptTemplateService;

    @Override
    public SseEmitter chatStream(String sessionId, String message) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);

        // 注册 SSE 回调
        emitter.onCompletion(() -> log.info("SSE 完成, sessionId={}", sessionId));
        emitter.onTimeout(() -> log.warn("SSE 超时, sessionId={}", sessionId));
        emitter.onError(e -> log.error("SSE 异常, sessionId={}", sessionId, e));

        // 在异步线程中执行对话
        executor.execute(() -> {
            StringBuilder fullResponse = new StringBuilder();
            List<String> citations = new ArrayList<>();

            try {
                // 1. 保存用户消息到历史
                ChatMessageBean userMsg = new ChatMessageBean("user", message);
                conversationHistoryService.appendMessage(sessionId, userMsg);

                // 2. 加载对话历史
                List<ChatMessageBean> history = conversationHistoryService.getHistory(
                        sessionId, DEFAULT_HISTORY_ROUNDS);

                // 3. RAG 知识库检索
                List<KnowledgeChunkBean> ragResults = knowledgeBaseService.retrieve(
                        message, DEFAULT_RAG_TOP_K, DEFAULT_RAG_MIN_SIMILARITY);

                // 构建知识上下文
                StringBuilder knowledgeContext = new StringBuilder();
                if (!ragResults.isEmpty()) {
                    for (int i = 0; i < ragResults.size(); i++) {
                        KnowledgeChunkBean chunk = ragResults.get(i);
                        knowledgeContext.append("[").append(i + 1).append("] ")
                                .append(chunk.getContent()).append("\n");
                        if (chunk.getDocumentTitle() != null) {
                            citations.add(chunk.getDocumentTitle());
                        }
                    }
                    log.debug("RAG 检索命中, sessionId={}, 匹配数={}", sessionId, ragResults.size());
                } else {
                    knowledgeContext.append("暂无相关参考资料");
                }

                // 4. 加载并渲染系统 Prompt
                String systemPrompt = buildSystemPrompt(knowledgeContext.toString());

                // 5. 构建 messages 列表
                List<DeepSeekChatReqBean.Message> messages = new ArrayList<>();
                // 添加系统 prompt
                messages.add(new DeepSeekChatReqBean.Message("system", systemPrompt));
                // 添加历史消息（排除最后一条用户消息，因为它会在 RAG 上下文中体现）
                for (int i = 0; i < history.size() - 1; i++) {
                    ChatMessageBean hMsg = history.get(i);
                    messages.add(new DeepSeekChatReqBean.Message(hMsg.getRole(), hMsg.getContent()));
                }
                // 添加当前用户消息（带 RAG 上下文）
                String enhancedUserMsg = buildEnhancedUserMessage(message, knowledgeContext.toString());
                messages.add(new DeepSeekChatReqBean.Message("user", enhancedUserMsg));

                // 6. 先发送引用信息
                if (!citations.isEmpty()) {
                    SseEventBean citationEvent = new SseEventBean("citation",
                            JSON.toJSONString(citations), false);
                    sendSseEvent(emitter, "citation", citationEvent);
                }

                // 7. 调用 DeepSeek 流式 API
                deepSeekService.chatStream(messages,
                        // onToken：每收到一个 token 就推送给前端
                        token -> {
                            if (conversationHistoryService.isStopped(sessionId)) {
                                return;
                            }
                            fullResponse.append(token);
                            SseEventBean event = new SseEventBean("chunk", token, false);
                            sendSseEvent(emitter, "message", event);
                        },
                        // onFinish：对话结束
                        () -> {
                            // 8. 保存助手回复到历史
                            String assistantContent = fullResponse.toString();
                            if (assistantContent.length() > 0) {
                                ChatMessageBean assistantMsg = new ChatMessageBean(
                                        "assistant", assistantContent);
                                assistantMsg.setCitations(
                                        citations.isEmpty() ? null : new ArrayList<>(citations));
                                conversationHistoryService.appendMessage(sessionId, assistantMsg);
                            }

                            // 9. 发送完成事件
                            SseEventBean doneEvent = new SseEventBean("done", null, true);
                            sendSseEvent(emitter, "done", doneEvent);
                            emitter.complete();
                            log.info("对话完成, sessionId={}, 回复长度={}",
                                    sessionId, assistantContent.length());
                        }
                );

            } catch (ContainerException e) {
                log.error("对话业务异常, sessionId={}", sessionId, e);
                SseEventBean errorEvent = new SseEventBean("error",
                        "抱歉，服务暂时不可用：" + e.getMessage(), true);
                sendSseEvent(emitter, "error", errorEvent);
                emitter.complete();
            } catch (Exception e) {
                log.error("对话系统异常, sessionId={}", sessionId, e);
                SseEventBean errorEvent = new SseEventBean("error",
                        "抱歉，系统出现异常，请稍后重试", true);
                sendSseEvent(emitter, "error", errorEvent);
                emitter.complete();
            }
        });

        return emitter;
    }

    @Override
    public void stopGeneration(String sessionId) {
        log.info("用户停止生成, sessionId={}", sessionId);
        conversationHistoryService.setStopFlag(sessionId);
    }

    /**
     * 应用关闭时优雅关闭线程池
     */
    @PreDestroy
    public void shutdown() {
        log.info("正在关闭对话执行线程池...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("对话执行线程池已关闭");
    }

    /**
     * 构建系统 Prompt（从模板渲染）
     */
    private String buildSystemPrompt(String knowledgeContext) {
        PromptTemplateEntity template = promptTemplateService.getByCode(RAG_SYSTEM_PROMPT_CODE);
        String templateContent;
        if (template != null) {
            templateContent = template.getContent();
        } else {
            // 使用默认 Prompt
            templateContent = getDefaultSystemPrompt();
        }

        Map<String, String> variables = new HashMap<>();
        variables.put("knowledge_context", knowledgeContext);
        return promptTemplateService.render(templateContent, variables);
    }

    /**
     * 构建增强的用户消息（将 RAG 知识上下文作为参考资料附加到用户消息）
     */
    private String buildEnhancedUserMessage(String userMessage, String knowledgeContext) {
        StringBuilder sb = new StringBuilder();
        sb.append("用户问题：").append(userMessage).append("\n");
        sb.append("参考资料：\n").append(knowledgeContext);
        return sb.toString();
    }

    /**
     * 发送 SSE 事件（捕获异常避免中断流）
     */
    private void sendSseEvent(SseEmitter emitter, String eventName, SseEventBean event) {
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(JSON.toJSONString(event)));
        } catch (Exception e) {
            log.warn("SSE 发送事件异常, eventName={}", eventName, e);
        }
    }

    /**
     * 默认系统 Prompt
     */
    private String getDefaultSystemPrompt() {
        return "你是 Smart Container 智能货柜项目的客服助手。\n" +
                "请根据以下参考资料回答用户问题。如果参考资料不足以回答问题，请如实告知。\n\n" +
                "## 参考资料\n{{knowledge_context}}\n\n" +
                "## 回答要求\n" +
                "1. 回答准确、简洁、友好\n" +
                "2. 中文回答\n" +
                "3. 涉及操作步骤时，请给出清晰的指引\n" +
                "4. 涉及订单问题时，提醒用户提供订单号以便进一步查询";
    }
}
