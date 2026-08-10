package com.example.mall.product.service;

import com.example.mall.product.domain.entity.CategoryBrand;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.api.category.dto.BrandDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yy
 * @since 2026-06-11
 */
public interface ICategoryBrandService extends IService<CategoryBrand> {
    /**
     * 按一级类目ID查询品牌
     * @param categoryId 类目ID
     * @return 品牌集合
     */
    List<BrandDTO> listByFirstCategoryId(Integer categoryId);
    /**
     * 按二级类目ID查询品牌
     * @param categoryId 类目ID
     * @return 品牌集合
     */
    List<BrandDTO> listBySecondCategoryId(Integer categoryId);
    /**
     * 按三级类目ID查询品牌
     * @param categoryId 类目ID
     * @return 品牌集合
     */
    List<BrandDTO> listByThirdCategoryId(Integer categoryId);

}
