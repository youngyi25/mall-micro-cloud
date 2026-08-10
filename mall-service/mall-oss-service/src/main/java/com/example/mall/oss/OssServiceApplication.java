package com.example.mall.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(scanBasePackages = "com.example.mall", exclude =  {DataSourceAutoConfiguration.class})
public class OssServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OssServiceApplication.class, args);
    }
}
