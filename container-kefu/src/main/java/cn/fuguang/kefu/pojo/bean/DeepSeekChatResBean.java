package cn.fuguang.kefu.pojo.bean;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * DeepSeek Chat Completion 响应体（SSE 流式逐 chunk 解析）
 */
@Data
public class DeepSeekChatResBean {

    /** 响应 ID */
    private String id;

    /** 选择列表 */
    private List<Choice> choices;

    /**
     * 单个选择
     */
    @Data
    public static class Choice {
        /** 增量内容（流式） */
        private Delta delta;

        /** 结束原因：stop / length / null */
        @JSONField(name = "finish_reason")
        private String finishReason;

        /** 索引 */
        private Integer index;
    }

    /**
     * 增量内容
     */
    @Data
    public static class Delta {
        /** 增量文本内容 */
        private String content;

        /** 角色 */
        private String role;
    }
}
