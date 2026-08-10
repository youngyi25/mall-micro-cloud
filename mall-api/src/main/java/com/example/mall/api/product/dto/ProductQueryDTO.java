package com.example.mall.api.product.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 商品名称：模糊查询 */
    private String productName;

    /** 三级分类：一级、二级、三级类目ID */
    private Integer FirstCategoryId;
    private Integer SecondCategoryId;
    private Integer ThirdCategoryId;
    /** 品牌ID */
    private Long brandId;
    /** 价格区间 */
    private Double minPrice;
    private Double maxPrice;
    /** 分页 */
    private Long pageNum = 1L;
    private Long pageSize = 10L;
}
