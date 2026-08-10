package com.example.mall.es.controller;

import com.example.mall.es.domain.dto.SkuInfoDTO;
import com.example.mall.es.service.ISkuInfoService;
import com.example.mall.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
public class SkuInfoController {
    @Autowired
    private ISkuInfoService skuInfoService;
    @GetMapping("/importAll")
    public Result importAll(){
        skuInfoService.saveAll();
        return Result.success();
    }
    @PostMapping("/save")
    public Result save(@RequestBody SkuInfoDTO skuInfoDTO) {
        skuInfoService.save(skuInfoDTO);
        return Result.success();
    }
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        skuInfoService.deleteById(String.valueOf(id));
        return Result.success();
    }
    @PutMapping("/update")
    public Result update(@RequestBody SkuInfoDTO skuInfoDTO) {
        skuInfoService.updateById(skuInfoDTO);
        return Result.success();
    }
}
