package cn.fuguang.kefu.mapper;

import cn.fuguang.kefu.pojo.entity.PromptTemplateEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Prompt 模板 Mapper
 */
public interface PromptTemplateMapper {

    /** 插入模板 */
    int insert(PromptTemplateEntity entity);

    /** 根据模板编码查询 */
    PromptTemplateEntity selectByCode(@Param("templateCode") String templateCode);

    /** 查询所有启用的模板 */
    List<PromptTemplateEntity> selectAllActive();

    /** 根据 ID 更新 */
    int updateById(PromptTemplateEntity entity);

    /** 根据 ID 删除 */
    int deleteById(@Param("id") Long id);
}
