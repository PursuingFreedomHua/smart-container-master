package cn.fuguang.kefu.pojo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SSE 事件 Bean（前端通过 EventSource 接收）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SseEventBean {

    /** 事件类型：chunk / citation / error / done */
    private String type;

    /** 内容（文本片段 / 引用列表 JSON / 错误信息） */
    private String content;

    /** 是否结束 */
    private Boolean finish;
}
