package com.example.mall.product.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ThymeleafController {
    @SentinelResource("thDemo")
    @GetMapping("/th/demo")
    public String demo(ModelMap model){
        // 用于封装模板引擎的数据
        model.put("name","张三");
        model.put("age",18);
        // 跳转到demo.html
        return "demo";
    }
}
