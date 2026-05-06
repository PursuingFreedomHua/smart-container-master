package cn.fuguang.kefu.pojo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 知识切片 Bean
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeChunkBean {

    /** 切片唯一标识（UUID） */
    private String chunkId;

    /** 关联文档 ID */
    private Long documentId;

    /** 文档标题 */
    private String documentTitle;

    /** 切片文本内容 */
    private String content;

    /** 向量数据（1024维） */
    private float[] vector;

    /** 元数据（来源、页码、分类等） */
    private Map<String, Object> metadata;

    /** 余弦相似度（检索时计算，非持久化） */
    private Double similarity;
}
