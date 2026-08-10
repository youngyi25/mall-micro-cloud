package com.example.mall.test.domain.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "connect")
@Data
@ToString
public class ConnectDTO{
    private Long timeOut;
    private Long online;
}
