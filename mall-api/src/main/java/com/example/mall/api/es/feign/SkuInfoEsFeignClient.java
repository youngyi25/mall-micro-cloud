package com.example.mall.api.es.feign;

import com.example.mall.api.product.dto.SkuInfoDTO;
import com.example.mall.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mall-es-service", contextId = "skuInfo-es-feign")
public interface SkuInfoEsFeignClient {

    @PostMapping("/search/save")
    Result save(@RequestBody SkuInfoDTO skuInfoDTO);

    @DeleteMapping("/search/delete/{id}")
    Result delete(@PathVariable Long id);

    @PutMapping("/search/update")
    Result update(@RequestBody SkuInfoDTO skuInfoDTO);
}
