package com.example.mall.Service;

import com.example.mall.product.ProductServiceApplication;
import com.example.mall.product.service.ICategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.mall.api.category.dto.CategoryDTO;

import java.util.List;

@SpringBootTest(classes = ProductServiceApplication.class)
public class CategoryServiceTest {
    @Autowired
    private ICategoryService categoryService;
    @Test
    public void listAll() {
        List<CategoryDTO> categoryDTOS = categoryService.listAll();
        System.out.println(categoryDTOS);
    }
}
