package com.example.mall.consumer.config;

import com.alibaba.fastjson.JSON;
import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.exception.BusinessException;
import com.example.mall.api.order.dto.OrderInfoUpdateDTO;
import com.example.mall.api.order.feign.OrderInfoFeignClient;
import com.example.mall.util.JsonUtils;
import com.example.mall.util.MqHeaderUtil;
import com.example.mall.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
public class UpdateOrderHandler {

    @Autowired
    private OrderInfoFeignClient orderInfoFeignClient;

    @Autowired
    private MqHeaderUtil mqHeaderUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Bean
    public Consumer<Message<String>> updateOrderStatusInput() {
        return message -> {
            // 1. 获取消息
            // 1.1 获取RocketMQ事务ID，用于幂等判断
            String key = (String) message.getHeaders().get("TRANSACTION_ID");
            String lockKey = "que:lock:" + key;
            // 1.2 获取消息体
            String bodyStr = new String(message.getPayload());
            OrderInfoUpdateDTO orderInfoUpdateDTO = JsonUtils.toObj(bodyStr, OrderInfoUpdateDTO.class);

            // 2. 幂等校验
            Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 30, TimeUnit.SECONDS);
            if (!Boolean.TRUE.equals(acquired)) {
                return;
            }

            // 3. 修改订单状态
            try {
                // 3.1 恢复Web请求上下文，解决Feign RequestAttributes null
                String headerJson = (String) message.getHeaders().get("MQ_HTTP_HEADER");
                Map<String, Object> headerMap = JSON.parseObject(headerJson);
                mqHeaderUtil.buildMockRequestContext(headerMap);
                // 3.2 远程调用修改订单状态
                Result result = orderInfoFeignClient.update(orderInfoUpdateDTO);
                if (!ResultCodeEnum.SUCCESS.getCode().equals(result.getCode())) {
                    throw new BusinessException(result.getCode(), result.getMsg());
                }
            } catch (Exception e) {
                // 抛出异常触发Stream自动重试，达到max-attempts进入死信
                throw new RuntimeException("消费修改订单消息异常", e);
            } finally {
                // 必须清理ThreadLocal，线程池复用防串上下文
                mqHeaderUtil.clearContext();
            }
        };
    }
}
