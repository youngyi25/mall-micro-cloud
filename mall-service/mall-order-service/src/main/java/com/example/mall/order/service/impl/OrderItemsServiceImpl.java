package com.example.mall.order.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.example.mall.order.domain.po.OrderItems;
import com.example.mall.order.mapper.OrderItemsMapper;
import com.example.mall.order.service.IOrderItemsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.util.JsonUtils;
import org.springframework.stereotype.Service;
import com.example.mall.api.order.dto.OrderItemsDTO;

import java.util.List;

/**
 * <p>
 * 订单中所包含的商品 服务实现类
 * </p>
 *
 * @author yy
 * @since 2026-07-27
 */
@Service
public class OrderItemsServiceImpl extends ServiceImpl<OrderItemsMapper, OrderItems> implements IOrderItemsService {

    @Override
    public List<OrderItemsDTO> listByOrderId(String orderId) {
        //创建构造器
        List<OrderItems> list = lambdaQuery()
                .eq(OrderItems::getOrderId, orderId)
                .select(OrderItems::getSkuName, OrderItems::getImage, OrderItems::getPrice, OrderItems::getQuantity)
                .list();
        //转换dto
        return JsonUtils.toList(list,OrderItemsDTO.class);
    }

    @Override
    public void removeByOrderId(String orderId) {
        lambdaUpdate().eq(OrderItems::getOrderId, orderId).remove();
    }
}
