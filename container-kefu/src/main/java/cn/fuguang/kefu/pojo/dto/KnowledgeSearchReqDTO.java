package cn.fuguang.kefu.pojo.dto;

import lombok.Data;

/**
 * 知识检索请求 DTO
 */
@Data
public class KnowledgeSearchReqDTO {

    /** 检索关键词/问题 */
    private String query;

    /** 返回 Top K 个结果，默认 5 */
    private Integer topK;

    /** 最小相似度阈值，默认 0.65 */
    private Double minSimilarity;
}
