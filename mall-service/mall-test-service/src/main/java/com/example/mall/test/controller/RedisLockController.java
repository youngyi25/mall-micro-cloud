package com.example.mall.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redis/lock")
@Slf4j
public class RedisLockController {
    @Autowired
    private RedisTemplate<String ,String> redisTemplate;
    @PutMapping("/key")
    public void setRedisKey(){
        boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("test_stock","1",100, TimeUnit.MILLISECONDS);
        if (ifAbsent) {
            log.info(System.currentTimeMillis() + "抢到锁，开始处理订单");
        } else {
            log.info("很遗憾，没有抢到锁");
        }
    }
}
