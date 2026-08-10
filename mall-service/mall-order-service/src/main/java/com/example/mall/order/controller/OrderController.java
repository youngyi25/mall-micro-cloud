package com.example.mall.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mall.api.pay.model.PayCreateModel;
import com.example.mall.order.service.IOrderService;
import com.example.mall.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.mall.api.order.dto.OrderCreateDTO;
import com.example.mall.api.order.dto.OrderDTO;
import com.example.mall.api.order.dto.OrderQueryDTO;

@Tag(name = "订单服务")
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private IOrderService orderService;
    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result create(@RequestBody OrderCreateDTO orderCreateDTO) throws Exception {
        PayCreateModel payCreateDTO = orderService.create(orderCreateDTO);
        return Result.success(payCreateDTO);
    }
    @Operation(summary = "分页查询订单")
    @GetMapping("/page")
    public Result<Page<OrderDTO>> page(OrderQueryDTO query) {
        return Result.success(orderService.page(query));
    }


    @Operation(summary = "删除订单")
    @DeleteMapping("/remove/{orderId}")
    public Result removeById(@PathVariable String orderId) {
        orderService.removeById(orderId);
        return Result.success();
    }
}
