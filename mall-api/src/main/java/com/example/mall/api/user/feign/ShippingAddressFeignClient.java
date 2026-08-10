package com.example.mall.api.user.feign;

import com.example.mall.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.mall.api.user.dto.ShippingAddressDTO;

import java.util.List;

@FeignClient(
        name = "mall-user-service",
        contextId = "shipping-address-feign"
)
public interface ShippingAddressFeignClient {
    /**
     * 根据用户ID获取用户收货地址
     */
    @GetMapping("/shippingAddress/{userId}")
    Result<List<ShippingAddressDTO>> listById(@PathVariable("userId") String userId);
}
