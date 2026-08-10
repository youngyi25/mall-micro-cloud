package com.example.mall.Service;

import com.example.mall.product.ProductServiceApplication;
import com.example.mall.product.service.ISaleAttributeValueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ProductServiceApplication.class)
public class SaleAttributeValueServiceTest {
    @Autowired
    private ISaleAttributeValueService saleAttributeValueService;
    @Test
    public void listBySpuId() {
        System.out.println(saleAttributeValueService.listBySpuId(1L));
    }
}
