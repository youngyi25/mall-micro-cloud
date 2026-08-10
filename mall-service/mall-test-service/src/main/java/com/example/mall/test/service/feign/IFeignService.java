package com.example.mall.test.service.feign;

import com.example.mall.api.category.dto.CategoryDTO;
import com.example.mall.api.user.dto.ShippingAddressDTO;

import java.util.List;

public interface IFeignService {
    /**
     * 根据用户ID获取用户收货地址
     */
    List<ShippingAddressDTO> listAddressByUserId(String userId);
    /**
     * 获取一级类目
     */
    List<CategoryDTO> listLevelOne();
    /**
     * 获取子级类目
     */
    List<CategoryDTO> listChildren(Integer parentId);
}
