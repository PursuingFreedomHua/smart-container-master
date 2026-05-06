package cn.fuguang.kefu.service.impl;

import cn.fuguang.exception.ContainerException;
import cn.fuguang.kefu.mapper.KnowledgeDocumentMapper;
import cn.fuguang.kefu.pojo.entity.KnowledgeDocumentEntity;
import cn.fuguang.kefu.service.KnowledgeDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 知识文档 CRUD 服务实现
 */
@Slf4j
@Service
public class KnowledgeDocumentServiceImpl implements KnowledgeDocumentService {

    @Resource
    private KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Override
    public Long insert(KnowledgeDocumentEntity entity) {
        try {
            int rows = knowledgeDocumentMapper.insert(entity);
            if (rows <= 0) {
                throw ContainerException.DATABASE_INERT_ERROR.newInstance("知识文档插入失败");
            }
            return entity.getId();
        } catch (ContainerException e) {
            throw e;
        } catch (Exception e) {
            log.error("插入知识文档异常", e);
            throw ContainerException.DATABASE_INERT_ERROR.newInstance("知识文档插入异常");
        }
    }

    @Override
    public KnowledgeDocumentEntity getById(Long id) {
        try {
            KnowledgeDocumentEntity entity = knowledgeDocumentMapper.selectById(id);
            if (entity == null) {
                throw ContainerException.DATE_NOT_EXIST_ERROR.newInstance("知识文档不存在 id:" + id);
            }
            return entity;
        } catch (ContainerException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询知识文档异常, id={}", id, e);
            throw ContainerException.DATABASE_QUERY_ERROR.newInstance("知识文档查询异常");
        }
    }

    @Override
    public List<KnowledgeDocumentEntity> listByPage(Integer page, Integer size) {
        try {
            int offset = (page - 1) * size;
            return knowledgeDocumentMapper.selectByPage(offset, size);
        } catch (Exception e) {
            log.error("分页查询知识文档异常", e);
            return Collections.emptyList();
        }
    }

    @Override
    public void updateStatus(Long id, String status, Integer chunkCount) {
        try {
            knowledgeDocumentMapper.updateStatus(id, status, chunkCount);
        } catch (Exception e) {
            log.error("更新知识文档状态异常, id={}", id, e);
            throw ContainerException.DATABASE_UPDATE_ERROR.newInstance("知识文档状态更新异常");
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            knowledgeDocumentMapper.deleteById(id);
        } catch (Exception e) {
            log.error("删除知识文档异常, id={}", id, e);
            throw ContainerException.DATABASE_INERT_ERROR.newInstance("知识文档删除异常");
        }
    }

    @Override
    public int count() {
        return knowledgeDocumentMapper.count();
    }
}
