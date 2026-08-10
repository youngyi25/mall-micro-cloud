package com.example.mall.product.service;

public interface IPageService {
    /**
     * 生成静态页面
     * @param spuId
     * @return
     */
    String generateProductTemplate(Long spuId) throws Exception;
}
