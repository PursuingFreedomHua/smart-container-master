package cn.fuguang.kefu.service.impl;

import cn.fuguang.exception.ContainerException;
import cn.fuguang.kefu.mapper.PromptTemplateMapper;
import cn.fuguang.kefu.pojo.entity.PromptTemplateEntity;
import cn.fuguang.kefu.service.PromptTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Prompt 模板服务实现
 */
@Slf4j
@Service
public class PromptTemplateServiceImpl implements PromptTemplateService {

    @Resource
    private PromptTemplateMapper promptTemplateMapper;

    @Override
    public Long insert(PromptTemplateEntity entity) {
        try {
            int rows = promptTemplateMapper.insert(entity);
            if (rows <= 0) {
                throw ContainerException.DATABASE_INERT_ERROR.newInstance("Prompt模板插入失败");
            }
            return entity.getId();
        } catch (ContainerException e) {
            throw e;
        } catch (Exception e) {
            log.error("插入Prompt模板异常", e);
            throw ContainerException.DATABASE_INERT_ERROR.newInstance("Prompt模板插入异常");
        }
    }

    @Override
    public PromptTemplateEntity getByCode(String templateCode) {
        try {
            PromptTemplateEntity entity = promptTemplateMapper.selectByCode(templateCode);
            if (entity == null) {
                log.warn("Prompt模板不存在, code={}", templateCode);
                return null;
            }
            return entity;
        } catch (Exception e) {
            log.error("查询Prompt模板异常, code={}", templateCode, e);
            return null;
        }
    }

    @Override
    public List<PromptTemplateEntity> listAllActive() {
        try {
            return promptTemplateMapper.selectAllActive();
        } catch (Exception e) {
            log.error("查询Prompt模板列表异常", e);
            return null;
        }
    }

    @Override
    public void updateById(PromptTemplateEntity entity) {
        try {
            promptTemplateMapper.updateById(entity);
        } catch (Exception e) {
            log.error("更新Prompt模板异常", e);
            throw ContainerException.DATABASE_UPDATE_ERROR.newInstance("Prompt模板更新异常");
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            promptTemplateMapper.deleteById(id);
        } catch (Exception e) {
            log.error("删除Prompt模板异常", e);
            throw ContainerException.DATABASE_INERT_ERROR.newInstance("Prompt模板删除异常");
        }
    }

    @Override
    public String render(String templateContent, Map<String, String> variables) {
        if (templateContent == null || variables == null) {
            return templateContent;
        }
        String result = templateContent;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }
}
