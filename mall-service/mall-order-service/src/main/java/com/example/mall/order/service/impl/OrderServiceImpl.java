package com.example.mall.order.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.exception.BusinessException;
import com.example.mall.order.domain.po.OrderInfo;
import com.example.mall.order.domain.po.OrderItems;
import com.example.mall.order.service.IMessageSendService;
import com.example.mall.order.service.IOrderInfoService;
import com.example.mall.order.service.IOrderItemsService;
import com.example.mall.api.pay.model.PayCreateModel;
import com.example.mall.order.service.IOrderService;
import com.example.mall.util.JsonUtils;
import com.example.mall.vo.Result;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.mall.api.cart.feign.CartFeignClient;
import com.example.mall.api.product.feign.SkuInfoFeignClient;
import com.example.mall.api.order.dto.OrderCreateDTO;
import com.example.mall.api.order.dto.OrderDTO;
import com.example.mall.api.order.dto.OrderItemsDTO;
import com.example.mall.api.order.dto.OrderQueryDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaUpdate;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private IOrderInfoService orderInfoService;
    @Autowired
    private IOrderItemsService orderItemsService;
    @Autowired
    private SkuInfoFeignClient skuInfoFeignClient;
    @Autowired
    private CartFeignClient cartFeignClient;
    @Autowired
    private IMessageSendService messageSendService;
    @Override
    @GlobalTransactional
    public PayCreateModel create(OrderCreateDTO orderCreateDTO) throws Exception {
        // 1. 分离数据
        // 1.1 订单数据
        OrderInfo orderInfo = JsonUtils.toObj(orderCreateDTO, OrderInfo.class);
        // 1.2 订单明细数据
        List<OrderItems> orderItemsList = JsonUtils.toList(orderCreateDTO.getOrderItemsList(), OrderItems.class);
        // 1.3 购物车ID集合
        List<String> cartIds = JsonUtils.toList(orderCreateDTO.getCartIds(), String.class);
        //库存扣减
        Map<Long, Integer> deductMap = orderItemsList.stream().collect(
                Collectors.toMap(OrderItems::getSkuId, OrderItems::getQuantity)
        );
        Result stockResult = skuInfoFeignClient.deductStock(deductMap);
        if (!stockResult.getCode().equals(ResultCodeEnum.SUCCESS.getCode())) {
            throw new BusinessException(stockResult.getCode(),stockResult.getMsg());
        }
        // 2. 保存订单数据
        String orderId = IdWorker.getIdStr();
        double totalAmount = 0.0;
        for (OrderItems item : orderItemsList) {
            double amount = item.getPrice() * item.getQuantity();
            item.setAmount(amount);
            item.setOrderId(orderId);
            totalAmount += amount;
        }
        orderItemsService.saveBatch(orderItemsList);
        orderInfo.setOrderId(orderId);
        orderInfo.setTotalAmount(totalAmount);
        orderInfoService.save(orderInfo);
        // 3. 清除购物车（直接发送，try-catch 包裹避免消息失败导致事务回滚）
        try {
            messageSendService.sendDeleteCartMsg(orderId, cartIds);
        } catch (Exception e) {
            // 消息发送失败不影响订单创建，可记录日志后续补偿
            // log.error("发送删除购物车消息失败, orderId={}", orderId, e);
        }
        // 4. 创建支付数据
        PayCreateModel payCreateDTO = createPayCreateModel(orderId, orderItemsList);
        return payCreateDTO;
    }

    private PayCreateModel createPayCreateModel(String orderId, List<OrderItems> orderItemsList) {
        PayCreateModel payCreateDTO = new PayCreateModel();
        payCreateDTO.setOrderId(orderId);
        StringBuffer body = new StringBuffer();
        double tatolAmount = 0.0;
        for (OrderItems item : orderItemsList) {
            body.append(item.getSkuName()+"\t"+item.getQuantity()+"\t"+item.getPrice()+"\n");
            tatolAmount += item.getAmount();
        }
        payCreateDTO.setBody(body.toString());
        payCreateDTO.setTotalAmount(tatolAmount);
        payCreateDTO.setSubject("订单已支付");
        return payCreateDTO;
    }

    @Override
    public Page<OrderDTO> page(OrderQueryDTO query) {
        //获取订单合集
        Page<OrderDTO> result = orderInfoService.page(query);
        List<OrderDTO> orderList = result.getRecords();

        //封装订单
        List<OrderDTO> records = orderList.stream().map(order -> {
            List<OrderItemsDTO> orderItems = orderItemsService.listByOrderId(order.getOrderId());
            order.setOrderItemsList(orderItems);
            return order;
        }).collect(Collectors.toList());
        result.setRecords(records);
        //封装数据
        return result;
    }

    @Override
    public void removeById(String orderId) {
        orderInfoService.removeById(orderId);
        orderItemsService.removeByOrderId(orderId);
    }

}
