package com.example.mall.product.service;

import com.example.mall.product.domain.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.api.category.dto.CategoryDTO;

import java.util.List;

/**
 * <p>
 * 商品类目 服务类
 * </p>
 *
 * @author yy
 * @since 2026-06-11
 */
public interface ICategoryService extends IService<Category> {
    /**
     * 查询一级商品类目
     *
     * @return 商品类目集合
     */
    List<CategoryDTO> listLevelOne();
    /**
     * 查询子级商品类目
     *
     * @param parentId 上一级商品类目ID
     * @return 商品类目集合
     */
    List<CategoryDTO> listChildren(Integer parentId);
    /**
     * 查询所商品类目
     *
     * @return 商品类目详情
     */
    List<CategoryDTO> listAll();

}
