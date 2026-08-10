package com.example.mall.fillter;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * IP 限流 KeyResolver — 以客户端 IP 作为限流维度。
 * 配合 Sentinel 网关流控规则（QPS/令牌桶），实现按 IP 独立限流。
 *
 * 使用方式：在 Nacos 或 Sentinel 控制台为路由配置流控规则，
 * grade=1（QPS），count=阈值，框架自动以 ipKeyResolver 的返回值作为限流 key。
 */
@Configuration
public class IpLimitKeyResolver {

    @Bean
    @Primary
    public KeyResolver ipLimitKeyResolver() {
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();
            InetSocketAddress remoteAddress = request.getRemoteAddress();
            String ip = (remoteAddress != null) ? remoteAddress.getHostString() : "unknown";
            return Mono.just(ip);
        };
    }
}
