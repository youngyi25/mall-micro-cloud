package com.example.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.example.mall.product.domain.entity.Category;
import com.example.mall.product.mapper.CategoryMapper;
import com.example.mall.product.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.util.JsonUtils;
import org.springframework.stereotype.Service;
import com.example.mall.api.category.dto.CategoryDTO;

import java.util.List;

/**
 * <p>
 * 商品类目 服务实现类
 * </p>
 *
 * @author yy
 * @since 2026-06-11
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Override
    public List<CategoryDTO> listLevelOne() {
        // 创建条件构造器
        LambdaQueryChainWrapper<Category> query = new LambdaQueryChainWrapper<>(this.baseMapper);
        // 查询条件: 父级ID为空
        List<Category> list = query.isNull(Category::getParentId)
                .select(Category::getId, Category::getName)
                .list();
        // 转换为DTO
        List<CategoryDTO> listDTO = JsonUtils.toList(list, CategoryDTO.class);
        return listDTO;
    }

    @Override
    public List<CategoryDTO> listChildren(Integer parentId) {
        // 创建条件构造器
        LambdaQueryChainWrapper<Category> query = new LambdaQueryChainWrapper<>(this.baseMapper);
        // 查询条件: 父级ID=parentId
        List<Category> list = query.eq(Category::getParentId, parentId)
                .select(Category::getId, Category::getName)
                .list();
        // 转换为DTO
        List<CategoryDTO> listDTO = JsonUtils.toList(list, CategoryDTO.class);
        return listDTO;
    }

    @Override
    public List<CategoryDTO> listAll() {
        // 获取一级类目
        List<CategoryDTO> listLevelOne = listLevelOne();
        for (CategoryDTO categoryDTO : listLevelOne) {
            // 获取二级类目
            List<CategoryDTO> listChildren = listChildren(categoryDTO.getId());
            // 设置二级类目
            categoryDTO.setChildren(listChildren);
            // 获取三级类目
            for (CategoryDTO children : listChildren) {
                // 获取三级类目
                List<CategoryDTO> listLastChildren = listChildren(children.getId());
                // 设置三级类目
                children.setChildren(listLastChildren);
            }
        }
        return listLevelOne;
    }

}
