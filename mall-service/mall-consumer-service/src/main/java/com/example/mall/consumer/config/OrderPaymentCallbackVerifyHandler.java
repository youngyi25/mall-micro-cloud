package com.example.mall.consumer.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.mall.api.order.dto.OrderInfoUpdateDTO;
import com.example.mall.api.order.feign.OrderFeignClient;
import com.example.mall.api.order.feign.OrderInfoFeignClient;
import com.example.mall.api.pay.feign.AlipayFeignClient;
import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.exception.BusinessException;
import com.example.mall.util.MqHeaderUtil;
import com.example.mall.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
public class OrderPaymentCallbackVerifyHandler {
    @Autowired
    private OrderInfoFeignClient orderInfoFeignClient;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private AlipayFeignClient alipayFeignClient;
    @Autowired
    private MqHeaderUtil mqHeaderUtil;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    public Consumer<Message<String>> orderCheckInput(){
        return message ->{
            String key=(String)message.getHeaders().get("TRANSACTION_ID");
            String lockKey = "que:lock" + key;
            String orderId = message.getPayload();
            Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, 1, 30, TimeUnit.SECONDS);
            if(!Boolean.TRUE.equals(acquired)){
                return;
            }
            try {
                String headerJSON=(String)message.getHeaders().get("MQ_HTTP_HEADER");
                Map<String,Object> headMap = JSON.parseObject(headerJSON);
                mqHeaderUtil.buildMockRequestContext(headMap);
                Result<Integer> result = orderInfoFeignClient.getOrderStatus(orderId);
                if(!ResultCodeEnum.SUCCESS.getCode().equals(result.getCode())){
                    throw new BusinessException(result.getCode(),result.getMsg());
                }
                Integer orderStatus= (Integer) result.getData();
                if(orderStatus>0){
                    return;
                }
                Result paymentResult = alipayFeignClient.queryPaymentResult(orderId);
                if(!ResultCodeEnum.SUCCESS.getCode().equals(paymentResult.getCode())){
                    throw new BusinessException(paymentResult.getCode(),paymentResult.getMsg());
                }
                if("TRADE_SUCCESS".equals(paymentResult.getData())){
                    OrderInfoUpdateDTO updateDTO = new OrderInfoUpdateDTO();
                    updateDTO.setOrderId(orderId);
                    updateDTO.setStatus(1);
                    updateDTO.setPayType(1);
                    updateDTO.setPaymentTime(LocalDateTime.now());
                    orderInfoFeignClient.update(updateDTO);
                }else {
                    orderFeignClient.removeById(orderId);
                }
            } catch (Exception e) {
                throw new RuntimeException("消费查询订单消息异常", e);
            } finally {
                mqHeaderUtil.clearContext();
            }
        };
    }
}
