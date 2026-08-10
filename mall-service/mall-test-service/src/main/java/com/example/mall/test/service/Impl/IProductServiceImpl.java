package com.example.mall.test.service.Impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.mall.test.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IProductServiceImpl implements IProductService {
    @Override
    @SentinelResource("stockCheck")
    public void stockCheck(String type) {
        log.info(type);
    }
}
