package com.example.mall.api.seckill.feign;

import com.example.mall.api.seckill.dto.SeckillGoodsDTO;
import com.example.mall.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "mall-seckill-service", contextId = "seckill-product-feign")
public interface SeckillProductFeignClient {

    /**
     * 获取今日秒杀商品列表
     */
    @GetMapping("/product/listTodaySeckillGoods")
    Result<List<SeckillGoodsDTO>> listTodaySeckillGoods();

    /**
     * 生成秒杀商品静态页面
     */
    @GetMapping("/product/generateHtml")
    void generateHtml() throws Exception;

    /**
     * 加载秒杀库存缓存
     */
    @GetMapping("/product/loadStockCache")
    void loadStockCache();
}
