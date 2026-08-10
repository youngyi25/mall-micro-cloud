package com.example.mall.test.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.mall.test.domain.dto.ConnectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private ConnectDTO connect;
    @SentinelResource("testConnect")
    @GetMapping("/connect")
    public ConnectDTO getConnect(){
        return connect;
    }
}
