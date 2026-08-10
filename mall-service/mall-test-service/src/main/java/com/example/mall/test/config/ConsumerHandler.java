package com.example.mall.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Configuration
public class ConsumerHandler {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Bean
    public Consumer<Message<String>> testInput() {
        return message -> {
            String payload = message.getPayload();
            Map<String, Object> headers = message.getHeaders();
            System.out.println("收到消息: " + payload + ", 头信息: " + headers);
        };
    }

    /**
     * 消费端幂等性保障：使用自定义业务键作为去重依据，消费前检查 Redis 是否存在该标识
     */
    @Bean
    public Consumer<Message<String>> testOrderInput() {
        return message -> {
            String payload = message.getPayload();
            Map<String, Object> headers = message.getHeaders();
            // 从消息头获取生产端绑定的业务唯一键
            String key = (String) headers.get("KEYS");
            String lockKey = "que:lock:" + key;
            // 原子性检查并设置去重标识（20秒过期），保证同一条消息只被消费一次
            Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 20, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(acquired)) {
                System.out.println("订单创建成功:" + payload);
            } else {
                System.out.println("订单创建失败:" + payload + "已重复创建");
            }
        };
    }

    @Bean
    public Consumer<Message<String>> testMultipleInputOne() {
        return message -> {
            String payload = message.getPayload();
            Map<String, Object> headers = message.getHeaders();
            System.out.println("消费者1收到消息: " + payload + ", 队列编号: " + headers.get("ROCKET_MQ_QUEUE_ID"));
        };
    }

    @Bean
    public Consumer<Message<String>> testMultipleInputTwo() {
        return message -> {
            String payload = message.getPayload();
            Map<String, Object> headers = message.getHeaders();
            System.out.println("消费者2收到消息: " + payload + ", 队列编号: " + headers.get("ROCKET_MQ_QUEUE_ID"));
        };
    }

    @Bean
    public Consumer<Message<String>> testMultipleInputThree() {
        return message -> {
            String payload = message.getPayload();
            Map<String, Object> headers = message.getHeaders();
            System.out.println("消费者3收到消息: " + payload + ", 队列编号: " + headers.get("ROCKET_MQ_QUEUE_ID"));
        };
    }
}
