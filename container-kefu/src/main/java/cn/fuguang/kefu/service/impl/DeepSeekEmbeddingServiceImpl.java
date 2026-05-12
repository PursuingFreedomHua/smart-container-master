package cn.fuguang.kefu.service.impl;

import cn.fuguang.exception.ContainerException;
import cn.fuguang.kefu.config.DeepSeekApiConfig;
import cn.fuguang.kefu.pojo.bean.DeepSeekEmbeddingReqBean;
import cn.fuguang.kefu.pojo.bean.DeepSeekEmbeddingResBean;
import cn.fuguang.kefu.service.DeepSeekEmbeddingService;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * DeepSeek Embedding API 服务实现
 */
@Slf4j
@Service
public class DeepSeekEmbeddingServiceImpl implements DeepSeekEmbeddingService {

    @Resource
    private DeepSeekApiConfig config;

    @Override
    public float[] embed(String text) {
        try {
            DeepSeekEmbeddingReqBean reqBody = new DeepSeekEmbeddingReqBean();
            reqBody.setModel(config.getEmbeddingModel());
            reqBody.setInput(text);

            String bodyJson = JSON.toJSONString(reqBody);

            log.debug("调用 DeepSeek Embedding API, 文本长度={}", text.length());

            String responseBody;
            try (HttpResponse response = HttpRequest.post(config.getEmbeddingUrl())
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .header("Content-Type", "application/json")
                    .body(bodyJson)
                    .timeout(config.getConnectTimeout())
                    .execute()) {

                responseBody = response.body();

                if (!response.isOk()) {
                    log.error("DeepSeek Embedding API 响应异常, status={}, body={}", response.getStatus(), responseBody);
                    throw ContainerException.LLM_EMBEDDING_ERROR.newInstance(
                            "API 返回状态码: " + response.getStatus());
                }
            }

            DeepSeekEmbeddingResBean res = JSON.parseObject(responseBody, DeepSeekEmbeddingResBean.class);

            if (res == null || res.getData() == null || res.getData().isEmpty()) {
                log.error("DeepSeek Embedding 响应为空, body={}", responseBody);
                throw ContainerException.LLM_EMBEDDING_ERROR.newInstance("Embedding 响应为空");
            }

            float[] embedding = res.getData().get(0).getEmbedding();
            log.debug("DeepSeek Embedding 成功, 向量维度={}", embedding.length);
            return embedding;

        } catch (ContainerException e) {
            throw e;
        } catch (Exception e) {
            log.error("DeepSeek Embedding API 调用异常", e);
            throw ContainerException.LLM_EMBEDDING_ERROR.newInstance("Embedding 调用异常: " + e.getMessage());
        }
    }
}
