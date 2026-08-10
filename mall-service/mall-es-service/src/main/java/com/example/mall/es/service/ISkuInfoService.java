package com.example.mall.es.service;

import com.example.mall.es.domain.dto.SkuInfoDTO;

import java.util.List;

public interface ISkuInfoService {
    void saveAll();
    void save(SkuInfoDTO skuInfoDTO);

    void  deleteById (String id);

    void updateById (SkuInfoDTO skuInfoDTO);

    SkuInfoDTO findById (String id);

    List<SkuInfoDTO> findByCategoryId (Long categoryId);

    List<SkuInfoDTO> findByPriceBetween(Double min, Double max);
}
