package com.example.mall.api.pay.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PayLogModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;              // 支付流水号
    private String orderId;         // 订单编号
    private BigDecimal amount;      // 支付金额
    private LocalDateTime createTime;// 提交时间
}
