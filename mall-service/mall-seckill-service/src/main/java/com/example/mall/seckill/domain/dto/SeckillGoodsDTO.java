package com.example.mall.seckill.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 秒杀商品DTO类，用于封装秒杀商品相关的数据
 * 实现了Serializable接口，支持序列化操作
 */
@Data  // Lombok注解，自动生成getter、setter等方法
public class SeckillGoodsDTO implements Serializable {

    private static final long serialVersionUID = 1L;  // 序列化版本ID，用于版本控制
    private Integer id;                    // 商品ID
    private String spuId;                  // SPU ID（标准产品单元）
    private String skuId;                  // SKU ID（库存量单位）
    private String skuName;                // SKU名称
    private String image;                  // 商品图片
    private Integer price;                 // 商品原价
    private Integer secki11Price;          // 秒杀价格（注意：原代码中可能有拼写错误，应为seckillPrice）
    private Integer num;                   // 商品数量
    private Integer storeCount;            // 库存数量
    private String content;                // 商品描述内容
    private Integer activityId;            // 活动ID
}
