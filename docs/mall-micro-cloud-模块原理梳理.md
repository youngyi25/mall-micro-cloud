# mall-micro-cloud 微服务电商项目 · 模块原理与关系梳理

> 用途：简历主打项目的面试答辩参考文档。按"全局架构 → 基础设施层 → 业务服务层 → 模块关系 → 面试问答"组织。
>
> 技术栈定位：Spring Boot 3.3.5 + Spring Cloud 2023.0.1 + Spring Cloud Alibaba 2023.0.3.4 + Java 17，Maven 多模块。

---

## 一、项目总体架构

### 1.1 模块分层（三层架构）

```
┌─────────────────────────────────────────────────────────────┐
│ 表现层  mall-micro-web (Vue3 + axios 多页静态站, Nginx 托管)  │
│        ── 统一走 http://localhost:9000/api/**（网关）          │
├─────────────────────────────────────────────────────────────┤
│ 接入层  mall-gateway (9000)                                  │
│         路由 /api/{service}/** · 统一鉴权 · 限流 · CORS        │
├─────────────────────────────────────────────────────────────┤
│ 基础设施层                                                    │
│   mall-common │ mall-api                                     │
│   (统一响应/异常/JWT/加密/布隆) │ (11个 Feign 客户端 + DTO 契约) │
├─────────────────────────────────────────────────────────────┤
│ 业务服务层  mall-service/  (均为 Nacos 注册、共享 common+api)   │
│   9001 product  9002 user   9003 cart(MongoDB)   9004 order  │
│   9005 consumer 9006 es/pay 9007 seckill        9008 schedule│
│   9009 oss      9011 ai(未完成)  10001 test                  │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 服务端口对照

| 服务 | 端口 | 存储 |
|---|---|---|
| mall-gateway | 9000 | — |
| mall-product-service | 9001 | MySQL (shop_goods) |
| mall-user-service | 9002 | MySQL (shop_user) |
| mall-cart-service | 9003 | **MongoDB** |
| mall-order-service | 9004 | MySQL (shop_order) |
| mall-consumer-service | 9005 | Redis（无 DB） |
| mall-es-service | 9006 | Elasticsearch |
| mall-pay-service | 9006 | MySQL (shop_pay) |
| mall-seckill-service | 9007 | MySQL + Redis |
| mall-scheduler-service | 9008 | —（无 DB） |
| mall-oss-service | 9009 | 阿里云 OSS |
| mall-AISearch-service | 9011 | MySQL（骨架，未完成） |
| mall-test-service | 10001 | —（验证沙箱） |

> ⚠️ mall-es-service 与 mall-pay-service 端口均为 9006，同时启动会冲突。

### 1.3 中间件矩阵

| 中间件 | 用途 |
|---|---|
| Nacos | 注册中心 + 配置中心（各服务 DB/MQ/Sentinel 配置按 group 托管） |
| MySQL + Druid | product/user/order/seckill/pay 数据 |
| MongoDB | 购物车存储（cart 集合） |
| Redis | JWT token、黑名单、秒杀预扣库存、布隆过滤器、生产/消费端幂等锁、网关限流计数 |
| Redisson | 分布式锁（秒杀读-减临界区）、RBloomFilter 布隆 |
| RocketMQ | 异步消息（删购物车、支付改单、订单超时、Canal→ES 同步） |
| Elasticsearch | 商品搜索（IK 分词） |
| Seata | 全局分布式事务（仅下单链路一处） |
| ElasticJob | 定时任务（秒杀商品预热） |
| Sentinel | 限流/熔断/降级（网关 + 服务端） |
| 阿里云 OSS | 商品详情/秒杀静态页存储 |
| Thymeleaf | 服务端渲染静态页模板 |

---

## 二、核心基础设施层（面试必讲）

### 2.1 mall-common —— 一切服务的公共底座

**统一响应规范**（`Result<T>` + `ResultCodeEnum`）：
- 所有接口返回 `{code, msg, data}`。编码三段式：
  - HTTP 标准码 1~999：`SUCCESS(200)` / `PARAM_ERROR(400)` / `UNAUTHORIZED(401)` / `FORBIDDEN(403)` / `NOT_FOUND(404)` / `FAIL(500)`
  - 用户/登录/Token 4000~4999：`TOKEN_EXPIRED(4001)` / `PASSWORD_ERR(4004)` / `USER_EXIST(4005)` / `ACCOUNT_DISABLED(4003)`
  - 业务异常 5000~5999：如 `STOCK_SHORTAGE(5001)` 库存不足
- `GlobalResponseAdvice`（`ResponseBodyAdvice`）自动把非 `Result` 返回值包成 `Result.success()`，排除 OpenAPI/Knife4j 文档路径。
- **`String` 返回值特殊处理**：用 fastjson2 序列化，否则会被 Spring 的 StringConverter 绕过包装 —— 高频面试细节。
- `GlobalExceptionHandler` 三层捕获：`BusinessException`（携带业务 code）→ `@Valid` 参数校验异常 → 兜底 `Exception`。

**JWT 认证体系**（面试重点）：
- `JwtUtil`（HS256，jjwt）生成 token 后**同时写入 Redis**（`user:token:{userId}`，TTL 与 JWT 一致）——实现**无状态 + 可踢人/管控**的双通道。
- `validateTokenWithRedis` 四层校验：
  1. 黑名单检查（`blacklist:token:{token}`，登出/强制踢人时加入）
  2. JWT 签名/过期校验
  3. Redis 中 token 存在性
  4. 与 Redis 存储的 token 精确匹配（不匹配 = **其他设备登录，踢下线**）→ 支持单点登录
- 登出 = 删 Redis + token 进黑名单按剩余 TTL 过期。

**敏感数据安全**：
- `EncryptHandler`：MyBatis `BaseTypeHandler`，字段级透明 **AES 加解密**（如收货地址详细字段落库即加密）。
- `DesensitizeUtil`：姓名 / 手机号(`138****8000`) / 身份证 / 邮箱 / 银行卡 / 地址脱敏。

**缓存防穿透组件**：
- `CacheBloomFilter<T>`（Redisson `RBloomFilter`）：`add()` 加载合法 key，`mightContain()` 前置判断——**false 一定不存在直接拦截，避免无效请求打穿 Redis/DB**。秒杀场景在用。
- 配置：`cache.bloom.name` / `expectedInsertions` / `falseProbability`；Redis key 前缀 `cache:bloom:`。

**MQ 上下文透传**（核心难点）：
- `MqHeaderUtil`：RocketMQ 消费运行在线程池线程（无 Web 上下文），直接调 Feign 会失败。
- 生产端把当前请求头快照进 `MQ_HTTP_HEADER`；消费端 `buildMockRequestContext()` 重建 `MockHttpServletRequest` 绑定到消费线程，使 Feign 能读到上下文。
- **`finally` 必须 `clearContext()`** 防 ThreadLocal 泄漏串数据。

**MyBatis-Plus 增强**：
- `MetaObjectHandler` 自动填充 `createTime` / `updateTime` / `deleted(0)` / `status(0)`；
- `PaginationInnerInterceptor` 分页 + `IllegalSQLInnerInterceptor` 非法 SQL 拦截。

### 2.2 mall-api —— 服务间调用契约

- 集中定义 **11 个 Feign 客户端**，全部返回 `Result<T>`，DTO 按业务域放 `dto/` 包（`@Data + Serializable`）。
- `contextId` 采用"业务名-feign"命名（如 `skuInfo-feign`），避免同一目标服务多客户端 Bean 冲突。
- `FeignAutoScanConfig`：`@EnableFeignClients(basePackages="com.example.mall.api")` 全局统一扫描。
- `HeaderInterceptor`（Feign `RequestInterceptor`）：白名单（Authorization / X-User-Id / X-Gateway-Secret / X-Trace-Id）把请求头透传到下游，保证跨服务调用时用户上下文不丢；无 Web 上下文（MQ/定时任务场景）时自动跳过。
- 降级示范：`CategoryFeignClient` 同时声明 `fallback + FallbackFactory`。

**Feign 客户端清单**：

| 接口 | contextId | 目标服务 | 关键方法 |
|---|---|---|---|
| CartFeignClient | cart-feign | mall-cart-service | removeCart(ids) 批量删 |
| CategoryFeignClient | category-feign | mall-product-service | listLevelOne / listChildren（带降级） |
| EsSkuFeignClient | esSku-feign | mall-es-service | importAll / save / delete / update |
| SkuInfoEsFeignClient | skuInfo-es-feign | mall-es-service | save / delete / update |
| OrderFeignClient | order-feign | mall-order-service | remove(orderId) 关单 |
| OrderInfoFeignClient | order-info-feign | mall-order-service | update / getOrderStatus |
| OssFeignClient | oss-feign | mall-oss-service | uploadByte(字节数组) |
| AlipayFeignClient | alipay-feign | mall-pay-service | queryPaymentResult(orderId) |
| SkuInfoFeignClient | skuInfo-feign | mall-product-service | deductStock(map) / listAll |
| SeckillProductFeignClient | seckill-product-feign | mall-seckill-service | listTodaySeckillGoods / generateHtml / loadStockCache |
| ShippingAddressFeignClient | shipping-address-feign | mall-user-service | listById(userId) |

### 2.3 mall-gateway —— 流量统一入口

**路由**：
- 以 `/api/{service}/**` 前缀路由，`RewritePath`（product/user/test）或 `StripPrefix=2`（cart/order/pay/seckill/oss）剥前缀。
- 另有非 `/api` 的裸路径路由（`/product/**`、`/brand/**`、`/category/**` 等）直接转发 product-service。

**统一鉴权 `AuthGatewayFilterFactory`**（`Auth=` 指令，面试重点）：
1. **白名单放行**（AntPathMatcher 通配）：`/api/user/login`、`/api/user/register`、`/product/**` 等显式列出；**未列出路径均需 JWT**；OpenAPI 文档路径豁免。
2. **JWT 四层校验**（走 `JwtUtil.validateTokenWithRedis`）。
3. **注入下游头**：`X-User-Id`（下游用 `@RequestHeader("X-User-Id")` 取当前用户）+ `X-Gateway-Secret`（微服务互信密钥，目前校验已注释关闭）。
4. **无感知续期**：重新生成新 token 写入 Redis，响应前回写响应头 `Authorization` —— **客户端每次请求都要读取并复用新 token**（前端 request.js 响应拦截器正是干这个的）。

**限流（Redis 令牌桶 `RequestRateLimiter`）** 三种维度 KeyResolver：

| KeyResolver | 维度 | 状态 |
|---|---|---|
| ActivityLimitKeyResolver | 秒杀活动 ID（query 参数） | ✅ 当前启用，2000 QPS / 突发 5000 |
| UserLimitKeyResolver | JWT 解析用户 ID | 注释备用 |
| IpLimitKeyResolver | 客户端 IP | 注释备用 |

> 超出限流返回 HTTP 429。可与 Sentinel 共存：网关做粗粒度 IP/活动级，Sentinel 做细粒度资源级。

**其他组件**：
- `RtGlobalFilter`：请求耗时日志（去重 CORS 头防 "multiple values" 错误）。
- `GatewayExceptionHandler`（`@Order(-1)`）：**必须放网关模块**——`NoResourceFoundException` 是 WebFlux 类，不能放 Servlet 基的 mall-common；同时避免 /favicon.ico 等无路由命中刷错误日志。
- `CorsConfig`：跨域（暴露 Authorization 头，配合 token 续期）。

---

## 三、业务服务层（按业务域逐个拆解）

### 3.1 mall-user-service (9002) —— 用户与认证

- **注册**：jBcrypt `hashpw` 加盐哈希后落库（非明文），`@SentinelResource` 限流保护。
- **登录**：`BCrypt.checkpw` 校验 → `JwtUtil.login` 生成 token 写 Redis，**同时回写响应头 + 响应体**（双通道防 CORS 丢头）→ 前端存 sessionStorage。
- **登录日志**：`LoginLogAspect`（`@Login4j` 注解 + Spring AOP `@Around`），记录 IP/终端信息，成功失败都落 remark。
- **收货地址** `ShippingAddressServiceImpl.saveOrUpdate`：**当新地址设默认时，先把该用户所有其他地址 `default_status` 批量置 0**，保证全局唯一默认地址。查询时 `detailAddress` 经 `EncryptHandler` 解密、手机号经 `DesensitizeUtil` 脱敏、地址拼接省市区。
- **定位**：纯被调用方（被 test-service 通过 `ShippingAddressFeignClient` 调用），自身不主动调他人。

### 3.2 mall-product-service (9001) —— 商品中心

- **商品体系**：三级分类（`category` 表 `parent_id` 自引用树）→ 品牌（`brand` + 多对多 `category_brand`）→ **SPU**（标准商品，含销售属性 `sale_attribute`/值）→ **SKU**（具体售卖单元：价格 / 库存 `num` / 规格 / 图片 / 上下架 status）。
- **商品详情聚合**：`getBySpuId` 把 SPU + 销售属性 Map + 该 SPU 下所有 SKU 组装成 `ProductDetailDTO`。
- **分类/品牌查询**：一级查 `parent_id is null`，子级按 parentId，`listAll` 递归拼接三级树；品牌查询用 `.gt(Brand::getId, 0)` 绕过"禁全表扫描"插件校验。
- **库存扣减（下单核心）**：`SkuInfoServiceImpl.decreaseStock`（`@Transactional`）——先批量查 SKU，逐个判断 `num < deduct` 则抛 `STOCK_SHORTAGE`，充足则批量 `updateById`。**MySQL 乐观校验式扣减**，由订单服务 Feign 触发。
- **静态页缓存**：`PageServiceImpl` 用 Thymeleaf 渲染商品详情 HTML → Feign `OssFeignClient.uploadByte` 上传 OSS → URL 写回 `sku_info.html_url`（**静态化缓存**，页面直接访问静态页）。
- **注意**：product-service **本身无任何 Redis 商品缓存**——缓存/防穿透都下沉到了秒杀服务。

### 3.3 mall-cart-service (9003) —— 购物车（MongoDB）

- **为什么用 MongoDB**：购物车是"一用户多条 sku"、频繁增删改、无强事务、schema 灵活的非核心数据，文档模型天然契合，读写性能高。
- **数据结构**：`Cart` 文档，`_id = skuId-userId` **复合主键**——同一用户同一 SKU 天然唯一。
- **业务逻辑**：
  - 加购：先 `findById` 判断已存在，存在则数量累加（`quantity = old + add`）再 `save`；
  - 批量加购：先删旧再 `insertAll` 全量覆盖；
  - 分页：userId 精确 + skuName 正则 + price 范围过滤 + create_time 降序；
  - 删除：按 `_id` 删除（单文档操作本身原子）。
- **价格来源**：购物车存**下单时快照**（前端传入完整 Cart 含价格/图/名），不实时 Feign 拉产品价格——降低耦合、保证结算价稳定。

### 3.4 mall-order-service (9004) —— 订单 + Seata 全局事务

**下单主链路**（方法标注 `@GlobalTransactional`，全项目唯一 Seata 边界）：

```
1) 拆单      OrderCreateDTO → 订单头 OrderInfo + 明细 List<OrderItems> + cartIds
2) 扣库存    Feign SkuInfoFeignClient.deductStock(Map<skuId, 数量>)
             → product 侧扣 MySQL 库存；库存不足抛 STOCK_SHORTAGE → Seata 全局回滚
3) 生成订单号 MP IdWorker 雪花 ID（非 DB 自增）
4) 保存明细    amount = price × quantity，saveBatch
5) 保存订单头  累计 totalAmount
6) 异步删购物车 StreamBridge.send("deleteCart-out-0")
             （try-catch 容忍失败——删购物车不在全局事务强关联内，可日志补偿）
7) 返回支付参数 PayCreateModel，前端跳支付宝
```

- **订单状态机**：`0 待付款 → 1 待发货 → 2 已发货 → 3 已完成`，另有 `4 已关闭 / 5 无效`；支付方式 `0 未支付 / 1 支付宝 / 2 微信 / 3 银行卡`。
- **状态被动接收**（order 不主动改单）：
  - 支付成功 → MQ → consumer → Feign `OrderInfoFeignClient.update` 置 1（待发货）；
  - 超时未付 → 延迟消息 + 主动回查支付宝 → `OrderFeignClient.removeById` 关单。
- **订单超时**：已实现"RocketMQ 延迟消息(DELAY=5) + 主动回查支付宝"（`orderCheck-out-0`），但**触发点 `sendOrderCheckMsg` 尚未被下单链路调用**（能力存在、未接线）——面试诚实说明，可作改进点。

### 3.5 mall-seckill-service (9007) —— 秒杀（三层防超卖）

**秒杀全链路**（面试核心亮点）：

```
前端抢购
  → ① 网关活动级限流（RequestRateLimiter: 2000 QPS / 突发 5000, 按 activityId）
  → ② 秒杀服务 /product/decreaseStock
        ├─ 布隆 mightContain(stockKey) 拦截无效商品（防穿透, code 7003）
        ├─ Redisson tryLock("lock:seckill:{activityId}:{skuId}") 保护"读-减"临界区
        ├─ Redis 读 count > 0 → decrement 原子自减（不会扣到负数）
        └─ finally 持锁人释放锁
  → ③ 前端调 /order/seckill/create → 订单服务下单（复用普通下单链路）
```

- **三层防超卖的层次**：网关限流削峰 → Redisson 锁化"读-减"临界区 → Redis `decrement` 原子性 + 前置判空。
- **数据存储**：活动/商品在 MySQL（`seckill_activity` / `seckill_goods`），Redis 只存**库存预扣键** `seckill-{activityId}-{skuId}`（1 天过期）。
- **布隆过滤**：加载的正是这些库存 key（`cache:bloom:seckill-activity-bloom`，预期 10000，误判率 0.1），拦截无效商品查询防止打穿缓存。
- **库存预热**：`loadStockCache()` 把当天活动商品库存写入 Redis + 布隆，由定时服务 ElasticJob 任务经 Feign 触发。
- **静态页**：Thymeleaf 渲染秒杀商品页 → 上传 OSS / 本地写文件。

> ⚠️ **已知设计缺陷**（可主动讲，体现深度）：秒杀 Redis 预扣库存 与 成单时订单服务扣 MySQL 库存是**两套独立扣减**，中间无 RocketMQ 事务消息把"预扣"与"真实扣减"绑定——预扣成功但下单失败要靠补偿。秒杀服务**本身不直连 MQ**。

### 3.6 mall-pay-service (9006) —— 支付宝支付

- **只接支付宝**（alipay-sdk，RSA2 签名），微信支付仅注释有概念、无代码——**别吹有微信支付**。
- **发起**：`DefaultAlipayClient → AlipayTradePagePayRequest`（`product_code=FAST_INSTANT_TRADE_PAY`）→ `pageExecute` 返回表单，用户跳转支付宝。
- **回调**：同步 `return` 只做页面展示**不真正改单**；异步 `notify` 校验 `trade_status == TRADE_SUCCESS` → 落 `pay_log`（MySQL）→ **发 MQ 改单**。
- **改单走异步链路**（pay 不直连订单 DB）：`updateOrderOutput-out-0` → consumer `UpdateOrderHandler`（Redis `que:lock` 幂等）→ Feign `OrderInfoFeignClient.update` 改订单为待发货。
- **幂等双保险**：生产端 Redis `req:lock:{orderId}` 防重发 + 消费端 `que:lock:{orderId}` 防重处理 + 回查时 `pay_log` 已存在直接返回成功。
- ⚠️ `payNotify` 未做支付宝签名验签（仅判 `trade_status`），是安全弱点。

### 3.7 mall-consumer-service (9005) —— 异步消费中枢

Spring Cloud Stream **函数式模型**：`Consumer<Message<String>>` Bean，函数名即 binding 名。

**四大处理器**：

| Bean | 绑定通道 | 职责 |
|---|---|---|
| UpdateOrderHandler | updateOrderStatusInput | 支付成功后改订单状态 |
| DeleteCartHandler | deleteCartInput | 下单后删购物车 |
| OrderPaymentCallbackVerifyHandler | orderCheckInput | 订单超时回查支付结果 |
| SyncDataToEsHandler | syncDataInput | Canal 监听 binlog → 增量同步 ES |

**统一消费套路**（面试重点模板）：
1. 从消息头取 `TRANSACTION_ID` → Redis `setIfAbsent("que:lock:{key}", 1, 30s)` **幂等去重**（未抢到直接 return，同消息只处理一次）；
2. `MqHeaderUtil.buildMockRequestContext()` 在线程池重建 HTTP 上下文，让 Feign 可用；
3. `finally { clearContext(); }` 清理 ThreadLocal 防串；
4. 异常抛 `RuntimeException` → Stream 自动重试，达 `max-attempts` 进死信。

### 3.8 mall-es-service (9006) —— 商品搜索

- **索引**：Spring Data Elasticsearch 自动建 `sku_info` index；`sku_name` 用 **IK 分词**（`ik_max_word` 索引 / `ik_smart` 搜索）。
- **搜索**（`/product/page`）：`BoolQuery` 组合——`match` 关键词 + `term` 分类/品牌精确 + `range` 价格区间 → 分页 + **高亮**（`HighlightQuery` 对 skuName 红色高亮）→ `FetchSourceFilter` 限字段返回。
- **数据同步双通道**：
  - **增量**：**Canal 监听 MySQL `sku_info` binlog → MQ → consumer `SyncDataToEsHandler`** 按 INSERT/UPDATE/DELETE 走 `SkuInfoEsFeignClient.save/update/delete`（Redis 幂等 + `MqHeaderUtil` 上下文重建）；
  - **全量**：`/search/importAll` 用 `SkuInfoFeignClient.listAll()` 拉取 product-service 全量 SKU → `saveAll()`。
- product-service 本身不主动调 ES Feign，靠 Canal/MQ 解耦同步。

### 3.9 mall-scheduler-service (9008) —— ElasticJob 定时调度

- **用 ShardingSphere-ElasticJob**（不是 `@Scheduled` / xxl-job），任务类实现 `SimpleJob.execute(ShardingContext)`，cron 配置在 Nacos（SCHEDULER_GROUP）。
- **核心任务 `LoadSeckillProductTask`**：通过 `SeckillProductFeignClient` 依次调用秒杀服务 `listTodaySeckillGoods → generateHtml（生成静态页）→ loadStockCache（预热库存到 Redis + 布隆）`——把 MySQL 库存预置到 Redis，是秒杀高峰性能的关键前置。
- 服务本身无 Controller，不连 DB，纯"定时驱动 + Feign 调下游"。

### 3.10 mall-oss-service (9009) —— 阿里云 OSS

- **AK 直传**（非 STS），`OssConfig` 用 AK/SK + endpoint 构建 client。
- **接口**：
  - 上传 / 下载（`Content-Disposition: attachment`）/ 删除；
  - `signUrl`：**预签名 URL**（30 分钟）供私有文件临时访问；
  - `uploadByte`：**字节数组直传**，供秒杀/商品静态页 HTML 用（`text/html;charset=UTF-8`）。
- **objectKey** = `prefix/{dir}/{yyyy/MM/dd}/UUID+后缀`，内容安全前缀 `temp`，上传限 10MB/50MB。

### 3.11 mall-test-service (10001) —— 技术验证沙箱

定位：**验证基础设施各组件是否可用**的沙箱（非业务服务），不连 DB。覆盖：
- **Sentinel**：`@SentinelResource` 限流/熔断/降级演示；`ProductSentinelHandler` 区分 `FlowException`（限流 429）与 `DegradeException`（熔断）返回不同文案；慢调用 `Thread.sleep(800)` 超过 500ms 阈值触发降级。
- **RocketMQ**：批量消息（`ROCKET_TAGS`）、**顺序消息**（`ORDERLY_KEY` 分区键 + `OrderPartitionKeyExtractor` 实现 `PartitionKeyExtractorStrategy`）、生产端幂等（`req:lock`）。
- **Redis**：String / Hash 操作、`setIfAbsent` 分布式锁。
- **Feign**：跨服务调用 + `HeaderInterceptor` 请求头透传验证。

### 3.12 mall-AISearch-service (9011) —— AI 智能搜索与推荐（langchain4j + Redis 向量库）

**定位**：基于 **langchain4j 1.19.0** + **Redis Stack（向量检索）** 实现两大能力：**自然语言商品搜索**（语义理解）和 **AI 个性化推荐**。与 mall-es-service（IK 分词全文搜索）互补——ES 做精确关键词匹配，AI 服务做语义理解。

> 当前状态：pom 已声明 langchain4j + langchain4j-open-ai 依赖，端口 9011 已预留，**待实现完整业务代码**。

#### 3.12.1 整体架构

```
┌─────────────┐     ┌──────────────┐     ┌───────────────────┐
│   前端用户   │────▶│  mall-gateway │────▶│  mall-AISearch    │
│  搜索栏/推荐 │     │  /api/ai/**   │     │  (9011)           │
└─────────────┘     └──────────────┘     └────────┬──────────┘
                                                   │
                          ┌────────────────────────┼────────────────────────┐
                          ▼                        ▼                        ▼
                   ┌─────────────┐         ┌──────────────┐        ┌──────────────┐
                   │  Redis Stack │         │  langchain4j │        │  大模型 API   │
                   │ (向量+全文索引)│         │  AiService   │        │ (OpenAI兼容) │
                   └─────────────┘         └──────────────┘        └──────────────┘
```

#### 3.12.2 自然语言搜索（RAG 检索增强）

**核心流程**：

```
用户输入："200元以内的无线蓝牙耳机，适合运动"
    │
    ▼
┌──────────────────────────────────────┐
│ 1. Embedding：用户 Query 转向量       │
│    langchain4j EmbeddingModel        │
│    (text-embedding-3-small / BGE)    │
└──────────────┬───────────────────────┘
               ▼
┌──────────────────────────────────────┐
│ 2. 向量检索：Redis KNN 搜索           │
│    Top-K=20 候选商品（余弦相似度）     │
└──────────────┬───────────────────────┘
               ▼
┌──────────────────────────────────────┐
│ 3. 混合检索（可选）：                  │
│    α * vectorScore + (1-α) * BM25    │
│    向量语义 + 关键词精确 加权融合       │
└──────────────┬───────────────────────┘
               ▼
┌──────────────────────────────────────┐
│ 4. RAG 增强：Top-K 商品拼入 Prompt    │
│    LLM 生成自然语言推荐回复            │
│    （突出卖点、引导下单）              │
└──────────────────────────────────────┘
```

**langchain4j 核心组件**：

| 组件 | 类/接口 | 作用 |
|---|---|---|
| Embedding 模型 | `EmbeddingModel` | 文本 → float[] 向量（1536 维） |
| 向量存储 | `RedisEmbeddingStore`（langchain4j-community） | Redis Stack 向量索引读写 |
| 内容检索器 | `EmbeddingStoreContentRetriever` | KNN 检索 + 相似度阈值过滤 |
| AI 服务 | `AiServices.build(ProductAiService.class)` | 接口 + `@SystemMessage` / `@UserMessage` 自动生成实现 |
| LLM | `ChatLanguageModel` | 生成最终推荐文案 |

**AI 服务接口定义**：

```java
public interface ProductAiService {
    @SystemMessage("""
        你是商城智能导购助手。根据检索到的商品信息回答用户问题。
        回答要简洁、有吸引力，突出商品卖点。
        如果检索结果中没有合适商品，诚实告知用户。
        """)
    String recommend(@UserMessage String userQuery);
}
```

#### 3.12.3 商品向量化（Embedding）

**向量化文本模板**（拼接语义最丰富的字段）：

```java
// 最佳语义字段组合：skuName + skuAttribute + SpuInfo.description + brandName + categoryName
String embeddingText = String.format(
    "商品：%s。规格：%s。品牌：%s。分类：%s。售价：%s元。商品详情：%s",
    sku.getSkuName(),          // Text (ik_max_word) — 高语义
    sku.getSkuAttribute(),     // 商品规格描述 — 高语义
    sku.getBrandName(),        // 品牌 — 元数据
    sku.getCategoryName(),     // 分类 — 元数据
    sku.getPrice(),            // 价格 — 数值过滤
    spu.getDescription()       // SPU 描述 — 高语义（需联表）
);
```

**Redis Stack 向量索引创建**：

```bash
FT.CREATE idx:product ON JSON PREFIX 1 product: SCHEMA
    $.name AS name TEXT
    $.category AS category TAG
    $.brand AS brand TAG
    $.price AS price NUMERIC
    $.embedding AS embedding VECTOR FLAT 6
        TYPE FLOAT32
        DIM 1536
        DISTANCE_METRIC COSINE
```

#### 3.12.4 AI 个性化推荐

**用户画像构建**（行为加权平均）：

```java
// 用户兴趣向量 = 交互商品 embedding 的加权平均
// 权重：下单(1.0) > 加购(0.7) > 收藏(0.5) > 浏览(0.3)
public float[] buildUserEmbedding(Long userId) {
    List<UserBehavior> behaviors = getRecentBehaviors(userId, 30);
    return weightedAverageEmbeddings(behaviors);
}
```

**推荐流程**：

```
用户兴趣向量 ──▶ Redis KNN 查询 ──▶ Top-N 相似商品 ──▶ 过滤已购/库存 ──▶ 返回推荐
```

#### 3.12.5 数据同步策略

```
MySQL (sku_info 表变更)
    │
    ├──▶ 现有 RocketMQ ──▶ mall-es-service (IK 全文索引)     ← 保留
    │
    └──▶ 新增消费端 ──▶ mall-AISearch-service (向量索引)      ← 新增
```

| 同步方式 | 适用场景 | 延迟 |
|---|---|---|
| 定时全量同步 | 初次导入、兜底 | 分钟级 |
| MQ 实时同步 | 价格变动、上下架 | 秒级（复用现有 RocketMQ） |
| 双写 | 新建/编辑商品时 | 实时 |

#### 3.12.6 与 ES 搜索的分工对比

| 能力 | ES (mall-es-service) | Redis 向量 (mall-AISearch-service) |
|---|---|---|
| 关键词精确搜 | ✅ IK 分词 | ❌ |
| 价格/品牌过滤 | ✅ 聚合 | ✅ 元数据过滤 |
| 自然语言理解 | ❌ | ✅ 语义匹配 |
| "适合运动的耳机" | ❌ 搜不到 | ✅ 能理解 |
| 高亮显示 | ✅ | ❌ |

**建议**：前端搜索栏可同时调用两者，结果融合排序（ES 精确匹配权重高 + 语义匹配补充）。

#### 3.12.7 依赖配置

```xml
<!-- mall-AISearch-service/pom.xml -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j</artifactId>
    <version>1.19.0</version>  <!-- 已声明 -->
</dependency>
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>  <!-- OpenAI 兼容，已声明 -->
    <version>1.19.0</version>
</dependency>
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-community</artifactId>  <!-- 原 langchain4j-redis 已停发 -->
    <version>1.19.0</version>
</dependency>
```

> ⚠️ Redis 需升级到 **Redis Stack**（带 RediSearch 模块），原版 Redis 不支持向量检索。与现有 Lettuce + Redisson 共存。

#### 3.12.8 落地阶段规划

| 阶段 | 内容 | 预期效果 |
|---|---|---|
| **P0** | 商品数据批量 Embedding → Redis Stack 索引 | 向量检索基础 |
| **P1** | langchain4j 集成 + 向量搜索 API | 能用自然语言搜商品 |
| **P2** | RAG 增强（LLM 生成推荐文案） | 对话式搜索体验 |
| **P3** | 用户画像 + 个性化推荐 | "猜你喜欢" 智能化 |

---

## 四、模块间关系全景

### 4.1 依赖关系

```
所有业务服务 ──依赖──> mall-common（公共底座：Result/异常/JWT/加密/布隆/MqHeaderUtil）
                   + mall-api（Feign 契约 + DTO）
mall-gateway / consumer / es / scheduler / test ── 也 scanBasePackages="com.example.mall"
```

### 4.2 Feign 调用关系图（谁调谁）

```
test     → user  (ShippingAddressFeignClient : 收货地址)
test     → product (CategoryFeignClient : 分类)
order    → product (SkuInfoFeignClient.deductStock : 扣库存)
consumer → es   (SkuInfoEsFeignClient save/update/delete : 增量同步)
es       → product (SkuInfoFeignClient.listAll : 全量导入)
es       → product (CategoryFeignClient : 分类)   [备用]
ai       → product (SkuInfoFeignClient.listAll : 全量导入向量库)
scheduler→ seckill (SeckillProductFeignClient : 预热)
product  → oss   (OssFeignClient.uploadByte : 静态页上传)
seckill  → oss   (OssFeignClient.uploadByte : 秒杀静态页上传)
consumer → order (OrderInfoFeignClient.update : 改单状态; OrderFeignClient.removeById : 关单)
consumer → cart  (CartFeignClient.removeCart : 删购物车)
consumer → pay   (AlipayFeignClient.queryPaymentResult : 回查支付结果)
```

### 4.3 三条核心业务链路（面试闭眼能画）

**① 完整下单链路**：

```
前端 cart.html
  → 网关(统一鉴权 + X-User-Id 注入)
  → order /order/create  (@GlobalTransactional)
      ├─ Feign 扣 product/MySQL 库存（失败 → Seata 全局回滚 订单+明细+库存）
      ├─ 本地保存订单头 + 明细（雪花ID）
      └─ MQ deleteCart → consumer(que:lock 幂等 + 上下文重建)
              → Feign 删 cart/MongoDB
前端跳支付宝
  → pay 回调 → 落 pay_log → MQ updateOrderOutput
  → consumer UpdateOrderHandler → Feign 改 order 状态 = 待发货
```

**② 秒杀链路**：

```
ElasticJob(定时) → Feign 预热秒杀(MySQL → Redis 库存 + 布隆 + 静态页)
前端抢购 → 网关活动级限流(2000qps/突发5000, 按 activityId)
  → seckill /product/decreaseStock
      → 布隆拦截 → Redisson 锁 → Redis decrement 原子扣减
前端调 /order/seckill/create → 复用下单链路成单
```

**③ 搜索链路**：

```
全量: es /search/importAll → Feign 拉 product 全量 SKU → 写入 ES 索引
增量: 商品表 binlog → Canal → MQ → consumer → Feign 增量同步到 ES
查询: 前端 search.html → 网关 → es /product/page
      → BoolQuery(IK 分词 match + term 过滤 + range 价格) → 高亮 → 分页
```

**④ AI 智能搜索链路**：

```
全量: ai /search/importAll → Feign 拉 product 全量 SKU → Embedding → 写入 Redis 向量索引
增量: 商品表 binlog → Canal → MQ → consumer → Feign 增量同步到 Redis 向量库
查询: 前端搜索栏 → 网关 → ai /api/ai/search?query=xxx
      → Query Embedding → Redis KNN 向量检索 → RAG 增强 → LLM 生成推荐文案
推荐: 用户行为(浏览/收藏/加购/下单) → 画像向量 → KNN 推荐相似商品
```

### 4.4 认证链路

```
前端请求携带 Authorization(JWT)
  → 网关 AuthGatewayFilterFactory
      白名单命中? 放行 : JWT 四层校验(黑名单/签名/Redis 存在/单点匹配)
  → 注入 X-User-Id / X-Gateway-Secret
  → 无感知续期：新 token 回写响应头 + 更新 Redis
  → 跨服务 Feign 靠 HeaderInterceptor 白名单透传
  → 下游 @RequestHeader("X-User-Id") 取当前用户
```

---

## 五、面试问答提醒

### 5.1 应突出的深度点

1. **网关统一鉴权 + JWT/Redis 双通道**：无状态签名 + 可单点/踢人/黑名单管控 + 无感知续期。
2. **Seata 全局事务边界把握**：只包"下单 + 扣库存"，删购物车/支付故意用 MQ 解耦（一致性与可用性的取舍）。
3. **秒杀三层防超卖**：限流（网关 2000qps）→ Redisson 锁 → Redis 原子自减 + 前置判空；布隆防穿透；ElasticJob 预热。
4. **MQ 消费健壮性**：函数式 Stream、`que:lock` SETNX 幂等、`MqHeaderUtil` 线程池上下文重建与 `clearContext` 防泄漏、失败重试进死信。
5. **技术选型合理性**：购物车为何用 MongoDB；ES 为何 IK 分词 + Canal 解耦增量；`String` 返回值包装特判；GatewayExceptionHandler 为什么必须放网关。
6. **AI 搜索与 ES 的互补设计**：精确关键词用 ES（IK 分词），自然语言语义用 Redis 向量库（KNN 检索）；RAG 增强让 LLM 基于检索结果生成推荐文案，避免幻觉。

### 5.2 诚实处理的已知缺陷（面试官问"有什么坑"时主动说，加分）

- 秒杀 Redis 预扣与 MySQL 真实扣减是**两套库存**，无事务绑定，需补偿；
- 秒杀库存扣减"先 get 再 decrement"存在窗口（虽有 Redisson 锁保护），非纯原子；
- 订单超时延迟消息**未接线**到下单链路；
- `payNotify` 缺支付宝签名验签（安全弱点）；
- `EncryptHandler` 第二个 `getNullableResult` 条件疑似反了；
- es/pay 端口 9006 冲突；`deleted` 字段不是 `@TableLogic` 逻辑删除；
- AISearch 服务已完成架构设计，**待实现完整业务代码**（langchain4j + Redis 向量库 + RAG 增强）。

---

## 附：核心源码文件速查

| 关键点 | 文件 |
|---|---|
| 统一响应/异常 | `mall-common/.../vo/Result.java`、`advice/GlobalResponseAdvice.java`、`advice/GlobalExceptionHandler.java`、`constant/ResultCodeEnum.java` |
| JWT/拦截器 | `mall-common/.../util/JwtUtil.java`、`interceptor/LoginInterceptor.java`、`interceptor/AuthorizationInterceptor.java` |
| 加解密/脱敏 | `mall-common/.../util/EncryptHandler.java`、`util/DesensitizeUtil.java` |
| 布隆过滤器 | `mall-common/.../filter/CacheBloomFilter.java` |
| MQ 上下文 | `mall-common/.../util/MqHeaderUtil.java` |
| Feign 契约 | `mall-api/.../api/<domain>/feign/*.java`、`common/config/FeignAutoScanConfig.java`、`common/interceptor/HeaderInterceptor.java` |
| 网关鉴权/限流 | `mall-gateway/fillter/AuthGatewayFilterFactory.java`、`ActivityLimitKeyResolver.java`、`advice/GatewayExceptionHandler.java` |
| 下单链路 | `mall-order-service/.../service/impl/OrderServiceImpl.java`、`MessageSendServiceImpl.java` |
| 秒杀链路 | `mall-seckill-service/.../service/impl/ProductServiceImpl.java` |
| 消费端 | `mall-consumer-service/.../config/UpdateOrderHandler.java`、`DeleteCartHandler.java`、`OrderPaymentCallbackVerifyHandler.java`、`service/SyncDataToEsHandler.java` |
| ES 搜索 | `mall-es-service/.../service/impl/ProductServiceImpl.java`、`SkuInfoServiceImpl.java` |
| 定时任务 | `mall-scheduler-service/.../scheduler/job/LoadSeckillProductTask.java` |
| OSS | `mall-oss-service/.../oss/util/OssUtil.java` |
| AI 搜索 | `mall-AISearch-service/.../config/AiSearchConfig.java`、`service/ProductAiService.java`、`controller/AiSearchController.java`（待实现） |
| 前端网关对接 | `mall-micro-web/js/request.js`（baseURL=localhost:9000/api、Token 读写） |