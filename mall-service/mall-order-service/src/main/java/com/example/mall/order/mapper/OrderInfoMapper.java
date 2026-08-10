package com.example.mall.order.mapper;

import com.example.mall.order.domain.po.OrderInfo;
import com.example.mall.product.domain.entity.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author yy
 * @since 2026-07-27
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /**
     * 根据商品ID列表查询SKU信息
     * @param skuIds 商品ID列表
     * @return SKU信息列表
     */
    @Select("<script>SELECT * FROM sku_info WHERE id IN " +
            "<foreach collection='skuIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach></script>")
    List<SkuInfo> selectByIds(@Param("skuIds") List<Long> skuIds);
}
