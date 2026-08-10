package com.example.mall.api.category.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BrandDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String logo;
}
