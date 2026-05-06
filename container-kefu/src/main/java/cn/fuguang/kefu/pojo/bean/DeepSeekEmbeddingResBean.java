package cn.fuguang.kefu.pojo.bean;

import lombok.Data;

import java.util.List;

/**
 * DeepSeek Embedding 响应体
 */
@Data
public class DeepSeekEmbeddingResBean {

    /** 向量数据列表 */
    private List<EmbeddingData> data;

    /** 模型名称 */
    private String model;

    /**
     * 单条向量数据
     */
    @Data
    public static class EmbeddingData {
        /** 向量 float 数组 */
        private float[] embedding;

        /** 索引 */
        private Integer index;
    }
}
