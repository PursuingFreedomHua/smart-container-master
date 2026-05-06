package cn.fuguang.kefu.pojo.dto;

import lombok.Data;

/**
 * 知识文档请求 DTO
 */
@Data
public class KnowledgeDocReqDTO {

    /** 文档标题 */
    private String title;

    /** 原始文本内容 */
    private String content;

    /** 分类 */
    private String category;

    /** 备注 */
    private String remarks;
}
