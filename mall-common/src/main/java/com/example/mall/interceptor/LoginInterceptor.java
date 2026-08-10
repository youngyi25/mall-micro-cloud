package com.example.mall.interceptor;

import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.util.JwtUtil;
import com.example.mall.vo.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求头获取 Token（和登录接口保持一致：Authorization）
        String token = request.getHeader("Authorization");

        // 2. Token 为空 → 未登录，拦截
        if (token == null || token.isBlank()) {
            // 将response输出转换成json格式
            ObjectMapper objectMapper = new ObjectMapper();
            response.setContentType("application/json;charset=UTF-8");
            objectMapper.writeValue(response.getWriter(), Result.fail(ResultCodeEnum.UNAUTHORIZED));
            return false;
        }

        // 3. Token 格式错误/过期/被篡改 → 拦截
        if (!jwtUtil.validateToken(token)) {
            // 将response输出转换成json格式
            ObjectMapper objectMapper = new ObjectMapper();
            response.setContentType("application/json;charset=UTF-8");
            objectMapper.writeValue(response.getWriter(), Result.fail(ResultCodeEnum.UNAUTHORIZED));
            return false;
        }

        // 4. 解析Token，把用户信息存入 request 域，供 Controller 使用
        Claims claims = jwtUtil.parseToken(token);
        request.setAttribute("username", claims.get("username"));
        request.setAttribute("email", claims.get("email"));
        request.setAttribute("userId", claims.getSubject());
        // 5. 比较token的用户ID和数据库中的用户ID对应的token是否一致是否一致(后续补充)
        // 6. 全部校验通过，放行
        return true;
    }
}
