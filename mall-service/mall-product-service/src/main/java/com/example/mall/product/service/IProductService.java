package com.example.mall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.mall.api.product.dto.ProductDetailDTO;
import com.example.mall.api.product.dto.ProductQueryDTO;

public interface IProductService {
    /**
     * 分页查询商品
     * @param queryDTO 查询参数
     * @return 商品分页结果
     */
    IPage page(ProductQueryDTO queryDTO);
    /**
     * 获取商品详情
     * @param spuId SPUID
     * @return 商品详情
     */
    ProductDetailDTO getBySpuId(Long spuId);
}
