package com.example.mall.test.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @SentinelResource("orderQuery")
    @GetMapping("/query")
    public String queryInfo(){
        log.info("查询订单数据");
        return "正常查询订单数据";
    }
    @PostMapping("/update")
    public String updateInfo() {
        log.info("创建订单数据");
        return "正常创建订单数据";
    }
    @GetMapping("/degrade")
    public String degradeTest() throws InterruptedException {
        log.info("降级测试");
        // 模拟业务处理耗时：固定800ms，超过阈值500ms，判定为慢调用
        Thread.sleep(800);
        return "接口正常执行，业务请求处理完成";
    }
    @GetMapping("/errorDegrade")
    public String errorDegradeTest() {
        log.info("错误降级测试");
        // 模拟随机业务异常
        int count = (int) (Math.random() * 10);
        if (count % 2 == 0) {
            int i = 1 / 0; // 偶数次请求抛异常
        }
        return "接口正常执行";
    }
}
