package com.example.mall.interceptor;

import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.vo.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class AuthorizationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求头获取网关密钥
        String secret = request.getHeader("X-Gateway-Secret");
        if(secret == null || !secret.equals("mall-micro-8080")){
            ObjectMapper objectMapper = new ObjectMapper();
            response.setContentType("application/json;charset=UTF-8");
            objectMapper.writeValue(response.getWriter(), Result.fail(ResultCodeEnum.FORBIDDEN));
            return false;
        }
        return true;
    }
}
