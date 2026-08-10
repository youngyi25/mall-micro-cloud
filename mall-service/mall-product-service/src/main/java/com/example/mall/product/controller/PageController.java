package com.example.mall.product.controller;

import com.example.mall.product.service.IPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/page")
public class PageController {
    @Autowired
    private IPageService pageService;
    @GetMapping("/generate/{spuId}")
    public String generateProductTemplate(@PathVariable Long spuId) throws Exception {
        String url = pageService.generateProductTemplate(spuId);
        return url;
    }
}
