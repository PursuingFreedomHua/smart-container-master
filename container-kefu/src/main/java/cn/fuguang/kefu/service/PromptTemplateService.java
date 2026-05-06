package cn.fuguang.kefu.service;

import cn.fuguang.kefu.pojo.entity.PromptTemplateEntity;

import java.util.List;
import java.util.Map;

/**
 * Prompt 模板服务接口
 */
public interface PromptTemplateService {

    /** 新增模板 */
    Long insert(PromptTemplateEntity entity);

    /** 根据编码查询 */
    PromptTemplateEntity getByCode(String templateCode);

    /** 查询所有启用的模板 */
    List<PromptTemplateEntity> listAllActive();

    /** 更新模板 */
    void updateById(PromptTemplateEntity entity);

    /** 删除模板 */
    void deleteById(Long id);

    /**
     * 渲染模板（替换 {{变量}}）
     *
     * @param templateContent 模板内容
     * @param variables       变量映射
     * @return 渲染后的文本
     */
    String render(String templateContent, Map<String, String> variables);
}
