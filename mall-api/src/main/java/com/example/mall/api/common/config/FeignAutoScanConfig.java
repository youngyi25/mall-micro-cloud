package com.example.mall.api.common.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.example.mall.api")
public class FeignAutoScanConfig {
    // 公共模块统一开启Feign扫描，全局生效
}
