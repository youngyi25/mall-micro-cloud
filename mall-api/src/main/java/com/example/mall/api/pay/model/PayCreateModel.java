package com.example.mall.api.pay.model;

import lombok.Data;

@Data
public class PayCreateModel {
    private String orderId;    // 订单ID
    private String subject;    // 订单标题
    private String body;       // 订单描述
    private Double totalAmount;// 订单金额
}
