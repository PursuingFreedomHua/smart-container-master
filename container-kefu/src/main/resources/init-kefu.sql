-- ============================================
-- Smart Container 智能客服模块数据库初始化脚本
-- 数据库: smart_container
-- 兼容: MySQL 5.5+
-- ============================================

-- 1. 知识文档表
CREATE TABLE IF NOT EXISTS knowledge_document (
    id          BIGINT          AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    title       VARCHAR(255)    NOT NULL                COMMENT '文档标题',
    content     LONGTEXT        NOT NULL                COMMENT '原始文本内容',
    status      VARCHAR(20)     NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING-待向量化, VECTORIZED-已向量化, FAILED-失败',
    chunk_count INT             DEFAULT 0               COMMENT '切片数量',
    category    VARCHAR(100)                            COMMENT '分类: order/device/payment/account/other',
    remarks     VARCHAR(500)                            COMMENT '备注',
    create_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP       NULL,
    INDEX idx_status (status),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识文档表';

-- 2. 知识切片向量表
CREATE TABLE IF NOT EXISTS knowledge_chunk (
    id          BIGINT          AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    chunk_id    VARCHAR(64)     NOT NULL                COMMENT '切片唯一标识 UUID',
    document_id BIGINT          NOT NULL                COMMENT '关联文档ID',
    content     TEXT            NOT NULL                COMMENT '切片文本内容',
    vector_data MEDIUMTEXT      NOT NULL                COMMENT '向量数据 JSON float[1024]',
    metadata    VARCHAR(1000)                           COMMENT '元数据JSON: source/category',
    create_time TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_chunk_id (chunk_id),
    INDEX idx_document_id (document_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识切片向量表';

-- 3. Prompt 模板表
CREATE TABLE IF NOT EXISTS prompt_template (
    id            BIGINT        AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    template_code VARCHAR(100)  NOT NULL                COMMENT '模板编码: RAG_SYSTEM_PROMPT',
    template_name VARCHAR(200)                          COMMENT '模板名称',
    content       TEXT          NOT NULL                COMMENT '模板内容（支持 {{变量}}）',
    is_active     TINYINT       DEFAULT 1               COMMENT '是否启用: 1启用 0禁用',
    remarks       VARCHAR(500)                          COMMENT '备注',
    create_time   TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP     NULL,
    UNIQUE KEY uk_code (template_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Prompt 模板表';

-- 4. 插入默认 RAG 系统 Prompt 模板
INSERT IGNORE INTO prompt_template (template_code, template_name, content, is_active) VALUES
('RAG_SYSTEM_PROMPT', 'RAG客服系统提示词',
 '你是 Smart Container 智能货柜项目的客服助手。请根据以下参考资料回答用户问题。
如果参考资料不足以回答问题，请如实告知用户你无法回答，不要编造信息。

## 参考资料
{{knowledge_context}}

## 回答要求
1. 回答准确、简洁、友好
2. 中文回答
3. 涉及操作步骤时，请给出清晰的指引
4. 涉及订单问题时，提醒用户提供订单号以便进一步查询
5. 如果用户问题与货柜项目无关，礼貌地引导用户咨询相关问题',
 1);
