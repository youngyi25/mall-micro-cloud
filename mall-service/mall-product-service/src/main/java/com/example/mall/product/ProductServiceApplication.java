package com.example.mall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 商品服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.example.mall")
@EnableFeignClients(basePackages = "com.example.mall.api")
@MapperScan("com.example.mall.product.mapper")
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}