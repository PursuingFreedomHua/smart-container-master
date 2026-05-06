package cn.fuguang.kefu.service;

import cn.fuguang.kefu.pojo.entity.KnowledgeDocumentEntity;

import java.util.List;

/**
 * 知识文档 CRUD 服务接口
 */
public interface KnowledgeDocumentService {

    /** 新增文档 */
    Long insert(KnowledgeDocumentEntity entity);

    /** 根据 ID 查询 */
    KnowledgeDocumentEntity getById(Long id);

    /** 分页查询 */
    List<KnowledgeDocumentEntity> listByPage(Integer page, Integer size);

    /** 更新文档状态 */
    void updateStatus(Long id, String status, Integer chunkCount);

    /** 删除文档 */
    void deleteById(Long id);

    /** 统计总数 */
    int count();
}
