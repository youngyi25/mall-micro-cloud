package com.example.mall.order.service;

import java.util.List;

/**
 * 消息发送服务接口
 * 该接口定义了发送购物车相关消息的方法
 */
public interface IMessageSendService {
    /**
     * 发送删除购物车消息
     * @param transactionId 交易ID，用于标识和追踪交易
     * @param cartIds 购物车ID列表，包含需要删除的购物车ID
     */
    void sendDeleteCartMsg(String transactionId, List<String> cartIds);
    /**
     * 发送订单回查事务消息
     * @param userId 用户ID
     * @param orderId 订单ID
     */
    void sendOrderCheckMsg(Long userId,String orderId);
}
