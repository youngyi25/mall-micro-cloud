package com.example.mall.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.mall.order.service.IMessageSendService;
import com.example.mall.util.MqHeaderUtil;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.statement.select.KSQLWindow;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MessageSendServiceImpl implements IMessageSendService {
   private  static final String DELETE_CART_CHANNEL = "deleteCart-out-0";
   private static final String ORDER_CHECK_CHANNEL = "orderCheck-out-0";
   private final RedisTemplate<String,Object> redisTemplate;
   private final StreamBridge streamBridge;
   private final MqHeaderUtil mqHeaderUtil;

    @Override
    public void sendDeleteCartMsg(String transactionId, List<String> cartIds) {
        if(cartIds == null || cartIds.isEmpty()){
            return;
        }
        Map<String, String> httpHeaders = mqHeaderUtil.extractHttpHeaders();
        String mqHttpHeaders = JSON.toJSONString(httpHeaders);
        String body = JSON.toJSONString(cartIds);
        Message<String> message = MessageBuilder
                .withPayload(body)
                .setHeader("MQ_HTTP_HEADER", mqHttpHeaders)
                .setHeader("TRANSACTION_ID", transactionId)
                .build();
        streamBridge.send(DELETE_CART_CHANNEL, message);
    }

    @Override
    public void sendOrderCheckMsg(Long userId, String orderId) {
        Map<String, String> httpHeaders = mqHeaderUtil.extractHttpHeaders();
        String mqHttpsHeaders = JSON.toJSONString(httpHeaders);
        String transactionId = "orderId"+"userId";
        Message<String> message = MessageBuilder
                .withPayload(orderId)
                .setHeader("MQ_HTTP_HEADER", mqHttpsHeaders)
                .setHeader("TRANSACTION_ID", transactionId)
                .setHeader("DELAY", 5)
                .build();
        String lockKey = "req:lock" + transactionId;
        if(redisTemplate.opsForValue().setIfAbsent(lockKey,"1",30, TimeUnit.SECONDS)){
                streamBridge.send(ORDER_CHECK_CHANNEL, message);
        }
    }
}
