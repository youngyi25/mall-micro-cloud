package com.example.mall.seckill.controller;

import com.example.mall.seckill.domain.dto.SeckillGoodsDTO;
import com.example.mall.seckill.domain.dto.StockDeductDTO;
import com.example.mall.seckill.service.IProductService;
import com.example.mall.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/product")
public class ProductController {
    @Autowired
    private IProductService productService;

    @GetMapping("/listTodaySeckillGoods")
    public List<SeckillGoodsDTO> listTodaySeckillGoods() {
        return productService.listTodaySeckillGoods();
    }
    @GetMapping("/generateHtml")
    public void generateHtml() throws Exception {
        productService.generateHtml();
    }
    @GetMapping("/loadStockCache")
    public void loadStockCache() {
        productService.loadStockCache();
    }
    @PutMapping("/decreaseStock")
    public Result decreaseStock(@RequestBody StockDeductDTO stockDeductDTO) {
        productService.decreaseStock(stockDeductDTO);
        return Result.success();
    }
}
