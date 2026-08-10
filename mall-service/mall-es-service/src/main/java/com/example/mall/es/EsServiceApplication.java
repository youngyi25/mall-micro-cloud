package com.example.mall.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Hello world!
 *
 */
@SpringBootApplication(scanBasePackages = "com.example.mall",exclude = {DataSourceAutoConfiguration.class})
@EnableElasticsearchRepositories(basePackages = "com.example.mall.es.Mapper")
public class EsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EsServiceApplication.class, args);
    }
}
