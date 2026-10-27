package com.example.mall.user.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.example.mall.user.domain.entity.ShippingAddress;
import com.example.mall.user.mapper.ShippingAddressMapper;
import com.example.mall.user.service.IShippingAddressService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.util.JsonUtils;
import org.springframework.stereotype.Service;
import com.example.mall.api.user.dto.ShippingAddressDTO;

import java.util.List;

/**
 * <p>
 * 用户收货地址表 服务实现类
 * </p>
 *
 * @author yy
 * @since 2026-07-08
 */
@Service
public class ShippingAddressServiceImpl extends ServiceImpl<ShippingAddressMapper, ShippingAddress> implements IShippingAddressService {

    @Override
    public List<ShippingAddressDTO> listById(String userId) {
        // 创建条件构造器
        List<ShippingAddress> list = lambdaQuery().eq(ShippingAddress::getUserId, userId).list();
        // 返回DTO列表
        List<ShippingAddressDTO> dtos = JsonUtils.toList(list, ShippingAddressDTO.class);
        return dtos;
    }
    /**
     * 重写添加或修改用户地址的方法
     */
    @Override
    public boolean saveOrUpdate(ShippingAddress entity) {
        // 修改默认地址
        if (entity.getDefaultStatus() == 1) {
            // 创建条件构造器
            lambdaUpdate().eq(ShippingAddress::getUserId, entity.getUserId())
                    .set(ShippingAddress::getDefaultStatus, 0)
                    .update();
        }
        return super.saveOrUpdate(entity);

    }
}
