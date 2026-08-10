package com.example.mall.product.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.mall.product.domain.entity.Category;
import com.example.mall.product.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.mall.api.category.dto.CategoryDTO;

import java.util.List;

/**
 * <p>
 * 商品类目 前端控制器
 * </p>
 *
 * @author yy
 * @since 2026-06-11
 */
@Tag(name = "类别模块",description = "类别增删改查接口")
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;
    @Operation(summary = "获取一级类目",description = "获取一级类目接口")
    @SentinelResource("categoryListLevelOne")
    @GetMapping("/listLevelOne")
    public List<CategoryDTO> listLevelOne() {
        return categoryService.listLevelOne();
    }
    @Operation(
            summary = "获取子级类目",
            description = "获取子级类目接口",
            parameters = {
                    @Parameter(name = "parentId",description = "父级类目ID" ,example = "2")
            }
    )
    @SentinelResource("categoryListChildren")
    @GetMapping("/listChildren/{parentId}")
    public List<CategoryDTO> listChildren(@PathVariable Integer parentId) {
        return categoryService.listChildren(parentId);
    }
    @SentinelResource("categoryGetOnChildren")
    @GetMapping("/getOnChildren")
    public CategoryDTO getOnChildren(
            @RequestParam(required = true) Integer parentId,
            @RequestParam(defaultValue = "手机通讯") String name
    ) {
        List<CategoryDTO> categoryDTOS = categoryService.listChildren(parentId);
        return categoryDTOS.stream()
                .filter(categoryDTO -> categoryDTO.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
    @Operation(summary = "添加类目",description = "添加类目接口")
    @SentinelResource("categorySave")
    @PostMapping("/save")
    public boolean save(@RequestBody Category category) {
        return categoryService.save(category);
    }
    @Operation(summary = "获取所有类目",description = "获取所有类目接口")
    @SentinelResource("categoryListAll")
    @GetMapping("/listAll")
    public List<CategoryDTO> listAll() {
        return categoryService.listAll();
    }
}
