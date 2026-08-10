# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

商城微服务（mall-micro-cloud）— 基于 Spring Boot 3.3.5 + Spring Cloud 2023.0.1 + Spring Cloud Alibaba 2023.0.3.4 的电商微服务项目。Java 17，Maven 多模块，使用阿里云 Maven 镜像。

## 构建和运行

```bash
# 从根目录构建整个项目（跳过测试）
mvn clean install -DskipTests

# 构建特定服务模块
cd mall-service/mall-product-service && mvn clean package -DskipTests

# 运行单个服务
mvn spring-boot:run

# 直接运行打包后的 jar（注意：product pom 使用 <classifier>exec</classifier>，可执行 jar 带 -exec 后缀）
java -jar target/mall-product-service-1.0-SNAPSHOT-exec.jar

# 运行单个测试类
mvn test -pl mall-common -Dtest=ResultTest
```

## 项目结构

```
mall-micro-cloud/
├── pom.xml                          # 父 POM，全局依赖管理
├── mall-common/                     # 共享工具、配置、响应包装、拦截器
├── mall-api/                        # Feign 客户端接口 + DTO（服务间调用契约）
├── mall-gateway/                    # Spring Cloud Gateway 网关（端口 9000）
├── mall-service/                    # 微服务父 POM
│   ├── mall-product-service/        # 商品服务（9001）
│   ├── mall-user-service/           # 用户服务（9002）
│   ├── mall-cart-service/           # 购物车服务（9003，MongoDB）
│   ├── mall-order-service/          # 订单服务（9004，Seata 分布式事务）
│   ├── mall-consumer-service/       # 消息消费者服务（9005，RocketMQ）
│   ├── mall-es-service/             # 搜索服务（9006，Elasticsearch）
│   ├── mall-pay-service/            # 支付服务（9006）
│   ├── mall-seckill-service/        # 秒杀服务（9007）
│   ├── mall-scheduler-service/      # 定时任务服务（9008）
│   ├── mall-oss-service/            # 阿里云 OSS 文件服务（9009）
│   └── mall-test-service/           # 测试/演示服务（10001）
├── code-generator/                  # MyBatis Plus 代码生成器（独立模块）
└── mall-micro-web/                  # Vue 3 前端（独立，不在 Maven 构建中）
```

## 技术栈

| 领域 | 技术 |
|------|------|
| 框架 | Spring Boot 3.3.5、Spring Cloud 2023.0.1 |
| 注册/配置中心 | Nacos `192.168.150.101:8848` |
| 流量管控 | Sentinel（`@SentinelResource`、Feign Sentinel、`ProductSentinelHandler`）+ Gateway 内置 `RequestRateLimiter`（Redis 令牌桶） |
| 网关 | Spring Cloud Gateway（WebFlux 响应式） |
| 服务调用 | OpenFeign（mall-api 定义）+ Spring Cloud LoadBalancer |
| 分布式事务 | Seata（`@GlobalTransactional`） |
| 消息队列 | RocketMQ `192.168.150.101:9876`（Spring Cloud Stream 函数式模型） |
| ORM | MyBatis Plus 3.5.15 |
| 数据库 | MySQL 8.0.30 + Druid 1.2.1（product/user/order/seckill）、MongoDB（cart） |
| 缓存/存储 | Redis `192.168.150.101:6379` + Redisson（分布式锁、布隆过滤器） |
| 搜索 | Elasticsearch（mall-es-service） |
| 对象存储 | 阿里云 OSS 3.17.4 |
| 认证 | JWT（jjwt 0.11.5，HS256）+ Redis 存储 + 黑名单 |
| 密码加密 | jBcrypt 0.4 |
| 文档 | Knife4j 4.5.0（OpenAPI 3，网关聚合） |
| JSON | Fastjson2 2.0.52 |

## 服务端口

| 服务 | 端口 |
|------|------|
| mall-gateway | 9000 |
| mall-product-service | 9001 |
| mall-user-service | 9002 |
| mall-cart-service | 9003 |
| mall-order-service | 9004 |
| mall-consumer-service | 9005 |
| mall-es-service | 9006 |
| mall-pay-service | 9006 |
| mall-seckill-service | 9007 |
| mall-scheduler-service | 9008 |
| mall-oss-service | 9009 |
| mall-test-service | 10001 |

