package com.example.mall.test.controller;

import com.example.mall.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/mq")
public class MQController {
    @Autowired
    StreamBridge streamBridge;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/send")
    public Result send(String msg){
        streamBridge.send("testOutput-out-0",msg);
        return Result.success("发送成功");
    }

    @GetMapping("/batch")
    public Result batch(){
        int count = Math.abs(new Random().nextInt(10));
        for (int i = 0; i < count; i++) {
            Message<?> message = MessageBuilder.withPayload("测试批量消息" + i)
                    .setHeader("ROCKET_TAGS", "batch")
                    .build();
            streamBridge.send("testOutput-out-1", message);
        }
        return Result.success(String.format("批量发送 %d 条消息", count));
    }

    @GetMapping("/order")
    public Result order(){
        for (int i = 0; i < 10; i++){
            // 订单编号
            int orderId = i % 3;
            String msg = "测试顺序消息" + orderId + "_" + i;
            Message<?> message = MessageBuilder.withPayload(msg)
                    .setHeader("ORDERLY_KEY", orderId)
                    .setHeader("ROCKET_ORDER_ID", orderId)
                    .build();
            streamBridge.send("testOutput-out-1", message);
        }
        return Result.success("批量发送 10 条消息");
    }

    /**
     * 生产端幂等性保障：通过 Redis 分布式锁防止重复发送
     * @param msg 消息内容
     * @param key 业务唯一键（如订单号、交易流水号）
     */
    @GetMapping("/unique")
    public Result<String> unique(String msg, String key) {
        // 1. 锁 Key 规范化
        String lockKey = "req:lock:" + key;
        String msgKey = "order:" + key;  // 消息 KEYS，加业务前缀便于识别
        // 2. 原子获取锁（10秒过期），必须设置合理过期时间，未设置过期时间导致锁无法自动释放(如系统崩溃)
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(acquired)) {
            // 3. 构建消息，通过消息头 KEYS 绑定业务唯一键
            Message<String> message = MessageBuilder.withPayload(msg)
                    .setHeader("KEYS", msgKey)
                    .build();
            // 4. 发送消息
            streamBridge.send("testOutput-out-2", message);
            return Result.success("消息发送成功");
        }
        // 5. 获取锁失败（10秒内重复提交）
        return Result.fail(7001, "请勿重复提交，请稍后再试");
    }
}
