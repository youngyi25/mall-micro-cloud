package com.example.mall.api.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String spuName;
    private Map<String,List<String>> attrMap; // 属性:<属性名称，属性值列表>
    private List<ProductDTO> productDTOs; // sku列表 一个SPU可能多个SKU

}
