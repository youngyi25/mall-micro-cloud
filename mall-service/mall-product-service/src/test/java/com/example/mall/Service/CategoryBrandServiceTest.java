package com.example.mall.Service;

import com.example.mall.product.ProductServiceApplication;
import com.example.mall.product.service.ICategoryBrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.mall.api.category.dto.BrandDTO;

import java.util.List;

@SpringBootTest(classes = ProductServiceApplication.class)
public class CategoryBrandServiceTest {
    @Autowired
    private ICategoryBrandService categoryBrandService;
    @Test
    public void listByFirstCategoryId() {
        List<BrandDTO> list = categoryBrandService.listByFirstCategoryId(2);
        System.out.println(list);
    }
}
