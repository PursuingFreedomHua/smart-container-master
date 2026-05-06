package cn.fuguang.kefu.controller;

import cn.fuguang.kefu.biz.SessionBiz;
import cn.fuguang.kefu.pojo.bean.ChatMessageBean;
import cn.fuguang.kefu.pojo.dto.SessionCreateResDTO;
import cn.fuguang.web.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 会话管理 Controller
 */
@Slf4j
@RestController
@RequestMapping("/session")
public class SessionController {

    @Resource
    private SessionBiz sessionBiz;

    /**
     * 创建新会话
     */
    @PostMapping("/create")
    public BaseResult<SessionCreateResDTO> createSession() {
        SessionCreateResDTO res = sessionBiz.createSession();
        return BaseResult.success(res);
    }

    /**
     * 获取会话历史消息
     */
    @GetMapping("/history")
    public BaseResult<List<ChatMessageBean>> getHistory(@RequestParam String sessionId) {
        List<ChatMessageBean> history = sessionBiz.getHistory(sessionId);
        return BaseResult.success(history);
    }

    /**
     * 清除会话
     */
    @DeleteMapping("/clear")
    public BaseResult<Void> clearSession(@RequestParam String sessionId) {
        sessionBiz.clearSession(sessionId);
        return BaseResult.success();
    }
}
