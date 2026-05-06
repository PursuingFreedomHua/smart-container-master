package cn.fuguang.kefu.pojo.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DeepSeek Chat Completion 请求体
 */
@Data
public class DeepSeekChatReqBean {

    /** 模型名称 */
    private String model;

    /** 消息列表 */
    private List<Message> messages;

    /** 是否流式输出 */
    @JSONField(name = "stream")
    private Boolean stream;

    /** 温度参数 */
    private Double temperature;

    /** 最大输出 token 数 */
    @JSONField(name = "max_tokens")
    private Integer maxTokens;

    /**
     * 消息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        /** 角色：system / user / assistant */
        private String role;

        /** 消息内容 */
        private String content;
    }
}
