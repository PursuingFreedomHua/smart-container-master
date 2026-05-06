package cn.fuguang.kefu.service;

import cn.fuguang.kefu.pojo.bean.KnowledgeChunkBean;

import java.util.List;

/**
 * 知识库检索服务接口（向量存储与检索）
 */
public interface KnowledgeBaseService {

    /**
     * 存储文档切片向量
     *
     * @param chunks 切片列表
     */
    void storeChunks(List<KnowledgeChunkBean> chunks);

    /**
     * 检索相关文档片段
     *
     * @param query         用户查询
     * @param topK          返回 Top K 个结果
     * @param minSimilarity 最小相似度阈值
     * @return 相关切片列表（按相似度降序）
     */
    List<KnowledgeChunkBean> retrieve(String query, int topK, double minSimilarity);

    /**
     * 删除指定文档的所有向量
     *
     * @param documentId 文档 ID
     */
    void deleteByDocumentId(Long documentId);
}
