package cn.fuguang.kefu.pojo.entity;

import lombok.Data;

import java.util.Date;

/**
 * Prompt 模板实体
 */
@Data
public class PromptTemplateEntity {

    /** 主键 */
    private Long id;

    /** 模板编码 */
    private String templateCode;

    /** 模板名称 */
    private String templateName;

    /** 模板内容（支持 {{变量}}） */
    private String content;

    /** 是否启用：1启用 0禁用 */
    private Integer isActive;

    /** 备注 */
    private String remarks;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
