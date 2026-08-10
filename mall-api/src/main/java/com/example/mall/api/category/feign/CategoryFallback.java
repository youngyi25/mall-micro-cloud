package com.example.mall.api.category.feign;

import com.example.mall.vo.Result;
import org.springframework.stereotype.Component;
import com.example.mall.api.category.dto.CategoryDTO;

import java.util.List;

@Component
public class CategoryFallback implements CategoryFeignClient{
    @Override
    public Result<List<CategoryDTO>> listLevelOne() {
        // 创建兜底数据
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(0);
        categoryDTO.setName("未知分类");
        return Result.success(List.of(categoryDTO));
    }

    @Override
    public Result<List<CategoryDTO>> listChildren(Integer parentId) {
        // 创建兜底数据
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(0);
        categoryDTO.setName("未知分类");
        return Result.success(List.of(categoryDTO));
    }
}
