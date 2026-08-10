package com.example.mall.test.config;

import feign.RequestInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.mall.api.common.interceptor.HeaderInterceptor;

/**
 * 显式注册 HeaderInterceptor 作为 Feign 请求拦截器。
 *
 * 问题背景：
 * HeaderInterceptor 位于 product.interceptor 包，不在主应用 scanBasePackages ("com.example.mall") 范围内，
 * 也不会被 @EnableFeignClients 扫描到（它不是 FeignClient，而是 RequestInterceptor），
 * 因此该 Bean 从未被创建，Feign 调用时网关注入的 X-Gateway-Secret 等头无法透传到下游服务，
 * 导致下游 AuthorizationInterceptor 收不到密钥而返回 403。
 *
 * 解决方式：
 * 通过 @Bean 手动创建 HeaderInterceptor 并绑定 configuration properties，
 * Spring Cloud OpenFeign 会自动将它作为全局 RequestInterceptor 应用到所有 Feign 客户端。
 */
@Configuration
public class FeignInterceptorConfig {

    @Bean
    @ConfigurationProperties(prefix = "header")
    public HeaderInterceptor headerInterceptor() {
        return new HeaderInterceptor();
    }

    @Bean
    public RequestInterceptor headerRequestInterceptor(HeaderInterceptor headerInterceptor) {
        // 触发 @PostConstruct init() 确保白名单有值
        headerInterceptor.init();
        return headerInterceptor;
    }
}
