package com.example.mall.user.service;

import com.example.mall.user.domain.entity.ShippingAddress;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.api.user.dto.ShippingAddressDTO;

import java.util.List;

/**
 * <p>
 * 用户收货地址表 服务类
 * </p>
 *
 * @author yy
 * @since 2026-07-08
 */
public interface IShippingAddressService extends IService<ShippingAddress> {
    /**
     * 获取用户收货地址列表
     */
    List<ShippingAddressDTO> listById(String userId);

}
