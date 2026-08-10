package com.example.mall.order.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author yy
 * @since 2026-07-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_info")
@Schema(name = "OrderInfo对象", description = "订单表")
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单编号")
    @TableId(value = "order_id", type = IdType.ASSIGN_ID)
    private String orderId;

    @Schema(description = "提交时间")
    private LocalDateTime createTime;

    @Schema(description = "用户帐号")
    private Long userId;

    @Schema(description = "订单总金额")
    private Double totalAmount;

    @Schema(description = "支付方式：0->未支付；1->支付宝；2->微信；3->银行卡")
    private Integer payType;

    @Schema(description = "订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单")
    private Integer status;

    @Schema(description = "订单类型：0->正常订单；1->秒杀订单")
    private Integer orderType;

    @Schema(description = "收货人地址编号")
    private Long addressId;

    @Schema(description = "交易流水号")
    private String transactionId;

    @Schema(description = "确认收货状态：0->未确认；1->已确认")
    private Integer confirmStatus;

    @Schema(description = "支付时间")
    private LocalDateTime paymentTime;

    @Schema(description = "发货时间")
    private LocalDateTime deliveryTime;

    @Schema(description = "确认收货时间")
    private LocalDateTime receiveTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;


}
