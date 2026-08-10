package com.example.mall.consumer.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.example.mall.api.es.feign.SkuInfoEsFeignClient;
import com.example.mall.api.product.dto.SkuInfoDTO;
import com.example.mall.util.CamelCastUtils;
import com.example.mall.util.MqHeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
public class SyncDataToEsHandler {
    @Autowired
    private SkuInfoEsFeignClient skuInfoEsFeignClient;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private MqHeaderUtil mqHeaderUtil;
    @Bean
    public Consumer<Message<String>> syncDataInput() {
        return message -> {
            // 1. 获取数据
            //  获取消息体
            String body = message.getPayload();
            // 将MQ转换中Map
            Map<String, Object> canalData = JSON.parseObject(body, Map.class);
            // 获取SkuInfo对象集合
            List<JSONObject> data = (List<JSONObject>) canalData.get("data");
            // 获取SkuInfo对象,并同步到Es
            if(data == null || data.isEmpty()){
                return;
            }
            // 将json对象转换成SkuInfoDTO对象（驼峰转换）
            SkuInfoDTO skuInfoDTO = CamelCastUtils.jsonToObject(data.get(0), SkuInfoDTO.class);
            // 获取商品ID
            String key = skuInfoDTO.getId();
            String lockKey = "que:lock:" + key;
            // 2. 幂等校验
            Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, "1", 30, TimeUnit.SECONDS);
            if (!Boolean.TRUE.equals(acquired)) {
                return;
            }
            // 3. 同步数据
            try {
                // 3.1 构建请求上下文
                mqHeaderUtil.buildMockRequestContext();
                // 3.2 同步操作
                // 获取操作类型
                String type = canalData.get("type").toString().toUpperCase();
                switch (type) {
                    case "INSERT":
                        // 保存
                        skuInfoEsFeignClient.save(skuInfoDTO);
                        break;
                    case "UPDATE":
                        // 修改
                        skuInfoEsFeignClient.update(skuInfoDTO);
                        break;
                    case "DELETE":
                        // 删除
                        skuInfoEsFeignClient.delete(Long.valueOf(skuInfoDTO.getId()));
                        break;
                }

            } catch (Exception e) {
                // 抛出异常触发Stream自动重试，达到max-attempts进入死信
                throw new RuntimeException("消费同步数据到Es异常", e);
            } finally {
                // 必须清理ThreadLocal，线程池复用防串上下文
                mqHeaderUtil.clearContext();
            }

        };
    }
}
