package com.example.mall.oss.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {
    @Value("${aliyun.cloud.endpoint}")
    private String endpoint;
    @Value("${aliyun.cloud.access-key-id}")
    private String accessKeyId;
    @Value("${aliyun.cloud.access-key-secret}")
    private String accessKeySecret;

    /** 注入OSS客户端Bean，销毁时关闭连接 */
    @Bean(destroyMethod = "shutdown")
    public OSS ossClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}
