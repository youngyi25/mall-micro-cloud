package com.example.mall.fillter;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * 活动级别限流 KeyResolver — 以秒杀活动 ID 作为限流维度。
 * 配合 RequestRateLimiter，限制整个秒杀活动的总 QPS。
 */
@Configuration
public class ActivityLimitKeyResolver {

    @Bean
    public KeyResolver activityKeyResolver() {
        return exchange -> {
            String activityId = exchange.getRequest().getQueryParams()
                    .getFirst("activityId");
            if (activityId == null || activityId.isEmpty()) {
                activityId = "unknown";
            }
            return Mono.just("seckill:activity:" + activityId);
        };
    }
}
