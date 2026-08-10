package com.example.mall.seckill.controller;

import com.example.mall.seckill.service.IPageService;
import com.example.mall.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/page")
public class PageController {
    @Autowired
    private IPageService pageService;
    @GetMapping("/generate/{activityId}")
    public Result<List<String>> generateProductTemplate(@PathVariable Integer activityId) throws Exception {
        List<String> urls = pageService.generateProductTemplate(activityId);
        return Result.success(urls);
    }
}
