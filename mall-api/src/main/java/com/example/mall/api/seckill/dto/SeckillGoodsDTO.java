package com.example.mall.api.seckill.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 秒杀商品DTO，用于Feign服务间调用
 */
@Data
public class SeckillGoodsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String spuId;

    private String skuId;

    private String skuName;

    private String image;

    private Integer price;

    private Integer seckillPrice;

    private Integer num;

    private Integer storeCount;

    private String content;

    private Integer activityId;
}
