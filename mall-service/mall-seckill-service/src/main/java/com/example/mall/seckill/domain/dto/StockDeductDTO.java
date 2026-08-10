package com.example.mall.seckill.domain.dto;

import lombok.Data;

@Data
public class StockDeductDTO {
    private Integer activityId;
    private Long skuId;
    private Integer quantity = 1;
}
