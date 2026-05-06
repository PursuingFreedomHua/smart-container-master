package cn.fuguang.kefu.service.impl;

import cn.fuguang.constants.RedisConstants;
import cn.fuguang.exception.ContainerException;
import cn.fuguang.kefu.pojo.bean.ChatMessageBean;
import cn.fuguang.kefu.service.ConversationHistoryService;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 对话历史服务实现（基于 Redis List）
 */
@Slf4j
@Service
public class ConversationHistoryServiceImpl implements ConversationHistoryService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /** 对话历史 TTL（分钟） */
    private static final long HISTORY_TTL_MINUTES = 30;

    /** 停止标记 TTL（秒） */
    private static final long STOP_FLAG_TTL_SECONDS = 60;

    /** 历史消息最大保留条数 */
    private static final int MAX_HISTORY_SIZE = 50;

    @Override
    public void appendMessage(String sessionId, ChatMessageBean message) {
        try {
            String key = RedisConstants.CONV_HISTORY_PREFIX + sessionId;
            String value = JSON.toJSONString(message);
            redisTemplate.opsForList().rightPush(key, value);
            // 限制历史消息数量
            redisTemplate.opsForList().trim(key, -MAX_HISTORY_SIZE, -1);
            // 刷新 TTL
            redisTemplate.expire(key, HISTORY_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("追加对话历史异常, sessionId={}", sessionId, e);
            throw ContainerException.REDIS_ERROR.newInstance("追加对话历史异常 sessionId:" + sessionId);
        }
    }

    @Override
    public List<ChatMessageBean> getHistory(String sessionId, int maxRounds) {
        try {
            String key = RedisConstants.CONV_HISTORY_PREFIX + sessionId;
            // 从 Redis List 中获取所有消息
            List<String> jsonList = redisTemplate.opsForList().range(key, 0, -1);
            if (jsonList == null || jsonList.isEmpty()) {
                return Collections.emptyList();
            }

            List<ChatMessageBean> messages = new ArrayList<>();
            for (String json : jsonList) {
                messages.add(JSON.parseObject(json, ChatMessageBean.class));
            }

            // 只返回最近 maxRounds*2 条消息（每轮包含 user + assistant）
            int maxMessages = maxRounds * 2;
            if (messages.size() > maxMessages) {
                messages = messages.subList(messages.size() - maxMessages, messages.size());
            }

            return messages;
        } catch (Exception e) {
            log.error("获取对话历史异常, sessionId={}", sessionId, e);
            throw ContainerException.REDIS_ERROR.newInstance("获取对话历史异常 sessionId:" + sessionId);
        }
    }

    @Override
    public void clearHistory(String sessionId) {
        try {
            String key = RedisConstants.CONV_HISTORY_PREFIX + sessionId;
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("清除对话历史异常, sessionId={}", sessionId, e);
            throw ContainerException.REDIS_ERROR.newInstance("清除对话历史异常 sessionId:" + sessionId);
        }
    }

    @Override
    public boolean isStopped(String sessionId) {
        try {
            String key = RedisConstants.CONV_STOP_FLAG_PREFIX + sessionId;
            String value = redisTemplate.opsForValue().get(key);
            return "1".equals(value);
        } catch (Exception e) {
            log.error("检查停止标记异常, sessionId={}", sessionId, e);
            return false;
        }
    }

    @Override
    public void setStopFlag(String sessionId) {
        try {
            String key = RedisConstants.CONV_STOP_FLAG_PREFIX + sessionId;
            redisTemplate.opsForValue().set(key, "1", STOP_FLAG_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("设置停止标记异常, sessionId={}", sessionId, e);
            throw ContainerException.REDIS_ERROR.newInstance("设置停止标记异常 sessionId:" + sessionId);
        }
    }
}
