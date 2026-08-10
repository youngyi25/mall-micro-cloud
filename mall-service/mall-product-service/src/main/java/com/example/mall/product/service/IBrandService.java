package com.example.mall.product.service;

import com.example.mall.product.domain.entity.Brand;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.api.category.dto.BrandDTO;

import java.util.List;

/**
 * <p>
 * 品牌表 服务类
 * </p>
 *
 * @author yy
 * @since 2026-06-11
 */
public interface IBrandService extends IService<Brand> {
    /**
     * 获取所有类目的品牌
     * @return
     */
    List<BrandDTO> listAll();

}
