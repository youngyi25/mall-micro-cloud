package com.example.mall.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.mall.api.order.dto.OrderInfoUpdateDTO;
import com.example.mall.pay.service.IMessageSendService;
import com.example.mall.util.MqHeaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PayMessageSendServiceImpl implements IMessageSendService {
    public static final String UPDATE_ORDER_CHANNEL = "updateOrderOutput-out-0";
    private final StreamBridge streamBridge;
    private final MqHeaderUtil mqHeaderUtil;
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public void sendUpdateStatusMsg(String orderId) {
        Map<String, String> httpHeaders = mqHeaderUtil.extractHttpHeaders();
        String mqHttpHeaders = JSON.toJSONString(httpHeaders);
        OrderInfoUpdateDTO orderInfoUpdateDTO = new OrderInfoUpdateDTO();
        orderInfoUpdateDTO.setOrderId(orderId);
        orderInfoUpdateDTO.setStatus(1);
        orderInfoUpdateDTO.setPayType(1);
        orderInfoUpdateDTO.setPaymentTime(LocalDateTime.now());
        String body = JSON.toJSONString(orderInfoUpdateDTO);
        Message<String> message = MessageBuilder
                .withPayload(body)
                .setHeader("MQ_HTTP_HEADER", mqHttpHeaders)
                .setHeader("TRANSACTION_ID", orderId)
                .build();
        String lockKey = "req:lock" + orderId;
        if(redisTemplate.opsForValue().setIfAbsent(lockKey, "1",30, TimeUnit.SECONDS)){
            streamBridge.send(UPDATE_ORDER_CHANNEL, message);
        }
    }
}
