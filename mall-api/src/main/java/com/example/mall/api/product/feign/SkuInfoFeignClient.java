package com.example.mall.api.product.feign;

import com.example.mall.api.product.dto.SkuInfoDTO;
import com.example.mall.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "mall-product-service", contextId = "skuInfo-feign")
public interface SkuInfoFeignClient {

    @PutMapping("/skuInfo/deductStock")
    Result deductStock(@RequestBody Map<Long, Integer> deductMap);

    @GetMapping("/skuInfo/listAll")
    Result<List<SkuInfoDTO>> listAll();
}
