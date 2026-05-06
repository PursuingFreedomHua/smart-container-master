package cn.fuguang.kefu.biz;

import cn.fuguang.kefu.pojo.bean.ChatMessageBean;
import cn.fuguang.kefu.pojo.dto.SessionCreateResDTO;

import java.util.List;

/**
 * 会话管理业务接口
 */
public interface SessionBiz {

    /**
     * 创建新会话
     *
     * @return 会话信息
     */
    SessionCreateResDTO createSession();

    /**
     * 获取会话历史消息
     *
     * @param sessionId 会话 ID
     * @return 历史消息列表
     */
    List<ChatMessageBean> getHistory(String sessionId);

    /**
     * 清除会话
     *
     * @param sessionId 会话 ID
     */
    void clearSession(String sessionId);
}
