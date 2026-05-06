package cn.fuguang.kefu.pojo.entity;

import lombok.Data;

import java.util.Date;

/**
 * 知识文档实体
 */
@Data
public class KnowledgeDocumentEntity {

    /** 主键 */
    private Long id;

    /** 文档标题 */
    private String title;

    /** 原始文本内容 */
    private String content;

    /** 状态：PENDING / VECTORIZED / FAILED */
    private String status;

    /** 切片数量 */
    private Integer chunkCount;

    /** 分类：order / device / payment / account / other */
    private String category;

    /** 备注 */
    private String remarks;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
