# 🌾 助农商品平台（mall-micro-cloud）

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-green)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.1-blue)](https://spring.io/projects/spring-cloud)
[![Spring Cloud Alibaba](https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2023.0.3.4-orange)](https://github.com/alibaba/spring-cloud-alibaba)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-6.0+-red)](https://redis.io/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

> 基于 Spring Cloud 微服务架构的助农电商解决方案，打通农产品从田间到餐桌的绿色通道。

---

## 📖 项目简介

助农商品平台致力于解决农产品"卖难买贵"问题，通过微服务架构构建高效、稳定、可扩展的农产品电商生态。平台连接农户与消费者，提供农产品展示、限时抢购、在线支付、物流跟踪等全流程服务，让优质农产品直达千家万户。

### 核心定位

- **助农兴农**：帮助农户直接对接消费者，减少中间环节，提升农民收入
- **品质保障**：产地溯源、品质把控，让消费者买到放心的农产品
- **技术驱动**：微服务架构支撑高并发场景，秒杀活动助力滞销农产品快速分销

---

## 🏗️ 技术架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        前端展示层                                  │
│              Vue 3 / 微信小程序 / H5 移动端                        │
└──────────────────────────────┬──────────────────────────────────┘
                               │ HTTP / HTTPS
┌──────────────────────────────▼──────────────────────────────────┐
│                    API 网关层 (9000)                               │
│     鉴权 │ 限流 │ 路由 │ 熔断 │ 灰度发布 │ 请求日志               │
└──────────────────────────────┬──────────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────────┐
│                    微服务层 (Spring Cloud)                         │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐  │
│  │ 商品服务 │ │ 用户服务 │ │ 订单服务 │ │ 秒杀服务 │ │ 支付服务 │  │
│  │  9001   │ │  9002   │ │  9004   │ │  9007   │ │  9006   │  │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘  │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐  │
│  │ 购物车  │ │ 搜索服务 │ │ 消息消费 │ │ 定时任务 │ │ OSS服务 │  │
│  │  9003   │ │  9006   │ │  9005   │ │  9008   │ │  9009   │  │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘  │
└──────────────────────────────┬──────────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────────┐
│                      基础设施层                                    │
│   Nacos │ Sentinel │ Seata │ RocketMQ │ MySQL │ Redis │ ES      │
└─────────────────────────────────────────────────────────────────┘
```

### 技术选型

| 领域 | 技术选型 | 说明 |
|------|----------|------|
| 应用框架 | Spring Boot 3.3.5 | 基础开发框架 |
| 微服务框架 | Spring Cloud 2023.0.1 | 微服务全家桶 |
| 微服务治理 | Spring Cloud Alibaba 2023.0.3.4 | Nacos/Sentinel/Seata |
| 注册中心 | Nacos | 服务注册发现 + 配置中心 |
| 流量管控 | Sentinel | 资源级流控、熔断降级 |
| 网关 | Spring Cloud Gateway | WebFlux 响应式网关 |
| 服务调用 | OpenFeign | 声明式 HTTP 客户端 |
| 负载均衡 | Spring Cloud LoadBalancer | 客户端负载均衡 |
| 分布式事务 | Seata | AT 模式，保证数据一致性 |
| 消息队列 | RocketMQ | 异步解耦、削峰填谷 |
| ORM | MyBatis Plus 3.5.15 | 数据持久层 |
| 关系数据库 | MySQL 8.0 + Druid | 业务数据存储 |
| 文档数据库 | MongoDB | 购物车数据存储 |
| 缓存 | Redis + Redisson | 缓存、分布式锁、布隆过滤器 |
| 搜索引擎 | Elasticsearch | 商品全文检索 |
| 对象存储 | 阿里云 OSS | 农产品图片存储 |
| 认证鉴权 | JWT (jjwt 0.11.5) | 无状态认证 |
| 接口文档 | Knife4j 4.5.0 | OpenAPI 3 文档 |
| 前端 | Vue 3 | 用户端界面 |

---

## 🚀 核心功能

### 🌱 农户/商家端

| 功能 | 说明 |
|------|------|
| 商品管理 | 农产品上架、编辑、分类管理、库存维护 |
| 品牌管理 | 农产品品牌建设，提升产品附加值 |
| 产地溯源 | 商品详情展示产地信息、农户故事 |
| 秒杀活动 | 设置限时特价活动，快速分销滞销农产品 |
| 订单处理 | 接单、发货、物流跟踪 |

### 🛒 消费者端

| 功能 | 说明 |
|------|------|
| 商品浏览 | 分类检索、关键词搜索、产地筛选、品牌筛选 |
| 商品详情 | 图文详情、规格参数、用户评价、产地信息 |
| 秒杀抢购 | 限时秒杀活动，多层限流保护，公平抢购 |
| 购物车 | 加入购物车、批量结算、离线添加（MongoDB） |
| 在线支付 | 对接支付宝，支持沙箱环境测试 |
| 订单管理 | 下单、支付、发货、收货、售后全流程 |
| 收货地址 | 多地址管理、默认地址设置 |

### ⚙️ 平台基础能力

| 功能 | 说明 |
|------|------|
| 统一认证 | JWT + Redis，登录、注册、单点登录 |
| 网关鉴权 | 白名单放行，Token 无感知续期 |
| 多级限流 | IP 级 → 用户级 → 活动级，Redis 令牌桶 |
| 分布式事务 | 下单扣库存，Seata 保证一致性 |
| 异步消息 | RocketMQ 解耦，幂等消费 |
| 定时任务 | 订单超时取消、库存自动回滚 |
| 文件上传 | 阿里云 OSS 图片管理 |

---

## 📁 项目结构

```
mall-micro-cloud/
├── pom.xml                          # 父 POM，全局依赖管理
├── mall-common/                     # 公共模块
│   ├── config/                      # 公共配置类
│   ├── filter/                      # 布隆过滤器
│   ├── handler/                     # 全局异常处理、加密处理器
│   ├── interceptor/                 # 登录拦截器
│   ├── advice/                      # 全局响应包装
│   ├── util/                        # JWT、加密、脱敏等工具类
│   └── vo/                          # 统一响应、结果枚举
├── mall-api/                        # Feign 客户端接口 + DTO
│   ├── category/                    # 分类 Feign
│   ├── product/                     # 商品 Feign
│   ├── user/                        # 用户 Feign
│   ├── cart/                        # 购物车 Feign
│   ├── order/                       # 订单 Feign
│   ├── pay/                         # 支付 Feign
│   ├── seckill/                     # 秒杀 Feign
│   ├── es/                          # 搜索 Feign
│   └── oss/                         # OSS Feign
├── mall-gateway/                    # API 网关（端口 9000）
│   ├── filter/                      # 限流 KeyResolver
│   └── handler/                     # 网关异常处理
├── mall-service/                    # 微服务父 POM
│   ├── mall-product-service/        # 商品服务（9001）
│   ├── mall-user-service/           # 用户服务（9002）
│   ├── mall-cart-service/           # 购物车服务（9003，MongoDB）
│   ├── mall-order-service/          # 订单服务（9004，Seata）
│   ├── mall-consumer-service/       # 消息消费者（9005，RocketMQ）
│   ├── mall-es-service/             # 搜索服务（9006，Elasticsearch）
│   ├── mall-pay-service/            # 支付服务（9006）
│   ├── mall-seckill-service/        # 秒杀服务（9007）
│   ├── mall-scheduler-service/      # 定时任务服务（9008）
│   ├── mall-oss-service/            # OSS 文件服务（9009）
│   └── mall-test-service/           # 测试服务（10001）
├── code-generator/                  # MyBatis Plus 代码生成器
└── mall-micro-web/                  # Vue 3 前端项目
```

---

## 🖥️ 服务端口

| 服务 | 端口 | 数据库 | 说明 |
|------|------|--------|------|
| mall-gateway | 9000 | - | API 网关，统一入口 |
| mall-product-service | 9001 | MySQL | 商品分类、品牌、SPU/SKU 管理 |
| mall-user-service | 9002 | MySQL | 用户注册登录、收货地址 |
| mall-cart-service | 9003 | MongoDB | 购物车，文档模型灵活存储 |
| mall-order-service | 9004 | MySQL | 订单管理，Seata 分布式事务 |
| mall-consumer-service | 9005 | - | RocketMQ 消息消费 |
| mall-es-service | 9006 | ES | 商品全文检索 |
| mall-pay-service | 9006 | MySQL | 支付服务 |
| mall-seckill-service | 9007 | MySQL | 秒杀活动，布隆过滤 + 限流 |
| mall-scheduler-service | 9008 | - | 定时任务，订单超时处理 |
| mall-oss-service | 9009 | - | 阿里云 OSS 文件上传下载 |
| mall-test-service | 10001 | - | 测试演示服务 |

> ⚠️ **注意**：mall-es-service 与 mall-pay-service 端口均为 9006，同时启动需修改其中一个。

---

## 🛠️ 快速开始

### 环境要求

| 组件 | 版本 |
|------|------|
| JDK | 17+ |
| Maven | 3.8+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| Nacos | 2.x |
| RocketMQ | 5.x |
| Elasticsearch | 8.x（可选） |

### 构建运行

```bash
# 1. 克隆项目
git clone https://github.com/youngyi25/mall-micro-cloud.git
cd mall-micro-cloud

# 2. 构建整个项目（跳过测试）
mvn clean install -DskipTests

# 3. 构建单个服务
cd mall-service/mall-seckill-service && mvn clean package -DskipTests

# 4. 运行服务
mvn spring-boot:run

# 5. 或直接运行 jar（注意：可执行 jar 带 -exec 后缀）
java -jar target/mall-seckill-service-1.0-SNAPSHOT-exec.jar
```

### 启动顺序

```mermaid
graph LR
    A[MySQL/Redis/Nacos] --> B[RocketMQ/ES]
    B --> C[mall-gateway]
    C --> D[各微服务]
    D --> E[前端访问 9000]
```

1. 启动基础设施：MySQL、Redis、Nacos、RocketMQ、Elasticsearch
2. 在 Nacos 配置中心导入各服务配置
3. 启动 `mall-gateway`（网关）
4. 启动各微服务（顺序无关，建议先启动 product/user）
5. 前端通过 `http://localhost:9000/api` 访问后端

---

## 🔒 安全机制

### 认证鉴权

```
请求 → 网关 → 白名单匹配 → JWT + Redis 校验 → 解析用户信息 → 注入请求头 → 下游服务
                                        ↓
                              无感知续期（新 Token 回写响应头）
```

- **JWT 统一认证**：HS256 签名，Redis 存储 Token 状态，支持黑名单强制下线
- **网关鉴权**：白名单路径放行，其余请求校验 JWT 合法性，解析 claims 注入请求头透传
- **Token 续期**：每次校验重新生成 JWT 回写响应头，客户端无感知刷新

### 流量管控

| 层级 | 维度 | 算法 | 配置 |
|------|------|------|------|
| 第一层 | IP 级 | Redis 令牌桶 | 全局默认 |
| 第二层 | 用户级 | Redis 令牌桶 | 按 JWT 用户 ID |
| 第三层 | 活动级 | Redis 令牌桶 | 2000 QPS，突发 5000 |

- **Sentinel**：资源级细粒度流控、熔断降级、热点参数限流
- **Gateway 限流**：粗粒度 IP/活动级限流，超量返回 429

### 数据安全

- **敏感字段加密**：AES 自动加解密（MyBatis Plus TypeHandler）
- **数据脱敏**：姓名、手机号、身份证、邮箱、银行卡、地址自动脱敏展示
- **密码加密**：jBcrypt 哈希存储

---

## 💡 技术亮点

### 1. 秒杀高并发防护

```
请求 → 布隆过滤器 → 活动级限流 → 用户级限流 → Redis 锁扣库存 → MQ 异步下单
          ↓               ↓             ↓
       不存在直接拦截   超量返回 429   防重复抢购
```

- **布隆过滤器**：Redisson RBloomFilter，预期 10000 元素，防缓存穿透
- **多级限流**：活动级（2000 QPS）+ 用户级双层防护
- **分布式锁**：Redisson RLock 保证库存扣减原子性
- **MQ 削峰**：秒杀请求先入队，异步处理订单

### 2. 分布式事务

```
@GlobalTransactional
下单流程：
  生成订单号 → 保存订单明细 → Feign 扣库存 → Feign 删购物车
      ↓              ↓              ↓              ↓
   任一环节异常 → 全局回滚
```

- **Seata AT 模式**：对业务侵入小，自动补偿回滚
- **幂等保障**：订单号唯一，重复请求不会重复下单

### 3. 消息队列可靠性

- **顺序消费**：按订单 ID 分区键，同一订单消息有序
- **幂等消费**：生产端 Redis 锁防重发 + 消费端 Redis 去重
- **上下文透传**：解决 MQ 线程池无 Web 上下文导致 Feign 调用失败问题

### 4. 统一响应与异常处理

```json
{
  "code": 200,
  "msg": "success",
  "data": { claude... }
}
```

| 异常类型 | 处理方式 |
|----------|----------|
| BusinessException | 返回业务错误码 + 提示信息 |
| 参数校验异常 | 返回 400 + 校验失败详情 |
| 兜底 Exception | 返回 500 + 通用错误提示 |

---

## 📊 监控与治理

| 工具 | 用途 |
|------|------|
| Sentinel | 实时监控、流量控制、熔断降级 |
| Nacos | 服务注册发现、配置中心、动态刷新 |
| Knife4j | OpenAPI 3 接口文档，网关聚合查看 |
| Druid | SQL 监控、慢查询分析 |

---

## 🔗 核心流程时序

### 秒杀下单流程

```
用户          网关          秒杀服务         MQ         订单服务        商品服务
 │             │              │             │            │              │
 │──秒杀请求──▶│              │             │            │              │
 │             │──限流校验───▶│             │            │              │
 │             │              │──布隆过滤──▶│            │              │
 │             │              │──分布式锁──▶│            │              │
 │             │              │──入队MQ────▶│            │              │
 │             │              │             │──消费─────▶│              │
 │             │              │             │            │──Feign 扣库存▶│
 │             │              │             │            │◀──成功───────│
 │             │              │             │            │──保存订单───▶│
 │◀──结果─────│◀─────────────│◀────────────│◀───────────│              │
```

---

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 📝 License

本项目基于 [MIT](LICENSE) 协议开源。

---

## 👥 联系

如有问题或建议，欢迎提交 Issue 或联系作者。

---

> 🌾 **让每一份农产品都值得被看见** — 助农商品平台，连接田间与餐桌。
