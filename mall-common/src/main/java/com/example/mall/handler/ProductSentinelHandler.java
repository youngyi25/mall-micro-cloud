package com.example.mall.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.example.mall.exception.BusinessException;

public class ProductSentinelHandler {
    // blockHandler：限流/熔断统一兜底
    // 参数规则：原方法入参 + BlockException 放在最后
    public static String resourceBlockHandler(Long productId, BlockException ex) {
        // 区分限流、熔断，返回不同提示文案
        if (ex instanceof FlowException) {
            // QPS限流触发
            throw new BusinessException(429, "【限流保护】当前访问人数过多，请稍后重试");
        } else if (ex instanceof DegradeException) {
            // 熔断降级触发
            throw new  BusinessException(429, "【熔断降级】服务响应缓慢，临时关闭实时库存查询");
        } else {
            // 热点限流、权限拦截等其他Sentinel规则
            throw new  BusinessException(429, "请求被Sentinel拦截，请稍后操作");
        }
    }


    //  fallback：业务代码异常兜底
    // 参数携带Throwable，可打印异常日志
    public static String resourceFallbackHandler(Long productId, Throwable t) {
        // 业务出错时返回缓存兜底数据
        throw new BusinessException(5003,"兜底缓存库存数据，商品ID：" + productId);
    }
}
