package com.example.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mall.domain.dto.CartDTO;
import com.example.mall.domain.dto.CartQueryDTO;
import com.example.mall.domain.entity.Cart;
import com.example.mall.service.ICartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "购物车接口")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final ICartService cartService;
    @Operation(summary = "添加购物车")
    @PostMapping("/add")
    public void addToCar(@RequestBody Cart cart) {
        cartService.addToCart(cart);
    }
    @Operation(summary = "批量添加购物车")
    @PostMapping("/addBatch")
    public void addToCar(@RequestBody List<Cart> carts) {
        cartService.addToCart(carts);
    }
    @Operation(summary = "获取购物车列表")
    @GetMapping("/page")
    public Page<CartDTO> page(CartQueryDTO cartQueryDTO) {
        return cartService.page(cartQueryDTO);
    }
    @Operation(summary = "获取用户购物车列表")
    @GetMapping("/list/{userId}")
    public List<CartDTO> list(@PathVariable String userId) {
        return cartService.list(userId);
    }
    @Operation(summary = "删除购物车")
    @DeleteMapping("/delete/{id}")
    public void removeCart(@PathVariable String id) {
        cartService. removeCart(id);
    }
    @Operation(summary = "批量删除购物车")
    @DeleteMapping("/deleteBatch")
    public void removeCart(@RequestBody List<String> ids) {
        cartService. removeCart(ids);
    }
}
