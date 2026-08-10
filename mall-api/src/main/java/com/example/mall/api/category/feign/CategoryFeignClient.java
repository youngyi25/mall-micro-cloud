package com.example.mall.api.category.feign;

import com.example.mall.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.mall.api.category.dto.CategoryDTO;
import com.example.mall.api.category.factory.CategoryFallbackFactory;

import java.util.List;

@FeignClient(
        contextId = "category-feign" ,
        name = "mall-product-service",
        fallback = CategoryFallback.class
)
public interface CategoryFeignClient {
    /**
     * 获取一级类目
     */
    @GetMapping("/category/listLevelOne")
    Result<List<CategoryDTO>> listLevelOne();
    /**
     * 获取子级类目
     * @param parentId
     */
    @GetMapping("/listChildren/{parentId}")
    Result<List<CategoryDTO>> listChildren(@PathVariable("parentId") Integer parentId);
}
