package cn.fuguang.kefu.service.impl;

import cn.fuguang.exception.ContainerException;
import cn.fuguang.kefu.config.DeepSeekApiConfig;
import cn.fuguang.kefu.pojo.bean.DeepSeekChatReqBean;
import cn.fuguang.kefu.pojo.bean.DeepSeekChatResBean;
import cn.fuguang.kefu.service.DeepSeekService;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

/**
 * DeepSeek Chat API 服务实现
 * 支持流式（SSE）和非流式两种调用方式
 */
@Slf4j
@Service
public class DeepSeekServiceImpl implements DeepSeekService {

    @Resource
    private DeepSeekApiConfig config;

    /** SSE 数据行前缀 */
    private static final String SSE_DATA_PREFIX = "data: ";

    /** SSE 结束标记 */
    private static final String SSE_DONE = "[DONE]";

    @Override
    public void chatStream(List<DeepSeekChatReqBean.Message> messages,
                           Consumer<String> onToken,
                           Runnable onFinish) {
        try {
            DeepSeekChatReqBean reqBody = new DeepSeekChatReqBean();
            reqBody.setModel(config.getChatModel());
            reqBody.setMessages(messages);
            reqBody.setStream(true);
            reqBody.setTemperature(config.getTemperature());
            reqBody.setMaxTokens(config.getMaxTokens());

            String bodyJson = JSON.toJSONString(reqBody);

            log.debug("调用 DeepSeek Chat Stream API, 消息数={}", messages.size());

            HttpRequest httpRequest = HttpRequest.post(config.getChatUrl())
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .header("Content-Type", "application/json")
                    .header("Accept", "text/event-stream")
                    .body(bodyJson)
                    .timeout(config.getReadTimeout());

            try (HttpResponse response = httpRequest.execute()) {
                if (!response.isOk()) {
                    String errorBody = response.body();
                    log.error("DeepSeek API 响应异常, status={}, body={}", response.getStatus(), errorBody);
                    throw ContainerException.LLM_API_ERROR.newInstance(
                            "API 返回状态码: " + response.getStatus());
                }

                // 逐行读取 SSE 流
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.bodyStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 跳过空行
                        if (line.isEmpty()) {
                            continue;
                        }

                        if (line.startsWith(SSE_DATA_PREFIX)) {
                            String data = line.substring(SSE_DATA_PREFIX.length()).trim();

                            // 结束标记
                            if (SSE_DONE.equals(data)) {
                                log.debug("DeepSeek SSE 流结束");
                                break;
                            }

                            // 解析 JSON 提取 delta.content
                            try {
                                DeepSeekChatResBean chunk = JSON.parseObject(data, DeepSeekChatResBean.class);
                                if (chunk != null && chunk.getChoices() != null) {
                                    for (DeepSeekChatResBean.Choice choice : chunk.getChoices()) {
                                        if (choice.getDelta() != null
                                                && choice.getDelta().getContent() != null) {
                                            onToken.accept(choice.getDelta().getContent());
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                log.warn("解析 DeepSeek SSE 数据行异常, data={}", data, e);
                            }
                        }
                    }
                }
            }

            // 流式响应正常完成后调用 onFinish（不在 finally 中，避免异常时重复 emitter.complete()）
            onFinish.run();

        } catch (ContainerException e) {
            throw e;
        } catch (Exception e) {
            log.error("DeepSeek Chat Stream API 调用异常", e);
            throw ContainerException.LLM_API_ERROR.newInstance("Chat API 调用异常: " + e.getMessage());
        }
    }

    @Override
    public String chatSync(List<DeepSeekChatReqBean.Message> messages) {
        try {
            DeepSeekChatReqBean reqBody = new DeepSeekChatReqBean();
            reqBody.setModel(config.getChatModel());
            reqBody.setMessages(messages);
            reqBody.setStream(false);
            reqBody.setTemperature(config.getTemperature());
            reqBody.setMaxTokens(config.getMaxTokens());

            String bodyJson = JSON.toJSONString(reqBody);

            log.debug("调用 DeepSeek Chat Sync API, 消息数={}", messages.size());

            String responseBody;
            try (HttpResponse response = HttpRequest.post(config.getChatUrl())
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .header("Content-Type", "application/json")
                    .body(bodyJson)
                    .timeout(config.getReadTimeout())
                    .execute()) {

                responseBody = response.body();

                if (!response.isOk()) {
                    log.error("DeepSeek API 响应异常, status={}, body={}", response.getStatus(), responseBody);
                    throw ContainerException.LLM_API_ERROR.newInstance(
                            "API 返回状态码: " + response.getStatus());
                }
            }

            DeepSeekChatResBean res = JSON.parseObject(responseBody, DeepSeekChatResBean.class);
            if (res != null && res.getChoices() != null && !res.getChoices().isEmpty()) {
                DeepSeekChatResBean.Choice choice = res.getChoices().get(0);
                // 非流式响应使用 message 字段
                if (choice.getMessage() != null && choice.getMessage().getContent() != null) {
                    return choice.getMessage().getContent();
                }
            }

            log.warn("DeepSeek Chat Sync 响应格式异常, body={}", responseBody);
            return "抱歉，模型返回了空的响应。";

        } catch (ContainerException e) {
            throw e;
        } catch (Exception e) {
            log.error("DeepSeek Chat Sync API 调用异常", e);
            throw ContainerException.LLM_API_ERROR.newInstance("Chat API 调用异常: " + e.getMessage());
        }
    }
}
