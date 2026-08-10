package com.example.mall.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.mall.product.ProductServiceApplication;
import com.example.mall.product.service.IProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.mall.api.product.dto.ProductQueryDTO;

@SpringBootTest(classes = ProductServiceApplication.class)
public class ProductServiceTest {
    @Autowired
    private IProductService productService;
    @Test
    public void testPage() {
        ProductQueryDTO queryDTO = new ProductQueryDTO();
        queryDTO.setPageNum(1L);
        queryDTO.setPageSize(3L);
        //queryDTO.setProductName("手机");
        //queryDTO.setBrandId(1L);
        //queryDTO.setThirdCategoryId(61);
        //queryDTO.setSecondCategoryId(13);
        queryDTO.setFirstCategoryId(2);
        queryDTO.setMinPrice(1000.0);
        queryDTO.setMaxPrice(5000.0);
        IPage page = productService.page(queryDTO);
        System.out.println(page.getTotal());
        System.out.println(page.getRecords());
    }
}
