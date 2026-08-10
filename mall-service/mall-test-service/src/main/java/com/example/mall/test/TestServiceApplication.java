package com.example.mall.test;

import com.example.mall.config.RedisConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * Hello world!
 *
 */
@SpringBootApplication(
        scanBasePackages = "com.example.mall",
        // 禁用数据源自动配置
        exclude = {DataSourceAutoConfiguration.class}
)
// 排除 mall-common 中的 RedisConfig，避免与本模块的 RedisConfig Bean 名称冲突
@ComponentScan(
        basePackages = "com.example.mall",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = RedisConfig.class
        )
)
@EnableDiscoveryClient
public class TestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestServiceApplication.class, args);
    }
}