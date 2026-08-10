package com.example.mall.advice;

import com.alibaba.fastjson2.JSON;
import com.example.mall.vo.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
@ControllerAdvice

public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {
    // 是否开启统一包装：true=执行beforeBodyWrite
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 排除已手动返回Result的接口，避免重复嵌套 Result<Result>
        //排除OpenAPI文档接口，避免包装导致Knife4j解析失败
        return !returnType.getParameterType().equals(Result.class) && !isOpenApiRequest();
    }

    // 统一封装返回体
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // 处理String特殊情况：Spring序列化String不走统一封装，单独处理
        if(body instanceof String){
            return JSON.toJSONString(Result.success(body));
        }
        return Result.success(body);
    }

    /**
     * 判断当前请求是否为OpenAPI文档接口
     * @return
     */
    private boolean isOpenApiRequest() {
        // 从当前请求上下文获取请求路径
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return false;
        }
        String path = attributes.getRequest().getRequestURI();
        // 匹配OpenAPI文档相关路径
        return path.contains("/v3/api-docs")
                || path.contains("/swagger-ui")
                || path.contains("/doc.html");
    }
}
