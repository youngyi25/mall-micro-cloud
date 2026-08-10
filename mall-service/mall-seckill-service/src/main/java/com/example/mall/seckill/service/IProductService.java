package com.example.mall.seckill.service;

import com.example.mall.seckill.domain.dto.SeckillGoodsDTO;
import com.example.mall.seckill.domain.dto.StockDeductDTO;

import java.util.List;

public interface IProductService {
    /**
     * 获取今天的秒杀商品
     */
    List<SeckillGoodsDTO> listTodaySeckillGoods();
    /**
     * 创建秒杀商品详情静态页
     */
    void generateHtml() throws Exception;
    /**
     * 将商品库存写入缓存
     */
    void loadStockCache();
    void decreaseStock(StockDeductDTO stockDeductDTO);
}
