package com.example.mall.test.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.example.mall.exception.BusinessException;
import com.example.mall.handler.ProductSentinelHandler;
import com.example.mall.test.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private IProductService productService;
    @PostMapping("/seckill")
    public String seckill() {
        productService.stockCheck("秒杀订单");
        return "秒杀成功";
    }
    @PostMapping("/order")
    public String order() {
        productService.stockCheck("普通订单");
        return "创建成功";
    }
    @GetMapping("/detail")
    @SentinelResource("productDetail")
    public String getProductDetail(
            @RequestParam Long productId,
            @RequestParam Long userId
    ) {
        log.info("商品详情查询，商品ID:{}，用户ID:{}", productId, userId);
        return "商品ID:" + productId + " 详情数据";
    }
    @GetMapping("/productStockCheck")
    @SentinelResource(
            value = "productStockCheck",
            blockHandler = "resourceBlockHandler",
            blockHandlerClass = ProductSentinelHandler.class,
            fallback = "resourceFallbackHandler",
            fallbackClass = ProductSentinelHandler.class
    )
    public String productStockCheck (Long productId) throws InterruptedException {
        // 模拟慢调用（用于测试熔断降级，耗时400-600ms）
        int time = (int)(Math.random() * 200) + 400;
        log.info("模拟慢调用，耗时{}", time);
        Thread.sleep(time);
        return "库存校验正常，商品ID：" + productId;
    }

    // blockHandler：限流/熔断统一兜底
    // 参数规则：原方法入参 + BlockException 放在最后
    public String resourceBlockHandler(Long productId, BlockException ex) {
        // 区分限流、熔断，返回不同提示文案
        if (ex instanceof FlowException) {
            // QPS限流触发
            log.warn("商品接口触发限流，商品ID:{}", productId);
            throw new BusinessException(429, "【限流保护】当前访问人数过多，请稍后重试");
        } else if (ex instanceof DegradeException) {
            // 熔断降级触发
            log.warn("商品接口触发熔断，商品ID:{}", productId);
            throw new  BusinessException(429, "【熔断降级】服务响应缓慢，临时关闭实时库存查询");
        } else {
            // 热点限流、权限拦截等其他Sentinel规则
            throw new  BusinessException(429, "请求被Sentinel拦截，请稍后操作");
        }
    }
    @GetMapping("/productFallbackStockCheck")
    @SentinelResource(
            value = "productFallbackStockCheck", // 核心：Sentinel识别的资源名
            blockHandler = "resourceBlockHandler", // 限流/熔断拦截兜底
            fallback = "resourceFallbackHandler"  // 业务代码异常兜底理
    )
    public String productFallbackStockCheck(Long productId) throws InterruptedException {
        // 场景1：模拟慢调用（用于测试熔断降级，耗时400-600ms）
        int time = (int)(Math.random() * 200) + 400;
        log.info("模拟异常数，耗时{}", time);
        Thread.sleep(time);
        // 模拟随机业务异常
        int count = (int) (Math.random() * 10);
        if (count % 2 == 0) {
            int i = 1 / 0; // 偶数次请求抛异常
        }
        return "库存校验正常，商品ID：" + productId;
    }

    //  fallback：业务代码异常兜底
    // 参数携带Throwable，可打印异常日志
    public String resourceFallbackHandler(Long productId, Throwable t) {
        log.error("库存校验业务异常，商品ID:{}，异常信息：{}", productId, t.getMessage());
        // 业务出错时返回缓存兜底数据
        throw new BusinessException(5003,"兜底缓存库存数据，商品ID：" + productId);
    }
}
