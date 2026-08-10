package com.example.mall.user.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.mall.user.domain.entity.ShippingAddress;
import com.example.mall.user.service.IShippingAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.mall.api.user.dto.ShippingAddressDTO;

import java.util.List;

/**
 * <p>
 * 用户收货地址表 前端控制器
 * </p>
 *
 * @author yy
 * @since 2026-07-08
 */
@Tag(name = "用户收货地址模块")
@RestController
@RequestMapping("/shippingAddress")
public class ShippingAddressController {
    @Autowired
    private IShippingAddressService shippingAddressService;
    @Operation(summary = "获取用户收货地址")
    @SentinelResource("shippingAddressListById")
    @GetMapping("/{userId}")
    public List<ShippingAddressDTO> listById(@PathVariable String userId) {
        return shippingAddressService.listById(userId);
    }
    @Operation(summary = "保存或修改用户收货地址")
    @SentinelResource("shippingAddressSaveOrUpdate")
    @PostMapping("/saveOrUpdate")
    public boolean saveOrUpdate(@RequestBody ShippingAddress shippingAddress) {
        return shippingAddressService.saveOrUpdate(shippingAddress);
    }

}
