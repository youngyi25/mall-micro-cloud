package com.example.mall.product.service;

import com.example.mall.product.domain.entity.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.api.product.dto.ProductDTO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author yy
 * @since 2026-06-11
 */
public interface ISkuInfoService extends IService<SkuInfo> {
    /**
     *  根据SPUID获取商品
     * @param spuId SPUID
     * @return 返回商品属性及属性值
     */
    List<ProductDTO> listBySpuId(Long spuId);
    /**
     *  保存SPU静态页
     * @param spuId SPUID
     * @param ossUrl OSS地址
     * @return 封装了商品详情
     */
    void updateBySpuId(Long spuId, String ossUrl);
    /**
     * 库存扣减
     * @param deductMap Map<商品ID,销售数量>
     */
    void decreaseStock(Map<Long, Integer> deductMap);

}
