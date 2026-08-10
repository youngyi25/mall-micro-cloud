package com.example.mall.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mall.api.order.dto.OrderCreateDTO;
import com.example.mall.api.order.dto.OrderDTO;
import com.example.mall.api.order.dto.OrderQueryDTO;
import com.example.mall.api.pay.model.PayCreateModel;

public interface IOrderService {
    /**
     * 生成订单
     * @param orderCreateDTO
     * @throws Exception
     */
    PayCreateModel create(OrderCreateDTO orderCreateDTO) throws Exception;
    /**
     * 分页查询订单
     * @param query
     * @return
     */
    Page<OrderDTO> page(OrderQueryDTO query);
    /**
     * 按订单ID删除订单
     * @param orderId
     */
    void removeById(String orderId);
}
