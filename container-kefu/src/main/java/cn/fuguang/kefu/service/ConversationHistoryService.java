package cn.fuguang.kefu.service;

import cn.fuguang.kefu.pojo.bean.ChatMessageBean;

import java.util.List;

/**
 * 对话历史服务接口（Redis 存储）
 */
public interface ConversationHistoryService {

    /**
     * 追加一条消息到对话历史
     *
     * @param sessionId 会话 ID
     * @param message   消息
     */
    void appendMessage(String sessionId, ChatMessageBean message);

    /**
     * 获取对话历史
     *
     * @param sessionId 会话 ID
     * @param maxRounds 最大返回轮数
     * @return 消息列表（按时间正序）
     */
    List<ChatMessageBean> getHistory(String sessionId, int maxRounds);

    /**
     * 清除对话历史
     *
     * @param sessionId 会话 ID
     */
    void clearHistory(String sessionId);

    /**
     * 检查是否已设置停止标记
     *
     * @param sessionId 会话 ID
     * @return true 表示需要停止生成
     */
    boolean isStopped(String sessionId);

    /**
     * 设置停止生成标记
     *
     * @param sessionId 会话 ID
     */
    void setStopFlag(String sessionId);
}
