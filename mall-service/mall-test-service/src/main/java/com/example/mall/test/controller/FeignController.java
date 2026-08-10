package com.example.mall.test.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.mall.test.service.feign.IFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.mall.api.category.dto.CategoryDTO;
import com.example.mall.api.user.dto.ShippingAddressDTO;

import java.util.List;

@RestController
@RequestMapping("/feign")
public class FeignController {
    @Autowired
    private IFeignService feignService;
    @SentinelResource("feignListAddressByUserId")
    @GetMapping("/listAddressByUserId/{userId}")
    public List<ShippingAddressDTO> listAddressByUserId(@PathVariable String userId) {
        return feignService.listAddressByUserId(userId);
    }
    @GetMapping("/listLevelOne")
    public List<CategoryDTO> listLevelOne() {
        return feignService.listLevelOne();
    }
    @GetMapping("/listChildren/{parentId}")
    public List<CategoryDTO> listChildren(@PathVariable Integer parentId) {
        return feignService.listChildren(parentId);
    }
}
