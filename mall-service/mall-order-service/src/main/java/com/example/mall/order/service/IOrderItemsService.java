package com.example.mall.order.service;

import com.example.mall.order.domain.po.OrderItems;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.api.order.dto.OrderItemsDTO;

import java.util.List;

/**
 * <p>
 * 订单中所包含的商品 服务类
 * </p>
 *
 * @author yy
 * @since 2026-07-27
 */
public interface IOrderItemsService extends IService<OrderItems> {
    /**
     * 获取订单明细
     * @param orderId
     */
    List<OrderItemsDTO> listByOrderId(String orderId);
    /**
     * 按订单ID删除订单明细
     * @param orderId
     */
    void removeByOrderId(String orderId);
}
