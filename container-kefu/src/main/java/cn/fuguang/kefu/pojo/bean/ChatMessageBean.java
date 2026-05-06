package cn.fuguang.kefu.pojo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 对话消息 Bean
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageBean implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 角色：user / assistant / system */
    private String role;

    /** 消息内容 */
    private String content;

    /** 时间戳 */
    private Long timestamp;

    /** 引用来源（RAG 检索到的文档标题列表） */
    private List<String> citations;

    public ChatMessageBean(String role, String content) {
        this.role = role;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }
}
