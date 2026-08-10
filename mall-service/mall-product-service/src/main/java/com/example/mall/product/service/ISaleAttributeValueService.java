package com.example.mall.product.service;

import com.example.mall.product.domain.entity.SaleAttributeValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yy
 * @since 2026-06-11
 */
public interface ISaleAttributeValueService extends IService<SaleAttributeValue> {
    /**
     * 根据SPUID获取SPU属性及属性值，并封装Map中
     * @param spuId SPUID
     */
    Map<String, List<String>> listBySpuId(Long spuId);

}
