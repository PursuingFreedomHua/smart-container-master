package cn.fuguang.kefu.service.impl;

import cn.fuguang.kefu.pojo.bean.ChatMessageBean;
import cn.fuguang.kefu.service.ConversationHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 对话历史服务内存实现
 */
@Slf4j
@Service
public class InMemoryConversationHistoryServiceImpl implements ConversationHistoryService {

    /** 内存对话历史存储 */
    private final Map<String, List<ChatMessageBean>> historyMap = new ConcurrentHashMap<>();

    /** 停止标记 */
    private final Map<String, Boolean> stopFlags = new ConcurrentHashMap<>();

    /** 历史消息最大保留条数 */
    private static final int MAX_HISTORY_SIZE = 50;

    /** 会话 TTL（分钟），从配置读取 */
    @Value("${conversation.history-ttl-minutes:30}")
    private int historyTtlMinutes;

    @Override
    public void appendMessage(String sessionId, ChatMessageBean message) {
        List<ChatMessageBean> messages = historyMap.computeIfAbsent(sessionId, k -> new ArrayList<>());
        synchronized (messages) {
            messages.add(message);
            // 限制历史消息数量
            if (messages.size() > MAX_HISTORY_SIZE) {
                messages.subList(0, messages.size() - MAX_HISTORY_SIZE).clear();
            }
        }
    }

    @Override
    public List<ChatMessageBean> getHistory(String sessionId, int maxRounds) {
        List<ChatMessageBean> messages = historyMap.get(sessionId);
        if (messages == null || messages.isEmpty()) {
            return Collections.emptyList();
        }
        synchronized (messages) {
            // 只返回最近 maxRounds*2 条消息
            int maxMessages = maxRounds * 2;
            if (messages.size() > maxMessages) {
                return new ArrayList<>(messages.subList(messages.size() - maxMessages, messages.size()));
            }
            return new ArrayList<>(messages);
        }
    }

    @Override
    public void clearHistory(String sessionId) {
        historyMap.remove(sessionId);
        stopFlags.remove(sessionId);
    }

    @Override
    public boolean isStopped(String sessionId) {
        return Boolean.TRUE.equals(stopFlags.get(sessionId));
    }

    @Override
    public void setStopFlag(String sessionId) {
        stopFlags.put(sessionId, Boolean.TRUE);
    }

    /**
     * 定时淘汰过期会话（每分钟执行一次）
     * 当会话最后一条消息的时间戳超过 TTL 时，清理该会话的全部数据
     */
    @Scheduled(fixedRate = 60000)
    public void evictExpiredSessions() {
        long now = System.currentTimeMillis();
        long ttlMs = TimeUnit.MINUTES.toMillis(historyTtlMinutes);
        int evictedCount = 0;

        for (Map.Entry<String, List<ChatMessageBean>> entry : historyMap.entrySet()) {
            List<ChatMessageBean> messages = entry.getValue();
            if (messages == null || messages.isEmpty()) {
                historyMap.remove(entry.getKey());
                stopFlags.remove(entry.getKey());
                evictedCount++;
                continue;
            }
            // 最后一条消息的时间戳超过 TTL 则清理
            ChatMessageBean lastMsg = messages.get(messages.size() - 1);
            if (lastMsg.getTimestamp() != null && (now - lastMsg.getTimestamp() > ttlMs)) {
                historyMap.remove(entry.getKey());
                stopFlags.remove(entry.getKey());
                evictedCount++;
            }
        }

        if (evictedCount > 0) {
            log.debug("TTL 淘汰了 {} 个过期会话", evictedCount);
        }
    }
}
