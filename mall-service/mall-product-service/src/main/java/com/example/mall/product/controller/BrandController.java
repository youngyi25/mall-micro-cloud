package com.example.mall.product.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.mall.product.service.IBrandService;
import com.example.mall.product.service.ICategoryBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.mall.api.category.dto.BrandDTO;

import java.util.List;

/**
 * <p>
 * 品牌表 前端控制器
 * </p>
 *
 * @author yy
 * @since 2026-06-11
 */
@Tag(name = "品牌模块",description = "品牌增删改查接口")
@RestController
@RequestMapping("/brand")
public class BrandController {
    @Autowired
    private IBrandService brandService;
    @Autowired
    private ICategoryBrandService categoryBrandService;
    @Operation(summary = "获取所有品牌",description = "获取所有品牌接口")
    @SentinelResource("brandListAll")
    @GetMapping("/listAll")
    public List<BrandDTO> listAll(){
        return brandService.listAll();
    }
    @Operation(summary = "获取一级类目的品牌",description = "获取一级类目的品牌接口")
    @SentinelResource("brandListByFirstCategory")
    @GetMapping("/category/1/{categoryId}")
    public List<BrandDTO> listByFirstCategoryId(@PathVariable Integer categoryId){
        return categoryBrandService.listByFirstCategoryId(categoryId);
    }
    @Operation(summary = "获取二级类目的品牌",description = "获取二级类目的品牌接口")
    @SentinelResource("brandListBySecondCategory")
    @GetMapping("/category/2/{categoryId}")
    public List<BrandDTO> listBySecondCategoryId(@PathVariable Integer categoryId){
        return categoryBrandService.listBySecondCategoryId(categoryId);
    }
    @Operation(summary = "获取三级类目的品牌",description = "获取三级类目的品牌接口")
    @SentinelResource("brandListByThirdCategory")
    @GetMapping("/category/3/{categoryId}")
    public List<BrandDTO> listByThirdCategoryId(@PathVariable Integer categoryId){
        return categoryBrandService.listByThirdCategoryId(categoryId);
    }


}