> ⚠️ mall-es-service 与 mall-pay-service 端口均为 9006，同时启动会冲突。

## 各服务应用类

| 服务 | 主类 |
|------|------|
| 网关 | `com.example.mall.GatewayApplication` |
| 商品 | `com.example.mall.product.ProductServiceApplication` |
| 用户 | `com.example.mall.user.UserServiceApplication` |
| 购物车 | `com.example.mall.CartServiceApplication` |
| 订单 | `com.example.mall.order.OrderServiceApplication` |
| 消费者 | `com.example.mall.consumer.ConsumerServiceApplication` |
| 搜索 | `com.example.mall.es.EsServiceApplication` |
| 支付 | `com.example.mall.pay.PayServiceApplication` |
| 秒杀 | `com.example.mall.seckill.SeckillServiceApplication` |
| 定时任务 | `com.example.mall.scheduler.SchedulerServiceApplication` |
| OSS | `com.example.mall.oss.OssServiceApplication` |
| 测试 | `com.example.mall.test.TestServiceApplication` |

## 核心架构模式

### 统一响应与异常（mall-common）

- `Result<T>`（`com.example.mall.vo.Result`）：`{code, msg, data}`，静态工厂 `success()`/`fail()`
- `GlobalResponseAdvice`：自动包装返回体为 `Result.success(body)`，排除已是 Result 类型和 OpenAPI 文档路径
- `ResultCodeEnum`：编码规范 — 200成功、4xx客户端、5xx服务、4000-4999用户/登录/Token、5000-5999业务（如 `STOCK_SHORTAGE(5001)`）
- `BusinessException`：携带 code 的业务异常
- `GlobalExceptionHandler`：`@RestControllerAdvice` 三层捕获（BusinessException → 参数校验 → 兜底 Exception）

### 认证与安全

- **网关统一鉴权**：`AuthGatewayFilterFactory`（`Auth=` 指令）— 白名单放行 → JWT+Redis 校验 → 解析 claims 注入 `X-User-Id`/`X-Gateway-Secret` → 无感知续期（新 Token 回写响应头）
- **微服务间互信**：`AuthorizationInterceptor` 校验 `X-Gateway-Secret: mall-micro-8080`（目前注释关闭）
- **JWT 工具**（`JwtUtil`）：创建/解析/验证、Redis 存储、黑名单、登录/登出/强制踢人
- **登录拦截器**（`LoginInterceptor`）：从请求头/参数取 Token 解析用户信息；`@Login4j` 为标记注解（暂未接入 AOP）
- **敏感字段**：`EncryptHandler`（MyBatis Plus BaseTypeHandler）AES 自动加解密
- **数据脱敏**：`DesensitizeUtil`（姓名/手机号/身份证/邮箱/银行卡/地址）

### 服务间通信

- **OpenFeign**：接口定义在 `mall-api`（按领域分包），DTO 同步定义在 `mall-api/.../dto/`
- **Header 透传**：`HeaderInterceptor`（Feign RequestInterceptor），白名单头通过 `header.alloweds` 配置
- **Feign 自动扫描**：`@EnableFeignClients(basePackages="com.example.mall.api")` 在 `FeignAutoScanConfig`
- **调用方消费模式**：`@Autowired` 注入接口 → 检查 `result.getCode()` 是否等于 `ResultCodeEnum.SUCCESS.getCode()`

### 异步消息（mall-consumer-service）

Spring Cloud Stream 函数式编程模型：
- 发送：`StreamBridge.send("binding-name", message)`
- 消费：`Consumer<Message<String>>` Bean，函数名即 binding 名
- 配置：`spring.cloud.function.definition` 用分号分隔多个函数
- 顺序消费：`OrderPartitionKeyExtractor` 实现 `PartitionKeyExtractorStrategy`
- 幂等：生产端 Redis 分布式锁（`req:lock:{key}`）+ 消费端 Redis 去重（`que:lock:{key}`）

### 分布式事务（mall-order-service）

`@GlobalTransactional`（Seata）— 下单流程：生成订单号 → 保存订单/明细 → Feign 远程扣库存 → Feign 远程删购物车

### 流量管控与限流（mall-gateway）

