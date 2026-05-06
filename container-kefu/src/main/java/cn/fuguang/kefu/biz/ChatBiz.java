package cn.fuguang.kefu.biz;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 对话核心业务接口
 */
public interface ChatBiz {

    /**
     * SSE 流式对话
     *
     * @param sessionId 会话 ID
     * @param message   用户消息
     * @return SseEmitter 流式响应
     */
    SseEmitter chatStream(String sessionId, String message);

    /**
     * 停止生成
     *
     * @param sessionId 会话 ID
     */
    void stopGeneration(String sessionId);
}
