package com.example.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mall.domain.dto.CartDTO;
import com.example.mall.domain.dto.CartQueryDTO;
import com.example.mall.domain.entity.Cart;

import java.util.List;

public interface ICartService {
    /**
     * 添加到购物车
     *
     * @param cart
     */
    void addToCart(Cart cart);
    /**
     * 批量添加到购物车
     * @param carts
     */
    void addToCart(List<Cart> carts);
    /**
     * 获取购物车列表
     *
     * @return
     */
    Page<CartDTO> page(CartQueryDTO cartQueryDTO);
    /**
     * 获取用户购物车列表
     *
     * @param userId
     */
    List<CartDTO> list(String userId);
    /**
     * 删除购物车
     *
     * @param id
     */
    void removeCart(String id);
    /**
     * 批量删除购物车
     *
     * @param ids
     */
    void removeCart(List<String> ids);

}
