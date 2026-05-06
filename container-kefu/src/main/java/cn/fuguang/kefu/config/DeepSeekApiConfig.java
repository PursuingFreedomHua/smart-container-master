package cn.fuguang.kefu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DeepSeek API 配置类
 * 对应 Nacos 配置中的 deepseek 前缀
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekApiConfig {

    /** API Key */
    private String apiKey;

    /** API 基础地址 */
    private String baseUrl = "https://api.deepseek.com";

    /** Chat Completion 端点 */
    private String chatEndpoint = "/v1/chat/completions";

    /** Embedding 端点 */
    private String embeddingEndpoint = "/v1/embeddings";

    /** 对话模型名称 */
    private String chatModel = "deepseek-chat";

    /** Embedding 模型名称 */
    private String embeddingModel = "deepseek-chat";

    /** 连接超时（毫秒） */
    private Integer connectTimeout = 10000;

    /** 读取超时（毫秒），SSE 流式需要较长超时 */
    private Integer readTimeout = 120000;

    /** 温度参数 0-2 */
    private Double temperature = 0.7;

    /** 最大输出 token 数 */
    private Integer maxTokens = 2048;

    /** 向量维度 */
    private Integer embeddingDimension = 1024;

    /** 获取完整的 Chat API URL */
    public String getChatUrl() {
        return baseUrl + chatEndpoint;
    }

    /** 获取完整的 Embedding API URL */
    public String getEmbeddingUrl() {
        return baseUrl + embeddingEndpoint;
    }
}
