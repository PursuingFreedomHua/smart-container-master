# Smart Container 项目技能定义

本文件定义了 Smart Container 项目的自定义技能，可通过 `/技能名` 方式调用。

## 技能列表

### 1. 构建与运行

- `/build [模块名]` — 构建指定模块或全部模块
  - `/build` → `mvn clean install -DskipTests`
  - `/build container-order` → `mvn clean install -pl container-order -am -DskipTests`
- `/run [模块名]` — 启动指定微服务
  - `/run container-order` → 启动订单服务（端口 9960）
  - `/run container-account` → 启动账户服务（端口 9961）
  - `/run container-channel` → 启动支付渠道服务（端口 9962）
  - `/run container-communicate` → 启动 IoT 通信服务（端口 9963）
  - `/run container-device` → 启动设备服务（端口 9964）
  - `/run container-manager` → 启动管理服务（端口 9965）
- `/test [模块名]` — 运行测试
  - `/test` → `mvn test`
  - `/test container-order` → `mvn test -pl container-order`

### 2. 代码生成

- `/new-module <模块名> <端口>` — 创建新的微服务模块，包含完整的 Controller → Biz → Service → Mapper 分层结构
- `/new-api <Feign接口名>` — 在 container-api 模块创建新的 Feign 客户端接口和 DTO
- `/new-entity <实体名>` — 在 container-common-core 创建新的数据库实体类
- `/new-mapper <Mapper名>` — 创建 MyBatis Mapper 接口和对应的 XML 映射文件

### 3. 前端转换

- `/convert-to-wechat` — 将 React+Tailwind CSS 代码转换为微信原生小程序代码（调用 react-to-wechat-converter agent）
  - 输入：React 组件源代码路径
  - 输出：完整的 WXML/WXSS/JS 小程序项目文件

### 4. 代码审查

- `/review` — 对当前分支的改动进行代码审查，检查：
  - 分层架构是否符合 Controller → Biz → Service → Mapper 规范
  - API 响应格式是否使用 BaseResult<T> 统一格式
  - 分布式锁使用是否正确
  - 异常处理是否使用 ContainerException
  - SQL 是否写在 MyBatis XML 中
  - 中文注释是否完整

### 5. 项目导航

- `/explain <主题>` — 讲解项目的技术选型、架构设计或模块职责（调用 project-mentor agent）

## 模块端口速查

| 模块 | 端口 | 说明 |
|------|------|------|
| container-order | 9960 | 订单服务 |
| container-account | 9961 | 账户服务 |
| container-channel | 9962 | 支付渠道服务 |
| container-communicate | 9963 | IoT 通信服务 |
| container-device | 9964 | 设备管理服务 |
| container-manager | 9965 | 管理服务 |

## 分层架构速查

```
Controller → Biz → Service → Mapper
   REST      编排     领域     数据
   API       逻辑     逻辑     访问
```

## API 响应格式

```json
{
  "code": "000000",
  "message": "success",
  "data": { }
}
```

- 成功码：`000000`
- 错误码：`999999`

## 常用错误码

| 错误码 | 说明 |
|--------|------|
| 100001 | 参数错误 |
| 100002 | 未支付订单 |
| 100003 | 黑名单 |
| 100005 | 设备状态错误 |
| 100006 | Redis 锁错误 |
| 100008 | 远程调用错误 |

## Agent 资源

本项目在 `.claude/agents/` 下配置了专用 Agent：

- **react-to-wechat-converter** — React 转微信小程序专家
- **project-mentor** — 项目技术导师
