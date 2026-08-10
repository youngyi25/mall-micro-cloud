package com.example.mall.test.service.feign;

import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.exception.BusinessException;
import com.example.mall.util.JsonUtils;
import com.example.mall.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.mall.api.category.feign.CategoryFeignClient;
import com.example.mall.api.category.dto.CategoryDTO;
import com.example.mall.api.user.dto.ShippingAddressDTO;
import com.example.mall.api.user.feign.ShippingAddressFeignClient;

import java.util.List;

@Service
public class FeignServiceImpl implements IFeignService{
    @Autowired
    private ShippingAddressFeignClient shippingAddressFeignClient;
    private CategoryFeignClient categoryFeignClient;
    @Override
    public List<ShippingAddressDTO> listAddressByUserId(String userId) {
        Result result = JsonUtils.toObj(shippingAddressFeignClient.listById(userId), Result.class);
        // 如果获取结果不成功，则抛出异常
        if (!result.getCode().equals(ResultCodeEnum.SUCCESS.getCode())) {
            throw new BusinessException(result.getCode(), result.getMsg());
        }
        return (List<ShippingAddressDTO> )result.getData();
    }
    @Override
    public List<CategoryDTO> listLevelOne() {
        Result result = JsonUtils.toObj(categoryFeignClient.listLevelOne(), Result.class);
        if (!result.getCode().equals(ResultCodeEnum.SUCCESS.getCode())) {
            throw new BusinessException(result.getCode(), result.getMsg());
        }
        return (List<CategoryDTO>)result.getData();
    }

    @Override
    public List<CategoryDTO> listChildren(Integer parentId) {
        Result result = JsonUtils.toObj(categoryFeignClient.listChildren(parentId), Result.class);
        if (!result.getCode().equals(ResultCodeEnum.SUCCESS.getCode())) {
            throw new BusinessException(result.getCode(), result.getMsg());
        }
        return (List<CategoryDTO>)result.getData();
    }
}
