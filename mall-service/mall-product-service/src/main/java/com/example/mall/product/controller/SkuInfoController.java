package com.example.mall.product.controller;

import com.example.mall.api.product.dto.SkuInfoDTO;
import com.example.mall.product.domain.entity.SkuInfo;
import com.example.mall.product.service.ISkuInfoService;
import com.example.mall.util.JsonUtils;
import com.example.mall.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品表 前端控制器
 * </p>
 *
 * @author yy
 * @since 2026-06-11
 */
@Tag(name = "商品信息接口")
@RestController
@RequestMapping("/skuInfo")
public class SkuInfoController {
    @Autowired
    private ISkuInfoService skuInfoService;
    @Operation(summary = "扣减库存")
    @PutMapping("/deductStock")
    public Result deductStock(@RequestBody Map<Long, Integer> deductMap) {
        skuInfoService.decreaseStock(deductMap);
        return Result.success();
    }

    @Operation(summary ="查询所有商品信息")
    @GetMapping("/listAll")
    public Result<List<SkuInfoDTO>> listAll(){
        List<SkuInfo> skuInfos = skuInfoService.list();
        List<SkuInfoDTO> list = JsonUtils.toList(skuInfos, SkuInfoDTO.class);
        return Result.success(list);
    }

}
