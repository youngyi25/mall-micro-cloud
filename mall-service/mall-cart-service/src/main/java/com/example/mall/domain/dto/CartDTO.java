package com.example.mall.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CartDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String userId;
    private String skuName;
    private Double price;
    private String image;
    private String skuId;
    private Integer quantity;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
