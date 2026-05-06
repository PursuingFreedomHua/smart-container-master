package cn.fuguang.kefu.biz;

import cn.fuguang.kefu.pojo.bean.KnowledgeChunkBean;
import cn.fuguang.kefu.pojo.dto.KnowledgeDocReqDTO;
import cn.fuguang.kefu.pojo.dto.KnowledgeSearchReqDTO;
import cn.fuguang.kefu.pojo.entity.KnowledgeDocumentEntity;

import java.util.List;

/**
 * 知识库业务接口
 */
public interface KnowledgeBiz {

    /** 上传文档（保存 → 切片 → 向量化 → 存储） */
    Long uploadDocument(KnowledgeDocReqDTO reqDTO);

    /** 分页查询知识文档列表 */
    List<KnowledgeDocumentEntity> listDocuments(Integer page, Integer size);

    /** 删除文档及其向量 */
    void deleteDocument(Long id);

    /** 重新向量化指定文档 */
    void reindexDocument(Long id);

    /** 知识库检索（不经过 LLM） */
    List<KnowledgeChunkBean> search(KnowledgeSearchReqDTO reqDTO);
}
