package com.example.mall.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "订单明细")
@Data
public class OrderItemsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "销售价格")
    private Double price;

    @Schema(description = "购买数量")
    private Integer quantity;

    @Schema(description = "商品名称")
    private String skuName;

    @Schema(description = "图片地址")
    private String image;
}
