package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.example.mall.product.domain.entity.Brand;
import com.example.mall.product.domain.entity.CategoryBrand;
import com.example.mall.product.mapper.CategoryBrandMapper;
import com.example.mall.product.service.IBrandService;
import com.example.mall.product.service.ICategoryBrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.product.service.ICategoryService;
import com.example.mall.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.mall.api.category.dto.BrandDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yy
 * @since 2026-06-11
 */
@Service
public class CategoryBrandServiceImpl extends ServiceImpl<CategoryBrandMapper, CategoryBrand> implements ICategoryBrandService {
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IBrandService brandService;
    @Override
    public List<BrandDTO> listByFirstCategoryId(Integer categoryId) {
        List<BrandDTO> brandDTOs = categoryService.listChildren(categoryId)
                .stream()
                // 每个二级分类  List<BrandDTO>，flatMap扁平化(打散为流，合并为一个集合流)
                .flatMap(category -> listBySecondCategoryId(category.getId()).stream())
                .distinct() // // 对象去重（依赖BrandDTO重写equals+hashCode）
                .collect(Collectors.toList());
        return brandDTOs;

    }

    @Override
    public List<BrandDTO> listBySecondCategoryId(Integer categoryId) {

        List<BrandDTO> brandDTOs = categoryService.listChildren(categoryId)
                .stream()
                // 每个三级分类  List<BrandDTO>，flatMap扁平化(打散为流，合并为一个集合流)
                .flatMap(category -> listByThirdCategoryId(category.getId()).stream())
                .distinct() // // 对象去重（依赖BrandDTO重写equals+hashCode）
                .collect(Collectors.toList());
        return brandDTOs;
    }

    @Override
    public List<BrandDTO> listByThirdCategoryId(Integer categoryId) {
        // 创建条件构造器
        LambdaQueryChainWrapper<CategoryBrand> query = new LambdaQueryChainWrapper(baseMapper);
        // 查询类别ID下的所有品牌ID集合
        List<Integer> brandIds = query.eq(CategoryBrand::getCategoryId, categoryId)
                .select(CategoryBrand::getBrandId)
                .list()
                .stream()
                .map(CategoryBrand::getBrandId)
                .collect(Collectors.toList());
        // 查询品牌ID集合下的所有品牌
        List<Brand> list = brandService.listByIds(brandIds);
        // 转换成DTO
        List<BrandDTO> brandDTOs = JsonUtils.toList(list, BrandDTO.class);
        return brandDTOs;
    }

}
