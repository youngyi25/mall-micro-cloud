package com.example.mall.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Document(collection = "cart") // 指定集合名称，默认为类名小写
public class Cart {
    @Id  // MongoDB _id 主键
    private String id; // 编号
    @Field("user_id") // 映射文档字名段, 默认与属性名一致
    private String userId; // 用户ID
    @Field("sku_name")
    private String skuName; // 商品名称
    private Double price; // 价格
    private String image; // 图片
    @Field("sku_id")
    private String skuId; // 商品ID
    private Integer quantity; // 数量
    @Field("create_time")
    private LocalDateTime createTime; // 创建时间
}