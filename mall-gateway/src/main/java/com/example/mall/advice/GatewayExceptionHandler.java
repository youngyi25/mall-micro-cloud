package com.example.mall.advice;

import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;

/**
 * 网关本地异常处理器。
 *
 * NoResourceFoundException 是 Spring WebFlux 的类（网关是 reactive 应用），
 * 写在 mall-common 里会被 Servlet 型服务共用而编译失败，因此放在网关模块内。
 *
 * 浏览器每次访问后台都会自动请求 /favicon.ico，网关没有匹配的路由会抛此异常。
 * 这里安静返回 404，避免被 mall-common 兜底的 Exception 处理器刷 error 日志 + 打栈。
 */
@Slf4j
@Order(-1)
@RestControllerAdvice
public class GatewayExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public Result<?> noResourceFound(NoResourceFoundException e) {
        return Result.fail(ResultCodeEnum.NOT_FOUND.getCode(), e.getMessage());
    }
}
