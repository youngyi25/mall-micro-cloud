package com.example.mall.consumer.config;

import com.alibaba.fastjson.JSON;
import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.exception.BusinessException;
import com.example.mall.vo.Result;
import org.springframework.messaging.Message;
import com.example.mall.api.cart.feign.CartFeignClient;
import com.example.mall.util.MqHeaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class DeleteCartHandler {
    private final CartFeignClient cartFeignClient;
    private final MqHeaderUtil mqHeaderUtil;
    private final RedisTemplate<String,Object> redisTemplate;
    @Bean
    public Consumer<Message<String>> deleteCartInput(){
        return message -> {
            //获取消息
            String key = (String)message.getHeaders().get("TRANSACTION_ID");
            String lockKey = "que:lock" + key;
            String bodyStr = message.getPayload();
            List<String> cartList = JSON.parseArray(bodyStr, String.class);
            Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, "1", 30, TimeUnit.SECONDS);
            if(!Boolean.TRUE.equals(acquired)){
                return;
            }
            try {
                String headerJson = (String) message.getHeaders().get("MQ_HTTP_HEADER");
                Map<String,Object> headerMap=JSON.parseObject(headerJson);
                mqHeaderUtil.buildMockRequestContext(headerMap);
                Result result = cartFeignClient.removeCart(cartList);
                if(!ResultCodeEnum.SUCCESS.getCode().equals(result.getCode())){
                    throw new BusinessException(result.getCode(),result.getMsg());
                }
                System.out.println("删除购物车成功");
            } catch (BusinessException e) {
                throw new RuntimeException("删除购物车失败");
            } finally {
                mqHeaderUtil.clearContext();
            }
        };

    }
}
