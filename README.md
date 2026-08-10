# 🌾 助农商品平台（mall-micro-cloud）

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-green)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.1-blue)](https://spring.io/projects/spring-cloud)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

> 基于 Spring Cloud 微服务架构的助农电商解决方案，打通农产品从田间到消费者的绿色通道。

---

## 📖 项目简介

助农商品平台致力于解决农产品"卖难买贵"问题，通过微服务架构构建高效、稳定、可扩展的农产品电商生态。平台连接农户与消费者，提供商品展示、秒杀抢购、在线支付、物流跟踪等全流程服务，让优质农产品直达千家万户。

---

## 🏗️ 技术架构

```
┌─────────────────────────────────────────────────────┐
│                  前端 (Vue 3)                        │
│             mall-micro-web / 小程序                  │
└──────────────────────┬──────────────────────────────┘
                       │ HTTP
┌──────────────────────▼──────────────────────────────┐
│              API 网关 (9000)                          │
│    鉴权 │ 限流 │ 路由 │ 布隆过滤 │ 负载均衡         │
└──────────────────────┬──────────────────────────────┘
                       │
    ┌─────────┬────────┼────────┬─────────┐
    ▼         ▼        ▼        ▼         ▼
┌───────┐ ┌───────┐ ┌───────┐ ┌───────┐ ┌───────┐
│ 商品  │ │ 用户  │ │ 订单  │ │ 秒杀  │ │ 支付  │
│ 9001  │ │ 9002  │ │ 9004  │ │ 9007  │ │ 9006  │
└───────┘ └───────┘ └───────┘ └───────┘ └───────┘
┌───────┐ ┌───────┐ ┌───────┐ ┌─────────────┐
│购物车 │ │ 搜索  │ │  OSS  │ │  定时任务   │
│ 9003  │ │ 9006  │ │ 9009  │ │   9008      │
└───────┘ └───────┘ └───────┘ └─────────────┘
```

### 技术栈

| 领域 | 技术选型 |
|------|----------|
| 框架 | Spring Boot 3.3.5 + Spring Cloud 2023.0.1 |
| 注册/配置中心 | Nacos `192.168.150.101:8848` |
| 流量管控 | Sentinel + Gateway 令牌桶限流 |
| 网关 | Spring Cloud Gateway（WebFlux 响应式） |
| 服务调用 | OpenFeign + Spring Cloud LoadBalancer |
| 分布式事务 | Seata（`@GlobalTransactional`） |
| 消息队列 | RocketMQ（Spring Cloud Stream） |
| ORM | MyBatis Plus 3.5.15 |
| 数据库 | MySQL 8.0.30 + Druid |
| 缓存 | Redis + Redisson（分布式锁/布隆过滤器） |
| 搜索 | Elasticsearch |
| 对象存储 | 阿里云 OSS |
| 认证 | JWT（HS256）+ Redis 黑名单 |
| 文档 | Knife4j 4.5.0（OpenAPI 3） |

---

## 🚀 核心功能

### 🌱 农户端
- **商品管理**：农产品上架、分类、库存管理
- **产地溯源**：商品详情展示产地、农户信息
- **批量秒杀**：设置限时特价活动，快速出货

### 🛒 消费者端
- **商品浏览**：分类检索、关键词搜索、产地筛选
- **秒杀抢购**：限时秒杀活动，三层限流保护（IP/用户/活动级）
- **购物车**：MongoDB 存储，支持离线添加
- **在线支付**：集成支付宝，支持沙箱测试
- **订单管理**：订单状态跟踪、物流查询

### ⚙️ 平台能力
- **网关统一鉴权**：JWT + Redis，无感知续期
- **多级限流**：IP 级 → 用户级 → 活动级，Redis 令牌桶
- **布隆过滤器**：防缓存穿透，保护数据库
- **分布式事务**：下单 → 扣库存 → 删购物车，Seata 保证一致性
- **异步消息**：RocketMQ 解耦，幂等消费
- **静态页面**：Thymeleaf 生成秒杀商品页，减少服务端压力

---

## 📁 项目结构

```
mall-micro-cloud/
├── pom.xml                          # 父 POM，全局依赖管理
├── mall-common/                     # 共享工具、配置、响应包装、布隆过滤器
├── mall-api/                        # Feign 客户端接口 + DTO（服务间调用契约）
├── mall-gateway/                    # Spring Cloud Gateway 网关（端口 9000）
├── mall-service/                    # 微服务父 POM
│   ├── mall-product-service/        # 商品服务（9001）
│   ├── mall-user-service/           # 用户服务（9002）
│   ├── mall-cart-service/           # 购物车服务（9003，MongoDB）
│   ├── mall-order-service/          # 订单服务（9004，Seata）
│   ├── mall-consumer-service/       # 消息消费者服务（9005，RocketMQ）
│   ├── mall-es-service/             # 搜索服务（9006，Elasticsearch）
│   ├── mall-pay-service/            # 支付服务（9006）
│   ├── mall-seckill-service/        # 秒杀服务（9007）
│   ├── mall-scheduler-service/      # 定时任务服务（9008）
│   ├── mall-oss-service/            # 阿里云 OSS 文件服务（9009）
│   └── mall-test-service/           # 测试服务（10001）
├── code-generator/                  # MyBatis Plus 代码生成器
└── mall-micro-web/                  # Vue 3 前端
```

---

## 🖥️ 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| mall-gateway | 9000 | API 网关 |
| mall-product-service | 9001 | 商品服务 |
| mall-user-service | 9002 | 用户服务 |
| mall-cart-service | 9003 | 购物车服务 |
| mall-order-service | 9004 | 订单服务 |
| mall-consumer-service | 9005 | 消息消费者 |
| mall-es-service | 9006 | 搜索服务 |
| mall-pay-service | 9006 | 支付服务 |
| mall-seckill-service | 9007 | 秒杀服务 |
| mall-scheduler-service | 9008 | 定时任务 |
| mall-oss-service | 9009 | OSS 文件服务 |
| mall-test-service | 10001 | 测试服务 |

> ⚠️ mall-es-service 与 mall-pay-service 端口均为 9006，同时启动需修改其中一个。

---

## 🛠️ 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.x
- RocketMQ 5.x
- Elasticsearch 8.x（可选）

### 构建运行

```bash
# 1. 克隆项目
git clone https://github.com/youngyi25/mall-micro-cloude.git
cd mall-micro-cloud

# 2. 构建整个项目（跳过测试）
mvn clean install -DskipTests

# 3. 构建单个服务
cd mall-service/mall-seckill-service && mvn clean package -DskipTests

# 4. 运行服务
mvn spring-boot:run

# 5. 或直接运行 jar
java -jar target/mall-seckill-service-1.0-SNAPSHOT-exec.jar
```

### 启动顺序

1. 启动 MySQL、Redis、Nacos、RocketMQ、Elasticsearch
2. 确保 Nacos 配置中心已导入各服务配置
3. 启动 `mall-gateway`
4. 启动各微服务
5. 前端通过 `localhost:9000/api` 访问后端

---

## 🔒 安全机制

- **JWT 统一认证**：HS256 签名 + Redis 存储 + 黑名单机制
- **网关鉴权**：白名单放行 → JWT 校验 → 无感知续期
- **三级限流**：IP 级 → 用户级 → 活动级，令牌桶算法
- **敏感数据加密**：AES 自动加解密（MyBatis Plus TypeHandler）
- **数据脱敏**：姓名/手机号/身份证等自动脱敏

---

## 📊 监控与治理

- **Sentinel**：实时监控、流量控制、熔断降级
- **Nacos**：服务注册发现、配置中心、动态刷新
- **Knife4j**：OpenAPI 3 接口文档，网关聚合查看
- **RtGlobalFilter**：请求耗时监控、日志追踪

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
