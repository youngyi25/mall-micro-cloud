package com.example.mall.es.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.mall.api.product.dto.ProductQueryDTO;

public interface IProductService {
    /**
     * 商品分页查询
     * @param queryDTO
     * @return
     */
    IPage page(ProductQueryDTO queryDTO);
}
