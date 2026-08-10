package com.example.mall.es.service.impl;

import com.example.mall.api.product.dto.SkuInfoDTO;
import com.example.mall.api.product.feign.SkuInfoFeignClient;
import com.example.mall.es.Mapper.SkuInfoMapper;
import com.example.mall.es.domain.entity.SkuInfo;
import com.example.mall.es.service.ISkuInfoService;
import com.example.mall.exception.BusinessException;
import com.example.mall.util.JsonUtils;
import com.example.mall.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class SkuInfoServiceImpl implements ISkuInfoService {
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuInfoFeignClient skuInfoFeignClient;
    @Override
    public void saveAll() {
        Result<List<SkuInfoDTO>> result = skuInfoFeignClient.listAll();
        if (result.getCode() != 200) {
            throw new BusinessException(result.getCode(),result.getMsg());
        }
        List<SkuInfo> skuInfo = JsonUtils.toList(result.getData(), SkuInfo.class);
        skuInfoMapper.saveAll(skuInfo);

    }

    @Override
    public void save(com.example.mall.es.domain.dto.SkuInfoDTO skuInfoDTO) {
        SkuInfo skuInfo = JsonUtils.toObj(skuInfoDTO, SkuInfo.class);
        skuInfoMapper.save(skuInfo);
    }

    @Override
    public void deleteById(String id) {
        skuInfoMapper.deleteById(id);
    }

    @Override
    public void updateById(com.example.mall.es.domain.dto.SkuInfoDTO skuInfoDTO) {
        SkuInfo skuInfo = JsonUtils.toObj(skuInfoDTO, SkuInfo.class);
        skuInfoMapper.deleteById(skuInfo.getId());
        skuInfoMapper.save(skuInfo);
    }

    @Override
    public com.example.mall.es.domain.dto.SkuInfoDTO findById(String id) {
        Optional<SkuInfo> optional = skuInfoMapper.findById(id);
        if(!optional.isPresent()){
            return null;
        }
        return JsonUtils.toObj(optional.get(), com.example.mall.es.domain.dto.SkuInfoDTO.class);
    }

    @Override
    public List<com.example.mall.es.domain.dto.SkuInfoDTO> findByCategoryId(Long categoryId) {
        List<SkuInfo> skuInfoList = skuInfoMapper.findByCategoryId(categoryId);
        List<com.example.mall.es.domain.dto.SkuInfoDTO> skuInfoDTOList = JsonUtils.toList(skuInfoList, com.example.mall.es.domain.dto.SkuInfoDTO.class);
        return skuInfoDTOList;
    }

    @Override
    public List<com.example.mall.es.domain.dto.SkuInfoDTO> findByPriceBetween(Double min, Double max) {
        List<SkuInfo> skuInfoList = skuInfoMapper.findByPriceBetween(min, max);
        List<com.example.mall.es.domain.dto.SkuInfoDTO> skuInfoDTOList = JsonUtils.toList(skuInfoList, com.example.mall.es.domain.dto.SkuInfoDTO.class);
        return skuInfoDTOList;
    }
}
