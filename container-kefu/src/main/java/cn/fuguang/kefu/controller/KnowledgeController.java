package cn.fuguang.kefu.controller;

import cn.fuguang.kefu.biz.KnowledgeBiz;
import cn.fuguang.kefu.pojo.bean.KnowledgeChunkBean;
import cn.fuguang.kefu.pojo.dto.KnowledgeDocReqDTO;
import cn.fuguang.kefu.pojo.dto.KnowledgeSearchReqDTO;
import cn.fuguang.kefu.pojo.entity.KnowledgeDocumentEntity;
import cn.fuguang.web.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 知识库管理 Controller
 */
@Slf4j
@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

    @Resource
    private KnowledgeBiz knowledgeBiz;

    /**
     * 上传知识文档
     */
    @PostMapping("/doc/upload")
    public BaseResult<Map<String, Object>> uploadDocument(@RequestBody KnowledgeDocReqDTO reqDTO) {
        Long docId = knowledgeBiz.uploadDocument(reqDTO);
        Map<String, Object> data = new HashMap<>();
        data.put("documentId", docId);
        return BaseResult.success(data);
    }

    /**
     * 分页查询知识文档列表
     */
    @GetMapping("/doc/list")
    public BaseResult<List<KnowledgeDocumentEntity>> listDocuments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<KnowledgeDocumentEntity> list = knowledgeBiz.listDocuments(page, size);
        return BaseResult.success(list);
    }

    /**
     * 删除知识文档
     */
    @DeleteMapping("/doc/{id}")
    public BaseResult<Void> deleteDocument(@PathVariable Long id) {
        knowledgeBiz.deleteDocument(id);
        return BaseResult.success();
    }

    /**
     * 重新向量化文档
     */
    @PostMapping("/doc/{id}/reindex")
    public BaseResult<Void> reindexDocument(@PathVariable Long id) {
        knowledgeBiz.reindexDocument(id);
        return BaseResult.success();
    }

    /**
     * 知识库检索测试（不经过 LLM）
     */
    @PostMapping("/search")
    public BaseResult<List<KnowledgeChunkBean>> search(@RequestBody KnowledgeSearchReqDTO reqDTO) {
        List<KnowledgeChunkBean> results = knowledgeBiz.search(reqDTO);
        return BaseResult.success(results);
    }
}
