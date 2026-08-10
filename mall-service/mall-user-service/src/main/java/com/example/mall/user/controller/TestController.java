package com.example.mall.user.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "测试模块", description = "API文档测试接口")
@RestController
@RequestMapping("/test")
public class TestController {

    @Operation(summary = "测试接口", description = "用于测试API文档是否正常")
    @SentinelResource("userTestHello")
    @GetMapping("/hello")
    public String hello() {
        return "用户服务API文档测试成功！";
    }
}