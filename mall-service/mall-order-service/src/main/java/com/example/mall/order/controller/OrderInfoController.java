package com.example.mall.order.controller;


import com.example.mall.order.domain.dto.OrderInfoUpdateDTO;
import com.example.mall.order.domain.po.OrderInfo;
import com.example.mall.order.service.IOrderInfoService;
import com.example.mall.util.JsonUtils;
import com.example.mall.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author yy
 * @since 2026-07-27
 */
@RestController
@RequestMapping("/order-info")
public class OrderInfoController {
    @Autowired
    private IOrderInfoService orderInfoService;
    /**
     * 修改订单
     * @param orderInfoUpdateDTO
     * @return
     */
    @PutMapping("/update")
    public Result update(@RequestBody OrderInfoUpdateDTO orderInfoUpdateDTO){
        // 转换
        OrderInfo orderInfo = JsonUtils.toObj(orderInfoUpdateDTO, OrderInfo.class);
        // 修改
        orderInfoService.updateById(orderInfo);
        return Result.success();
    }
    /**
     * 获取订单状态
     * @param orderId
     * @return
     */
    @GetMapping("/getOrderStatus/{orderId}")
    public int getOrderStatus(@PathVariable String orderId){
        return orderInfoService.getOrderStatus(orderId);
    }
}
