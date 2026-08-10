package com.example.mall.order.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderInfoUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单编号
     */
    private String orderId;

    /**
     * 用户帐号
     */
    private Long userId;


    /**
     * 支付方式：0->未支付；1->支付宝；2->微信；3->银行卡
     */
    private Integer payType;

    /**
     * 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
     */
    private Integer status;

    /**
     * 收货人地址ID
     */

    private Long addressId;

    /**
     * 交易流水号
     */
    private String transactionId;

    /**
     * 确认收货状态：0->未确认；1->已确认
     */
    private Byte confirmStatus;

    /**
     * 支付时间
     */

    private LocalDateTime paymentTime;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 确认收货时间
     */

    private LocalDateTime receiveTime;

}
