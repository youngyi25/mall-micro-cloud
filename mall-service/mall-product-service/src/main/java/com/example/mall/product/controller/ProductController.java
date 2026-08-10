package com.example.mall.product.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.mall.product.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.mall.api.product.dto.ProductQueryDTO;

@Tag(name = "商品模块",description = "商品高级查询接口")
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private IProductService productService;
    @Operation(summary = "商品分页查询",description = "商品分页查询接口")
    @SentinelResource("productPage")
    @GetMapping("/page")
    public Object page(ProductQueryDTO queryDTO) {
        return productService.page(queryDTO);
    }

}
