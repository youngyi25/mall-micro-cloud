package com.example.mall.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CartQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private String skuName;
    private Double minPrice;
    private Double maxPrice;

    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
