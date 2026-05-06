package cn.fuguang.kefu.service;

/**
 * DeepSeek Embedding API 服务接口
 */
public interface DeepSeekEmbeddingService {

    /**
     * 将文本向量化
     *
     * @param text 待向量化的文本
     * @return 向量 float 数组（1024维）
     */
    float[] embed(String text);
}
