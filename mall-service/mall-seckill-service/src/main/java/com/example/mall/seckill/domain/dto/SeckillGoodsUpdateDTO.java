package com.example.mall.seckill.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 秒杀商品更新DTO类
 * 用于传输和更新秒杀商品相关数据
 * 实现Serializable接口以支持序列化
 */
@Data // 使用Lombok的@Data注解，自动生成getter、setter等方法
public class SeckillGoodsUpdateDTO implements Serializable {
    private static final long serialVersionUID = 1L; // 序列化版本UID，用于版本控制
    private Integer id; // 商品ID
    private Integer secki11Price; // 秒杀价格（注意：变量名疑似拼写错误，应为seckillPrice）
    private Integer num; // 商品数量
    private Integer storeCount; // 库存数量
    private Integer activityId; // 活动ID
}
