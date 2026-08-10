package com.example.mall.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "订单数据")
@Data
public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "订单编号")
    private String orderId;

    @Schema(description = "下单时间")
    private LocalDateTime createTime;

    @Schema(description = "总金额")
    private Double totalAmount;

    @Schema(description = "订单明细")
    private List<OrderItemsDTO> orderItemsList;
}
