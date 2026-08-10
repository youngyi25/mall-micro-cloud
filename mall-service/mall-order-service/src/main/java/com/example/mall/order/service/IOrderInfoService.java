package com.example.mall.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mall.order.domain.po.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.api.order.dto.OrderCreateDTO;
import com.example.mall.api.order.dto.OrderDTO;
import com.example.mall.api.order.dto.OrderQueryDTO;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author yy
 * @since 2026-07-27
 */
public interface IOrderInfoService extends IService<OrderInfo> {

    /**
     * 按条件分页查询订单
     * @param query
     */
    Page<OrderDTO> page(OrderQueryDTO query);
    /**
     * 获取订单状态
     * @param orderId
     */
    int getOrderStatus(String orderId);

}
