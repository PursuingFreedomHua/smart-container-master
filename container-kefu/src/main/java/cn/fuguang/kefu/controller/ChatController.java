package cn.fuguang.kefu.controller;

import cn.fuguang.kefu.biz.ChatBiz;
import cn.fuguang.web.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;

/**
 * 对话 Controller（SSE 流式对话）
 */
@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private ChatBiz chatBiz;

    /**
     * SSE 流式对话
     * 前端使用 EventSource 接收：
     * const es = new EventSource('/kefu/chat/stream?sessionId=xxx&message=xxx');
     * es.addEventListener('message', e => { ... });  // 文本 chunk
     * es.addEventListener('citation', e => { ... }); // 引用来源
     * es.addEventListener('done', e => { ... });     // 完成
     * es.addEventListener('error', e => { ... });    // 错误
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(
            @RequestParam String sessionId,
            @RequestParam String message) {
        log.info("SSE 对话请求, sessionId={}, message长度={}", sessionId, message.length());
        return chatBiz.chatStream(sessionId, message);
    }

    /**
     * 停止生成
     */
    @PostMapping("/stop")
    public BaseResult<Void> stopGeneration(@RequestParam String sessionId) {
        chatBiz.stopGeneration(sessionId);
        return BaseResult.success();
    }
}
