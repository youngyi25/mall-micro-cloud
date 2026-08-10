package com.example.mall.api.product.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long spuId;

    private Double price;

    private String skuName;

    private Integer num;

    private String skuDefaultImg;

    private String images;

    private String skuAttribute;
}