**网关内置限流（RequestRateLimiter — Redis 令牌桶）：**
- `IpLimitKeyResolver`：按客户端 IP 限流（`org.springframework.cloud.gateway.filter.ratelimit.KeyResolver`）
- `ActivityLimitKeyResolver`：按秒杀活动 ID 限流（从 query 参数取 `activityId`）
- `UserLimitKeyResolver`：按用户 ID 限流（从 JWT Token 解析）
- 当前秒杀路由启用了活动级别限流（2000 QPS，突发 5000），用户级/IP 级注释备用
- 超出返回 HTTP 429 Too Many Requests

**Sentinel 限流（可选）：**
- 网关已接入 `spring-cloud-starter-alibaba-sentinel`
- 可在 Sentinel 控制台/Nacos 配置更灵活的流控规则（QPS、并发数、热点参数等）
- 两套机制可共存：Gateway 内置做粗粒度 IP/活动级，Sentinel 做细粒度资源级

### 布隆过滤器（mall-common）

- `CacheBloomFilter<T>`（`mall-common/filter/`）：基于 Redisson `RBloomFilter`，防缓存穿透
- 配置：`cache.bloom.name`、`cache.bloom.expectedInsertions`、`cache.bloom.falseProbability`
- Redis key 前缀：`cache:bloom:`
- 使用：`add()`/`addAll()` 加载数据，`mightContain()` 校验（false = 一定不存在，直接拦截）
- 秒杀服务已配置：`seckill-activity-bloom`，预期 10000 元素，误判率 0.1

### MyBatis Plus 配置（mall-common）

- 自动填充：`createTime`/`updateTime`/`deleted`(0)/`status`(0)
- 分页拦截器 + 非法SQL拦截器
- 枚举处理：`MybatisEnumTypeHandler`
- 驼峰命名转换已开启

## 网关路由规则

所有前端请求经 `/api/{service}/**` 前缀路由到对应服务，`RewritePath` 或 `StripPrefix` 去掉前缀：

| 路径前缀 | 目标服务 | 鉴权白名单 |
|----------|----------|-----------|
| `/api/product/**` | mall-product-service | 大部分放行（含 `/api/product/**`） |
| `/category/**`, `/brand/**`, `/product/**` 等 | mall-product-service | 同上（无 /api 前缀的直连路由） |
| `/api/user/**` | mall-user-service | 仅 `/api/user/login`, `/api/user/register` |
| `/api/cart/**` | mall-cart-service | 需鉴权 |
| `/api/order/**` | mall-order-service | 需鉴权 |
| `/api/pay/**` | mall-pay-service | 大部分放行 |
| `/api/seckill/**` | mall-seckill-service | 列表/详情/页面放行 |
| `/api/test/**` | mall-test-service | 仅 `/api/test/connect/**`, `/api/aliyun/oss/**` |
| `/api/aliyun/oss/**` | mall-oss-service | — |
| `/doc.html`, `/v3/api-docs/**`, `/swagger-ui/**`, `/webjars/**` | mall-product-service | — |

> 每个路由的 `Auth=` 指令显式列出白名单路径，**未列出的路径均需 JWT 鉴权**。

## Feign 客户端清单

| 接口 | 文件路径 | 目标服务 | contextId |
|------|----------|----------|-----------|
| `CategoryFeignClient` | `api/category/feign/` | mall-product-service | `category-feign` |
| `SkuInfoFeignClient` | `api/product/feign/` | mall-product-service | `skuInfo-feign` |
| `ShippingAddressFeignClient` | `api/user/feign/` | mall-user-service | `shipping-address-feign` |
| `CartFeignClient` | `api/cart/feign/` | mall-cart-service | `cart-feign` |
| `OrderFeignClient` | `api/order/feign/` | mall-order-service | `order-feign` |
| `OrderInfoFeignClient` | `api/order/feign/` | mall-order-service | `order-info-feign` |
| `AlipayFeignClient` | `api/pay/feign/` | mall-pay-service | `alipay-feign` |
| `OssFeignClient` | `api/oss/` | mall-oss-service | `oss-feign` |
| `SkuInfoEsFeignClient` | `api/es/feign/` | mall-es-service | `skuInfo-es-feign` |
| `EsSkuFeignClient` | `api/es/feign/` | mall-es-service | `esSku-feign` |
| `SeckillProductFeignClient` | `api/seckill/feign/` | mall-seckill-service | `seckill-product-feign` |

> 新增 Feign 接口：放在 `mall-api/.../api/<domain>/feign/`，DTO 放 `mall-api/.../api/<domain>/dto/`，返回 `Result<T>`，使用 `@Data` + `Serializable`。

