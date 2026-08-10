package com.example.mall.es.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.mall.api.product.dto.ProductQueryDTO;
import com.example.mall.es.service.IProductService;
import com.example.mall.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "商品搜索服务")
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private IProductService productService;
    @Operation(summary = "商品搜索")
    @GetMapping("/page")
    public Result<IPage> page(ProductQueryDTO queryDTO){
        IPage page = productService.page(queryDTO);
        return Result.success(page);
    }
}
