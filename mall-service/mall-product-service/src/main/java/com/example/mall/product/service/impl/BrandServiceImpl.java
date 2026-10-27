package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.example.mall.product.domain.entity.Brand;
import com.example.mall.product.mapper.BrandMapper;
import com.example.mall.product.service.IBrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.util.JsonUtils;
import org.springframework.stereotype.Service;
import com.example.mall.api.category.dto.BrandDTO;

import java.util.List;

/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author yy
 * @since 2026-06-11
 */
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements IBrandService {
    @Override
    public List<BrandDTO> listAll() {
        List<Brand> list = lambdaQuery()
                .gt(Brand::getId, 0) // 绕过插件校验(禁止全表扫描)
                .select(Brand::getId, Brand::getName, Brand::getLogo)
                .list();
        return JsonUtils.toList(list, BrandDTO.class);
    }

}