## 网关限流 KeyResolver 清单

| 类名 | Bean 名 | 限流维度 | 配置位置 |
|------|---------|----------|----------|
| `IpLimitKeyResolver` | `ipLimitKeyResolver` | 客户端 IP | `mall-gateway/fillter/` |
| `ActivityLimitKeyResolver` | `activityKeyResolver` | 活动 ID（query 参数） | `mall-gateway/fillter/` |
| `UserLimitKeyResolver` | `userKeyResolver` | 用户 ID（JWT Token） | `mall-gateway/fillter/` |

> 当前秒杀路由启用活动级限流（2000 QPS / 突发 5000），用户级/IP 级在 `application.yml` 中注释备用。

## 配置管理

- 各服务基础配置在本地 `application.yml`（应用名、端口、Redis、Nacos 地址）
- 数据源、MyBatis、Sentinel、JWT 等详细配置托管在 **Nacos 配置中心**（按 group 区分，如 `PRODUCT_GROUP`、`USER_GROUP`、`ORDER_GROUP`、`CART_GROUP`、`SEATA_GROUP`）
- 多环境通过 `spring.profiles.active`（dev/prod）切换 Nacos namespace
- Nacos 配置通过 `spring.config.import` 导入，格式：`optional:nacos:{data-id}?group={group}&refreshEnabled=true`

## 开发提示

### 数据库

MySQL 连接信息在 Nacos 配置中心。开发环境直连示例（shop_user 库）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://192.168.150.101:3306/shop_user
    username: root
    password: root
```

### 前端

`mall-micro-web/` 是独立的 Vue 3 前端项目（不在 Maven 构建中）：
- 页面：login、register、index、cart、order、address、seckill 等
- API 配置：`js/common.js`、`js/request.js`
- 通过网关 `localhost:9000/api` 访问后端

### 运行顺序

1. 启动 MySQL、Redis、Nacos、RocketMQ、Elasticsearch
2. 确保 Nacos 配置中心已导入各服务配置
3. 启动 `mall-gateway`
4. 启动各微服务
5. 前端直接打开 HTML 或通过 nginx 静态服务

### 易踩坑点

- **Feign 路径匹配**：Feign 接口的 `@GetMapping` 路径必须匹配下游 controller 的原始路径（不含 `/api/{svc}` 前缀，网关已 strip/rewrite）
- **MQ 上下文传递**：消费者运行在线程池线程（无 Web 上下文），调用 Feign 会失败。生产端通过 `MQ_HTTP_HEADER` 头快照 HTTP 头，消费端通过 `MqHeaderUtil.buildMockRequestContext()` 重建，**finally 必须 `clearContext()`** 避免 ThreadLocal 泄漏
- **网关每次请求刷新 Token**：`AuthGatewayFilterFactory` 每次校验都重新生成 JWT 并回写响应头 `Authorization`，客户端需读取并复用新 Token
- **网关异常处理**：`GatewayExceptionHandler`（`@Order(-1)`）必须放在网关模块内，因为 `NoResourceFoundException` 是 WebFlux 类，不能放在 Servlet 基的 `mall-common`
- **String 返回值**：`GlobalResponseAdvice` 对 `String` 返回值特殊处理（用 fastjson2 序列化），否则 Spring 的 StringConverter 会绕过包装

### 已知问题

- 秒杀布隆过滤器误判率 0.1（10%）偏高，生产环境建议改为 0.01
- `AuthorizationInterceptor` 和 `WebConfig` 的拦截器注册均被注释（微服务间密钥校验关闭，便于测试）
- `EncryptHandler` 第二个 `getNullableResult` 逻辑疑似 bug（`isNotEmpty` 时返回 null，条件可能反了）
- `EncryptHandler` AES 密钥硬编码（`"BgKLBcLdAgZK7s64"`），未外部化配置
- `mall-es-service` 与 `mall-pay-service` 端口冲突（均为 9006），同时启动需修改其中一个
- 秒杀服务内部 DTO 字段 `secki11Price` 为拼写错误（应为 `seckillPrice`），Feign DTO 已修正
- 秒杀库存扣减非原子操作（先 get 再 decrement），高并发下存在竞态条件
- `deleted` 字段是普通 Integer，**不是** MyBatis Plus 的 `@TableLogic` 逻辑删除标记
