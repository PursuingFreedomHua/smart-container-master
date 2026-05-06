package cn.fuguang.kefu.pojo.bean;

import lombok.Data;

/**
 * DeepSeek Embedding 请求体
 */
@Data
public class DeepSeekEmbeddingReqBean {

    /** 模型名称 */
    private String model;

    /** 待向量化的文本 */
    private String input;
}
