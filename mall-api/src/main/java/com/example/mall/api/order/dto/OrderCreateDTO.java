package com.example.mall.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Schema(description = "生成订单数据")
@Data
public class OrderCreateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户帐号")
    private Long userId;

    @Schema(description = "订单类型：0->正常订单；1->秒杀订单")
    private Integer orderType;

    @Schema(description = "收货人地址编号")
    private String addressId;

    @Schema(description = "订单明细集合")
    private List<OrderItemsCreateDTO> orderItemsList;

    @Schema(description = "购物车Id集合")
    private List<String> cartIds;
}
