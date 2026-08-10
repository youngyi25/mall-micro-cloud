package com.example.mall.fillter;

import com.example.mall.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * 用户级别限流 KeyResolver — 从 JWT Token 中解析用户 ID 作为限流维度。
 * 配合 RequestRateLimiter，限制每个用户的 QPS。
 */
@Configuration
public class UserLimitKeyResolver {

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String token = exchange.getRequest().getHeaders()
                    .getFirst("Authorization");
            String userId = "anonymous";
            if (token != null && !token.isEmpty()) {
                try {
                    Claims claims = jwtUtil.parseToken(token);
                    userId = claims.getSubject();
                } catch (Exception e) {
                    // Token 解析失败，按匿名用户限流
                }
            }
            return Mono.just("seckill:user:" + userId);
        };
    }
}
