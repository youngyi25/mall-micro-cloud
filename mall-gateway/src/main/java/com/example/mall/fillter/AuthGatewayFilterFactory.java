package com.example.mall.fillter;

import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.exception.BusinessException;
import com.example.mall.util.JwtUtil;
import com.example.mall.vo.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;

/**
 * 自定义 Gateway 认证过滤器工厂。
 * 使用方式（在 routes.filters 或 default-filters 中）：
 *   - Auth=pattern1,pattern2,...
 * 其中 pattern 为白名单路径（支持 Ant 风格），匹配则放行；其余路径需要校验 JWT。
 */
@Component
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthGatewayFilterFactory.Config> {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private JwtUtil jwtUtil;

    public AuthGatewayFilterFactory() {
        super(Config.class);
    }

    /**
     * 短格式参数顺序：patterns 在前，matchTrailingSlash 在后
     * 配置示例：Auth=/login,/user/register,true
     * matchTrailingSlash: true-匹配时忽略末尾斜杠
     */
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("patterns", "matchTrailingSlash");
    }

    /**
     * 短格式类型：列表参数 + 尾部布尔标记
     */
    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.GATHER_LIST_TAIL_FLAG;
    }

    /**
     * 创建并返回一个 GatewayFilter 实例，该实例包含认证过滤器的核心处理逻辑。
     * <p>
     * 该过滤器用于对请求进行统一的身份认证和授权校验，主要功能包括：
     * <ul>
     *     <li>白名单放行：对配置的公开路径（如登录、注册、文档等）直接放行，不进行 Token 校验</li>
     *     <li>Token 校验：从请求头中提取 Authorization Token，校验其有效性（是否为空、是否过期）</li>
     *     <li>用户信息提取：解析 Token 中的用户 ID 等信息，存入请求头传递给下游微服务</li>
     *     <li>Token 刷新：Token 有效时自动生成新 Token 放入响应头，实现无感知续期</li>
     * </ul>
     *
     * @param config 过滤器配置对象(自定义），包含白名单路径列表（patterns）和末尾斜杠匹配策略（matchTrailingSlash）
     * @return GatewayFilter 实例，由 Spring Cloud Gateway 过滤器链执行
     */
    @Override
    public GatewayFilter apply(Config config) {
        return new GatewayFilter() {
            /**
             * 过滤器的核心处理方法，对每个经过网关的请求执行认证逻辑。
             *
             * @param exchange 网关上下文对象，包含请求（ServerHttpRequest）、响应（ServerHttpResponse）及会话信息
             * @param chain    过滤器链，用于将请求传递给下一个过滤器或目标服务
             * @return Mono&lt;Void&gt; 响应式编程的返回结果，表示异步处理完成
             */
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                // 获取请求和响应对象
                ServerHttpRequest request = exchange.getRequest();
                ServerHttpResponse response = exchange.getResponse();
                // 获取当前请求的路径
                String path = request.getURI().getPath();

                //1. 放行
                // 1.1 白名单放行
                // 遍历配置的白名单路径，使用 AntPathMatcher 进行通配符匹配（支持 /**、/* 等模式）
                // 如果当前请求路径匹配任一白名单模式，则直接放行，不进行 Token 校验
                for (String pattern : config.getPatterns()) {
                    if (PATH_MATCHER.match(pattern, path)) {
                        return chain.filter(exchange);
                    }
                }
                // 1.2 放行接口文档
                if (path.contains("/v3/api-docs")
                        || path.contains("/swagger-ui")
                        || path.contains("/doc.html")) {
                    return chain.filter(exchange);
                }

                // 2. Token 校验
                // 从请求头中获取 Authorization 头
                String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                try {
                    jwtUtil.validateTokenWithRedis(token);
                } catch (BusinessException e) {
                    return writeErrorResponse(response, e.getCode(), e.getMessage());
                }

                // 3. 解析 Token，提取用户信息
                try {
                    // 3.1 从 Token 中解析用户信息
                    Claims claims = jwtUtil.parseToken(token);
                    String userId = claims.getSubject();
                    String username = (String) claims.get("username");
                    String email = (String) claims.get("email");

                    // 3.2 将用户信息传递给下游服务
                    // 构造新的请求对象，在保留所有原有请求头的基础上，添加用户 ID 头
                    // 下游微服务可通过 @RequestHeader("X-User-Id") 获取当前用户 ID
                    ServerHttpRequest newRequest = request.mutate()
                            .header("X-User-Id", userId)
                            .header("X-Gateway-Secret", "mall-micro-8080")
                            .build();
                    // 3.3 刷新 Token（无感知续期）
                    // 使用用户信息重新生成新 Token，延长有效时间
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", userId);
                    map.put("username", username);
                    map.put("email", email);
                    // 生成Token（无感知续期，同步更新 Redis）
                    String newToken = jwtUtil.createTokenAndStore(Long.parseLong(userId), claims);

                    // 执行后续过滤器链（使用新的请求对象），并在所有过滤器执行完毕后
                    // 通过 then(Mono.fromRunnable(...)) 在响应返回前将新 Token 放入响应头
                    return chain.filter(exchange.mutate().request(newRequest).build())
                            .then(Mono.fromRunnable(() -> {
                                // 在响应头中设置新 Token，客户端可将其保存用于后续请求
                                response.getHeaders().set(HttpHeaders.AUTHORIZATION, newToken);
                            }));

                } catch (Exception e) {
                    // Token 解析失败（格式错误、签名无效等），视为 Token 无效，返回过期错误
                    return writeErrorResponse(response, ResultCodeEnum.TOKEN_EXPIRED);
                }
            }


            private Mono<Void> writeErrorResponse(ServerHttpResponse response, int code, String message) {
                // 设置响应内容类型为 JSON
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                try {
                    // 将错误响应对象序列化为 JSON 字节数组
                    byte[] bytes = objectMapper.writeValueAsBytes(Result.fail(code, message));
                    // 将字节数组包装为 DataBuffer 并写入响应体
                    return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
                } catch (JsonProcessingException e) {
                    // 序列化失败时，返回一个错误流
                    return response.writeWith(Mono.error(new IOException("响应序列化失败", e)));
                }
            }

            private Mono<Void> writeErrorResponse(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
                return writeErrorResponse(response, resultCodeEnum.getCode(), resultCodeEnum.getMsg());
            }
        };
    }



    /**
     * 配置类
     */
    public static class Config {
        private List<String> patterns = new ArrayList<>();
        private boolean matchTrailingSlash = true;

        public List<String> getPatterns() {
            return patterns;
        }

        public Config setPatterns(List<String> patterns) {
            this.patterns = patterns;
            return this;
        }

        public boolean isMatchTrailingSlash() {
            return matchTrailingSlash;
        }

        public Config setMatchTrailingSlash(boolean matchTrailingSlash) {
            this.matchTrailingSlash = matchTrailingSlash;
            return this;
        }
    }
}