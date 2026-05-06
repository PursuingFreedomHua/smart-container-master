package cn.fuguang.kefu.mapper;

import cn.fuguang.kefu.pojo.entity.KnowledgeDocumentEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识文档 Mapper
 */
public interface KnowledgeDocumentMapper {

    /** 插入文档 */
    int insert(KnowledgeDocumentEntity entity);

    /** 根据 ID 查询 */
    KnowledgeDocumentEntity selectById(@Param("id") Long id);

    /** 分页查询 */
    List<KnowledgeDocumentEntity> selectByPage(@Param("offset") Integer offset,
                                                @Param("size") Integer size);

    /** 根据 ID 更新状态 */
    int updateStatus(@Param("id") Long id,
                     @Param("status") String status,
                     @Param("chunkCount") Integer chunkCount);

    /** 根据 ID 删除 */
    int deleteById(@Param("id") Long id);

    /** 统计总数 */
    int count();
}
