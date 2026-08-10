package com.example.mall.order.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.exception.BusinessException;
import com.example.mall.order.domain.po.OrderInfo;
import com.example.mall.order.domain.po.OrderItems;
import com.example.mall.order.mapper.OrderInfoMapper;
import com.example.mall.order.service.IOrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.order.service.IOrderItemsService;
import com.example.mall.util.JsonUtils;
import com.example.mall.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.mall.api.category.feign.CategoryFeignClient;
import com.example.mall.api.cart.feign.CartFeignClient;
import com.example.mall.api.product.feign.SkuInfoFeignClient;
import com.example.mall.api.order.dto.OrderCreateDTO;
import com.example.mall.api.order.dto.OrderDTO;
import com.example.mall.api.order.dto.OrderQueryDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author yy
 * @since 2026-07-27
 */
@Service
@RequiredArgsConstructor
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {

    @Override
    public Page<OrderDTO> page(OrderQueryDTO query) {
        // 创建条件构造器
        LambdaQueryChainWrapper<OrderInfo> queryWrapper = new LambdaQueryChainWrapper<>(this.baseMapper);
        // 拼接条件
        if (query.getUserId() != null){
            queryWrapper.eq(OrderInfo::getUserId, query.getUserId());
        }
        if (query.getStatus() != null){
            queryWrapper.eq(OrderInfo::getStatus, query.getStatus());
        }
        // 按创建时间降序
        queryWrapper.orderByDesc(OrderInfo::getCreateTime);
        // 筛选字段
        queryWrapper.select(OrderInfo::getOrderId, OrderInfo::getCreateTime, OrderInfo::getTotalAmount);
        // 分页查询
        Page<OrderInfo> page = new Page<>(query.getPageNum(), query.getPageSize());
        Page<OrderInfo> result = queryWrapper.page(page);
        // 转换DTO
        Page<OrderDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(JsonUtils.toList(result.getRecords(), OrderDTO.class));
        return dtoPage;
    }

    @Override
    public int getOrderStatus(String orderId) {
        OrderInfo orderInfo = this.getById(orderId);
        if (orderInfo != null){
            return orderInfo.getStatus();
        }
        return 0;
    }

}
