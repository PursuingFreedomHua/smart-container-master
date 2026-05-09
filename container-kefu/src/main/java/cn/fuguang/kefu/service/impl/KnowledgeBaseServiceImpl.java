package cn.fuguang.kefu.service.impl;

import cn.fuguang.exception.ContainerException;
import cn.fuguang.kefu.pojo.bean.KnowledgeChunkBean;
import cn.fuguang.kefu.service.DeepSeekEmbeddingService;
import cn.fuguang.kefu.service.KnowledgeBaseService;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 知识库检索服务实现
 * 使用 Java 内存 ConcurrentHashMap 存储向量，MySQL 持久化
 * 启动时从 MySQL 加载所有已向量化的切片到内存
 */
@Slf4j
@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Resource
    private DeepSeekEmbeddingService embeddingService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    /** 内存向量索引：chunkId → KnowledgeChunkBean */
    private final Map<String, KnowledgeChunkBean> chunkIndex = new ConcurrentHashMap<>();

    /** 默认切片大小（字符） */
    private static final int DEFAULT_CHUNK_SIZE = 512;

    /** 默认切片重叠（字符） */
    private static final int DEFAULT_CHUNK_OVERLAP = 100;

    /**
     * 启动时从 MySQL 加载所有已向量化的切片
     */
    @PostConstruct
    public void loadFromDB() {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT chunk_id, document_id, content, vector_data, metadata FROM knowledge_chunk");
            for (Map<String, Object> row : rows) {
                KnowledgeChunkBean chunk = new KnowledgeChunkBean();
                chunk.setChunkId((String) row.get("chunk_id"));
                chunk.setDocumentId((Long) row.get("document_id"));
                chunk.setContent((String) row.get("content"));

                // 从 JSON 反序列化向量
                String vectorJson = (String) row.get("vector_data");
                List<Double> vectorList = JSON.parseArray(vectorJson, Double.class);
                float[] vector = new float[vectorList.size()];
                for (int i = 0; i < vectorList.size(); i++) {
                    vector[i] = vectorList.get(i).floatValue();
                }
                chunk.setVector(vector);

                // 解析元数据
                String metadataStr = (String) row.get("metadata");
                if (metadataStr != null && !metadataStr.isEmpty()) {
                    chunk.setMetadata(JSON.parseObject(metadataStr, Map.class));
                }

                chunkIndex.put(chunk.getChunkId(), chunk);
            }
            log.info("向量索引加载完成, chunk数量={}", chunkIndex.size());
        } catch (Exception e) {
            log.warn("向量索引加载失败（可能表尚未创建）, 将在后续操作中重建", e);
        }
    }

    @Override
    public void storeChunks(List<KnowledgeChunkBean> chunks) {
        try {
            for (KnowledgeChunkBean chunk : chunks) {
                // 写入 MySQL（Java 8 不支持 float[] 的 stream，手动转换）
                float[] vec = chunk.getVector();
                double[] doubleVec = new double[vec.length];
                for (int i = 0; i < vec.length; i++) {
                    doubleVec[i] = (double) vec[i];
                }
                String vectorJson = JSON.toJSONString(doubleVec);
                String metadataJson = chunk.getMetadata() != null
                        ? JSON.toJSONString(chunk.getMetadata()) : null;

                jdbcTemplate.update(
                        "INSERT INTO knowledge_chunk (chunk_id, document_id, content, vector_data, metadata) " +
                                "VALUES (?, ?, ?, ?, ?)",
                        chunk.getChunkId(), chunk.getDocumentId(),
                        chunk.getContent(), vectorJson, metadataJson
                );

                // 写入内存索引
                chunkIndex.put(chunk.getChunkId(), chunk);
            }
            log.info("存储切片完成, 新增数量={}, 总数量={}", chunks.size(), chunkIndex.size());
        } catch (Exception e) {
            log.error("存储切片异常", e);
            throw ContainerException.DATABASE_INERT_ERROR.newInstance("存储知识切片异常");
        }
    }

    @Override
    public List<KnowledgeChunkBean> retrieve(String query, int topK, double minSimilarity) {
        if (chunkIndex.isEmpty()) {
            log.debug("向量索引为空，跳过 RAG 检索");
            return Collections.emptyList();
        }

        // 1. 向量化查询文本
        float[] queryVector = embeddingService.embed(query);

        // 2. 计算所有向量的余弦相似度
        List<KnowledgeChunkBean> results = new ArrayList<>();
        for (KnowledgeChunkBean chunk : chunkIndex.values()) {
            double similarity = cosineSimilarity(queryVector, chunk.getVector());
            if (similarity >= minSimilarity) {
                KnowledgeChunkBean match = new KnowledgeChunkBean();
                match.setChunkId(chunk.getChunkId());
                match.setDocumentId(chunk.getDocumentId());
                match.setDocumentTitle(chunk.getDocumentTitle());
                match.setContent(chunk.getContent());
                match.setMetadata(chunk.getMetadata());
                match.setSimilarity(similarity);
                results.add(match);
            }
        }

        // 3. 按相似度降序排序，取 TopK
        results.sort((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()));
        if (results.size() > topK) {
            results = results.subList(0, topK);
        }

        log.debug("RAG 检索完成, query长度={}, 匹配数={}, TopK={}",
                query.length(), results.size(), topK);
        return results;
    }

    @Override
    public void deleteByDocumentId(Long documentId) {
        try {
            // 从内存索引移除
            chunkIndex.entrySet().removeIf(entry ->
                    documentId.equals(entry.getValue().getDocumentId()));

            // 从 MySQL 删除
            jdbcTemplate.update("DELETE FROM knowledge_chunk WHERE document_id = ?", documentId);
            log.info("删除文档向量完成, documentId={}, 剩余chunk数={}", documentId, chunkIndex.size());
        } catch (Exception e) {
            log.error("删除文档向量异常, documentId={}", documentId, e);
            throw ContainerException.DATABASE_INERT_ERROR.newInstance("删除知识切片异常");
        }
    }

    /**
     * 文本切片（固定大小滑动窗口，按段落边界优先）
     */
    public List<String> splitText(String text) {
        return splitText(text, DEFAULT_CHUNK_SIZE, DEFAULT_CHUNK_OVERLAP);
    }

    /**
     * 文本切片
     *
     * @param text      原始文本
     * @param chunkSize 切片大小（字符数）
     * @param overlap   重叠大小（字符数）
     * @return 切片列表
     */
    public List<String> splitText(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return chunks;
        }

        // 先按段落分割
        String[] paragraphs = text.split("\\n\\n+");
        StringBuilder current = new StringBuilder();

        for (String para : paragraphs) {
            para = para.trim();
            if (para.isEmpty()) {
                continue;
            }

            if (current.length() + para.length() <= chunkSize) {
                if (current.length() > 0) {
                    current.append("\n\n");
                }
                current.append(para);
            } else {
                // 当前段落放不下，先保存当前块
                if (current.length() > 0) {
                    chunks.add(current.toString());
                    // 重叠部分：保留最后 overlap 个字符
                    if (current.length() > overlap) {
                        current = new StringBuilder(current.substring(current.length() - overlap));
                    } else {
                        current = new StringBuilder();
                    }
                }

                // 如果段落本身超过 chunkSize，按固定大小切分
                if (para.length() > chunkSize) {
                    int start = 0;
                    while (start < para.length()) {
                        int end = Math.min(start + chunkSize, para.length());
                        String subChunk = para.substring(start, end);
                        chunks.add(subChunk);
                        start += (chunkSize - overlap);
                    }
                } else {
                    current.append(para);
                }
            }
        }

        // 保存最后一个块
        if (current.length() > 0) {
            chunks.add(current.toString());
        }

        return chunks;
    }

    /**
     * 计算两个向量的余弦相似度
     *
     * @param a 向量 A
     * @param b 向量 B
     * @return 余弦相似度 [-1, 1]
     */
    public static double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("向量维度不一致: " + a.length + " vs " + b.length);
        }
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += (double) a[i] * b[i];
            normA += (double) a[i] * a[i];
            normB += (double) b[i] * b[i];
        }
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * 获取当前索引大小（用于监控）
     */
    public int getIndexSize() {
        return chunkIndex.size();
    }
}
