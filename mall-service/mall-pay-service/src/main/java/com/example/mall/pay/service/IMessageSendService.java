package com.example.mall.pay.service;

public interface IMessageSendService {
    /**
     * 发送修改订单状态事务消息
     * @param orderId 订单ID
     */
    void sendUpdateStatusMsg(String orderId);
}
