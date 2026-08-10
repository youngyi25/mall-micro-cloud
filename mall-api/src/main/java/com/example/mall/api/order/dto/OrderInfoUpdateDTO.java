package com.example.mall.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "订单更新数据")
@Data
public class OrderInfoUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "订单编号")
    private String orderId;

    @Schema(description = "用户帐号")
    private Long userId;

    @Schema(description = "支付方式：0->未支付；1->支付宝；2->微信；3->银行卡")
    private Integer payType;

    @Schema(description = "订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单")
    private Integer status;

    @Schema(description = "收货人地址ID")
    private Long addressId;

    @Schema(description = "交易流水号")
    private String transactionId;

    @Schema(description = "确认收货状态：0->未确认；1->已确认")
    private Byte confirmStatus;

    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    @Schema(description = "发货时间")
    private LocalDateTime deliveryTime;

    @Schema(description = "确认收货时间")
    private LocalDateTime receiveTime;
}
