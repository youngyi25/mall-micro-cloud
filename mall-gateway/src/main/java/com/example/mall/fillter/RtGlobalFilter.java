package com.example.mall.fillter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import org.springframework.http.HttpHeaders;

@Component
@Slf4j
public class RtGlobalFilter implements GlobalFilter, Ordered {
    /**
     * 过滤器执行顺序，数值越小优先级越高
     * 设置为 0，在所有过滤器中优先执行（记录开始时间）
     */
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //  1. 获取请求信息
        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethod().name();           // 请求方法：GET/POST/PUT/DELETE
        URI uri = request.getURI();                           // 请求 URI
        String path = request.getPath().value();              // 请求路径（不包含参数）
        String query = request.getURI().getQuery();           // 请求参数


        // 组装日志信息（避免暴露完整 URL 中的敏感参数）
        String requestInfo = String.format("%s %s%s", method, path,
                (query != null && !query.isEmpty()) ? "?" + query : "");

        // 2. 记录开始时间
        long startTime = System.currentTimeMillis();
        log.info("=== 请求开始 ===> [{}] [{}]", requestInfo, startTime);

        // 3. 执行后续过滤器链，并记录结束日志
        return chain.filter(exchange)
                .then(Mono.<Void>fromRunnable(() -> {
                    // 去除重复的 CORS 头，避免浏览器报 "multiple values" 错误
                    HttpHeaders headers = exchange.getResponse().getHeaders();
                    String[] corsHeaders = {
                        "Access-Control-Allow-Origin",
                        "Access-Control-Allow-Methods",
                        "Access-Control-Allow-Headers",
                        "Access-Control-Expose-Headers",
                        "Access-Control-Allow-Credentials",
                        "Access-Control-Max-Age"
                    };
                    for (String corsHeader : corsHeaders) {
                        List<String> values = headers.get(corsHeader);
                        if (values != null && values.size() > 1) {
                            headers.put(corsHeader, List.of(values.get(0)));
                        }
                    }
                }))
                .doFinally(signalType -> {
                    // ========== 4. 计算耗时 ==========
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;

                    // ========== 5. 获取响应状态码 ==========
                    HttpStatusCode statusCode = exchange.getResponse().getStatusCode();

                    // ========== 6. 打印结束日志 ==========
                    // 根据响应状态码决定日志级别：4xx/5xx 使用 warn 级别，2xx/3xx 使用 info 级别
                    if (statusCode != null && (statusCode.is4xxClientError() || statusCode.is5xxServerError())) {
                        log.warn("<=== 请求结束 [{}] [{}] 状态码: {} 耗时: {}ms",
                                requestInfo, endTime, statusCode.value(), duration);
                    } else {
                        log.info("<=== 请求结束 [{}] [{}] 状态码: {} 耗时: {}ms",
                                requestInfo, endTime,
                                statusCode != null ? statusCode.value() : "UNKNOWN",
                                duration);
                    }
                });
    }
}
