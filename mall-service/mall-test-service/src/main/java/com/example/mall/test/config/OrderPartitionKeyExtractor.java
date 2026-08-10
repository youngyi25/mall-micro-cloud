package com.example.mall.test.config;

import org.springframework.cloud.stream.binder.PartitionKeyExtractorStrategy;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class OrderPartitionKeyExtractor implements PartitionKeyExtractorStrategy {
    @Override
    public Object extractKey(Message<?> message) {
        // 获取消息头中的ORDERLY_KEY作为分区键（同一个订单数据发到同一个分区）
        Object orderlyKey = message.getHeaders().get("ORDERLY_KEY");
        if(orderlyKey != null){
            return orderlyKey.hashCode();
        }
        // 获取消息头中的KEYS,作为分区键（同一个KEY会发到同一个分区）
        Object key = message.getHeaders().get("KEYS");
        if(key != null){
            return key.hashCode();
        }
        // 最终兜底，使用消息ID 作为分区键（所有消息会被分散到不同分区）
        Object messageId = message.getHeaders().getId();
        return messageId.hashCode();
    }
}
