package com.example.mall.api.product.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SkuInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    private Long spuId;

    private Long price;

    private String skuName;

    private String skuAttribute;

    private Integer num;

    private Long brandId;

    private String brandName;

    private Long categoryId;

    private String categoryName;

    private String skuDefaultImg;

    private String images;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String htmlUrl;
}
