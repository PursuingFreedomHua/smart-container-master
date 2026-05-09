# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

# Smart Container 项目配置
## 删除文件的注意事项
禁止批量删除文件或目录。不要使用：
- 'del /s'	
- 'rd /s'
- 'rmdir /s'
- 'Remove-Item -Recurse'
- 'rm-rf'
需要删除文件时，只能一次删除一个明确路径的文件。
正确示例：
Remove-Item "C:\path\to\file.txt"
如果需要批量删除文件，应停止操作，并向用户请求，让用户手动删除。

## 语言要求
- 所有自然语言回复使用中文
- 技术术语保留英文（如 Spring Boot、Netty、Redis、LangChain、LangGraph、RAG、LLM 等）
- 生成代码时必须带中文注释
- 代码注释必须使用中文
- 代码中的变量名、类名使用英文（遵循 Java 命名规范）

## 项目概述
Smart Container 是一个基于 Spring Cloud Alibaba 的智能货柜/售货机 IoT 管理系统，支持设备管理、订单处理、支付集成、IoT设备通信等功能。

## 技术栈
- 核心框架：Java 8 + Spring Boot 2.7.7 + Spring Cloud 2021.0.5 + Spring Cloud Alibaba 2021.0.4.0
- 服务治理：Nacos（服务发现 + 配置中心）
- 数据层：MyBatis 2.2.2 + MySQL 8.0 + Druid 连接池
- 缓存：Redis + Redisson（分布式锁）
- 消息队列：RocketMQ
- IoT通信：Netty TCP Server（支持益诺、可耐两种硬件协议）
- 支付集成：支付宝 SDK 4.39.70.ALL、微信支付
- 工具库：Hutool、FastJSON2、Lombok

## 常用命令

### 构建
- `mvn clean install` - 构建所有模块
- `mvn clean install -DskipTests` - 构建所有模块（跳过测试）
- `mvn clean install -pl container-order -am` - 构建指定模块及其依赖
- `mvn clean package` - 打包

### 运行
- `mvn spring-boot:run -pl container-order` - 启动订单服务（端口 9960）
- `mvn spring-boot:run -pl container-account` - 启动账户服务（端口 9961）
- `mvn spring-boot:run -pl container-channel` - 启动支付渠道服务（端口 9962）
- `mvn spring-boot:run -pl container-communicate` - 启动IoT通信服务（端口 9963）
- `mvn spring-boot:run -pl container-device` - 启动设备服务（端口 9964）
- `mvn spring-boot:run -pl container-manager` - 启动管理服务（端口 9965）

### 测试
- `mvn test` - 运行所有测试
- `mvn test -pl container-order` - 运行指定模块测试
- `mvn test -Dtest=ClassName` - 运行指定测试类

### 数据库
- MyBatis XML映射文件位于各模块的 `src/main/resources/mapper/` 目录
- 实体类位于 `container-common-core` 模块

## 代码风格

### 分层架构
每个服务遵循：Controller → Biz → Service → Mapper
- Controller：REST API 接口
- Biz：复杂业务流程编排（跨服务调用）
- Service：领域业务逻辑
- Mapper：MyBatis 数据访问

### DTO/VO 模式
- Entity：数据库实体，位于 container-common-core
- DTO：Feign 接口传输对象
- VO：Controller 层请求/响应对象
- BO：业务处理对象

### 命名规范
- 包名：`cn.fuguang.{模块名}`
- 实体类：`XxxEntity`
- Feign 接口：`XxxFeignService`
- 响应类：`BaseResult<T>` 或 `BaseResponse<T>`

### API 响应格式
```json
{
  "code": "000000",
  "message": "success",
  "data": { ... }
}
```
成功码：`000000`，错误码：`999999`

## 目录结构

smart-container-master/
├── container-api/                  # Feign 客户端接口
│   └── src/main/java/cn/fuguang/api/
├── container-common/               # 公共模块
│   ├── container-common-core/      # 核心实体、枚举、常量、异常
│   ├── container-common-redis/     # Redis 服务
│   └── container-common-rocketmq/  # RocketMQ 服务
├── container-account/              # 账户服务（端口 9961）
├── container-channel/              # 支付渠道服务（端口 9962）
├── container-communicate/          # IoT 通信服务（端口 9963）
├── container-device/               # 设备管理服务（端口 9964）
├── container-manager/              # 管理服务（端口 9965）
├── container-order/                # 订单服务（端口 9960）
│   ├── src/main/java/cn/fuguang/order/
│   │   ├── controller/             # REST 控制器
│   │   ├── biz/                    # 业务编排层
│   │   ├── service/                # 服务层
│   │   └── mapper/                 # MyBatis 接口
│   └── src/main/resources/
│       ├── mapper/                 # MyBatis XML
│       └── bootstrap.yml           # Nacos 配置
└── pom.xml                         # 父 POM

## 开发规范

### 服务间通信
- 同步调用：使用 OpenFeign，接口定义在 container-api 模块
- 异步调用：使用 RocketMQ，通过 container-common-rocketmq

### 分布式锁
- 使用 Redisson，示例：
```java
RLock lock = redissonClient.getLock("device:" + deviceId);
try {
    if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
        // 业务逻辑
    }
} finally {
    lock.unlock();
}
```

### 异常处理
- 使用 `ContainerException` 抛出业务异常
- 预定义错误码：参数错误(100001)、未支付订单(100002)、黑名单(100003)、设备状态错误(100005)、Redis锁错误(100006)、远程调用错误(100008)等

### 订单状态机
订单状态通过 `OrderStatusEnum` 枚举管理，包含 `nextStatus()` 方法控制状态流转。

## 注意事项

### 基础设施依赖
启动服务前需确保以下服务可用：
- Nacos：服务发现和配置中心（地址在 bootstrap.yml）
- MySQL：主数据库
- Redis：缓存和分布式锁
- RocketMQ：消息队列

### 配置管理
- 服务配置通过 Nacos 配置中心管理
- 本地配置在 `bootstrap.yml`（服务名、端口、Nacos地址）
- 不要将敏感信息提交到代码库

### IoT 设备协议
- container-communicate 服务支持益诺(YiNuo)和可耐(KeNai)两种硬件供应商
- 使用策略模式处理不同协议的消息

### 数据库操作
- 所有 SQL 写在 MyBatis XML 文件中
- Mapper 接口只定义方法签名
- 复杂查询使用 XML 中的动态 SQL

## 常见问题

### Q: 启动报错 "Unable to connect to Nacos Server"
A: 检查 bootstrap.yml 中的 Nacos 地址是否正确，确保 Nacos 服务已启动

### Q: Feign 调用失败
A: 检查目标服务是否已注册到 Nacos，检查 container-api 中的接口定义是否正确

### Q: Redis 连接失败
A: 检查 Redis 服务是否启动，检查 container-common-redis 模块的配置

### Q: RocketMQ 消息发送失败
A: 检查 RocketMQ 服务状态，检查 container-common-rocketmq 模块的配置
