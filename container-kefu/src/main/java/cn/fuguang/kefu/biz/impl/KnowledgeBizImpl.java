package cn.fuguang.kefu.biz.impl;

import cn.fuguang.kefu.biz.KnowledgeBiz;
import cn.fuguang.kefu.pojo.bean.KnowledgeChunkBean;
import cn.fuguang.kefu.pojo.dto.KnowledgeDocReqDTO;
import cn.fuguang.kefu.pojo.dto.KnowledgeSearchReqDTO;
import cn.fuguang.kefu.pojo.entity.KnowledgeDocumentEntity;
import cn.fuguang.kefu.service.KnowledgeBaseService;
import cn.fuguang.kefu.service.KnowledgeDocumentService;
import cn.fuguang.kefu.service.DeepSeekEmbeddingService;
import cn.fuguang.kefu.service.impl.KnowledgeBaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 知识库业务实现
 */
@Slf4j
@Service
public class KnowledgeBizImpl implements KnowledgeBiz {

    /** 文档状态常量 */
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_VECTORIZED = "VECTORIZED";
    private static final String STATUS_FAILED = "FAILED";

    @Resource
    private KnowledgeDocumentService knowledgeDocumentService;

    @Resource
    private KnowledgeBaseService knowledgeBaseService;

    @Resource
    private DeepSeekEmbeddingService embeddingService;

    /** 注入具体实现类以调用 splitText 方法 */
    @Resource
    private KnowledgeBaseServiceImpl knowledgeBaseServiceImpl;

    @Override
    public Long uploadDocument(KnowledgeDocReqDTO reqDTO) {
        // 1. 保存文档元数据
        KnowledgeDocumentEntity entity = new KnowledgeDocumentEntity();
        entity.setTitle(reqDTO.getTitle());
        entity.setContent(reqDTO.getContent());
        entity.setStatus(STATUS_PENDING);
        entity.setChunkCount(0);
        entity.setCategory(reqDTO.getCategory());
        entity.setRemarks(reqDTO.getRemarks());

        Long docId = knowledgeDocumentService.insert(entity);
        log.info("知识文档已保存, id={}, title={}", docId, reqDTO.getTitle());

        // 2. 异步向量化（这里同步执行，后续可改为 RocketMQ 异步处理）
        try {
            // 文本切片
            List<String> chunks = knowledgeBaseServiceImpl.splitText(reqDTO.getContent());
            log.info("文本切片完成, docId={}, chunk数={}", docId, chunks.size());

            // 批量向量化并存储
            List<KnowledgeChunkBean> chunkBeans = new ArrayList<>();
            for (String chunkText : chunks) {
                float[] vector = embeddingService.embed(chunkText);
                KnowledgeChunkBean bean = new KnowledgeChunkBean();
                bean.setChunkId(UUID.randomUUID().toString().replace("-", ""));
                bean.setDocumentId(docId);
                bean.setDocumentTitle(reqDTO.getTitle());
                bean.setContent(chunkText);
                bean.setVector(vector);

                Map<String, Object> metadata = new HashMap<>();
                metadata.put("source", reqDTO.getTitle());
                metadata.put("category", reqDTO.getCategory());
                bean.setMetadata(metadata);

                chunkBeans.add(bean);
            }

            knowledgeBaseService.storeChunks(chunkBeans);

            // 3. 更新文档状态
            knowledgeDocumentService.updateStatus(docId, STATUS_VECTORIZED, chunks.size());
            log.info("知识文档向量化完成, id={}, chunk数={}", docId, chunks.size());

        } catch (Exception e) {
            log.error("知识文档向量化失败, id={}", docId, e);
            knowledgeDocumentService.updateStatus(docId, STATUS_FAILED, 0);
            // 不抛异常，文档已保存，允许后续重新向量化
        }

        return docId;
    }

    @Override
    public List<KnowledgeDocumentEntity> listDocuments(Integer page, Integer size) {
        return knowledgeDocumentService.listByPage(page, size);
    }

    @Override
    public void deleteDocument(Long id) {
        // 先获取文档信息（校验存在性）
        knowledgeDocumentService.getById(id);
        // 删除向量
        knowledgeBaseService.deleteByDocumentId(id);
        // 删除文档
        knowledgeDocumentService.deleteById(id);
        log.info("知识文档已删除, id={}", id);
    }

    @Override
    public void reindexDocument(Long id) {
        KnowledgeDocumentEntity doc = knowledgeDocumentService.getById(id);
        // 删除旧向量
        knowledgeBaseService.deleteByDocumentId(id);

        // 重新切片 + 向量化
        try {
            List<String> chunks = knowledgeBaseServiceImpl.splitText(doc.getContent());
            List<KnowledgeChunkBean> chunkBeans = new ArrayList<>();
            for (String chunkText : chunks) {
                float[] vector = embeddingService.embed(chunkText);
                KnowledgeChunkBean bean = new KnowledgeChunkBean();
                bean.setChunkId(UUID.randomUUID().toString().replace("-", ""));
                bean.setDocumentId(id);
                bean.setDocumentTitle(doc.getTitle());
                bean.setContent(chunkText);
                bean.setVector(vector);

                Map<String, Object> metadata = new HashMap<>();
                metadata.put("source", doc.getTitle());
                metadata.put("category", doc.getCategory());
                bean.setMetadata(metadata);

                chunkBeans.add(bean);
            }

            knowledgeBaseService.storeChunks(chunkBeans);
            knowledgeDocumentService.updateStatus(id, STATUS_VECTORIZED, chunks.size());
            log.info("知识文档重新向量化完成, id={}, chunk数={}", id, chunks.size());

        } catch (Exception e) {
            log.error("重新向量化失败, id={}", id, e);
            knowledgeDocumentService.updateStatus(id, STATUS_FAILED, 0);
        }
    }

    @Override
    public List<KnowledgeChunkBean> search(KnowledgeSearchReqDTO reqDTO) {
        int topK = reqDTO.getTopK() != null ? reqDTO.getTopK() : 5;
        double minSimilarity = reqDTO.getMinSimilarity() != null ? reqDTO.getMinSimilarity() : 0.65;
        return knowledgeBaseService.retrieve(reqDTO.getQuery(), topK, minSimilarity);
    }
}
