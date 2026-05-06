package cn.fuguang.kefu.biz.impl;

import cn.fuguang.kefu.biz.SessionBiz;
import cn.fuguang.kefu.pojo.bean.ChatMessageBean;
import cn.fuguang.kefu.pojo.dto.SessionCreateResDTO;
import cn.fuguang.kefu.service.ConversationHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * 会话管理业务实现
 */
@Slf4j
@Service
public class SessionBizImpl implements SessionBiz {

    /** 默认最大历史轮数 */
    private static final int DEFAULT_MAX_ROUNDS = 10;

    @Resource
    private ConversationHistoryService conversationHistoryService;

    @Override
    public SessionCreateResDTO createSession() {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        log.info("创建新会话, sessionId={}", sessionId);
        return new SessionCreateResDTO(sessionId, System.currentTimeMillis());
    }

    @Override
    public List<ChatMessageBean> getHistory(String sessionId) {
        return conversationHistoryService.getHistory(sessionId, DEFAULT_MAX_ROUNDS);
    }

    @Override
    public void clearSession(String sessionId) {
        log.info("清除会话, sessionId={}", sessionId);
        conversationHistoryService.clearHistory(sessionId);
    }
}
