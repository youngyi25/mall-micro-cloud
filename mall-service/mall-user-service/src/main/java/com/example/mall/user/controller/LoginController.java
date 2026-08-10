package com.example.mall.user.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.mall.annotation.Login4j;
import com.example.mall.user.service.IUserInfoService;
import com.example.mall.util.JwtUtil;
import com.example.mall.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.mall.user.dto.LoginDTO;
import com.example.mall.user.dto.UserInfoDTO;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "用户登录模块")
@RestController
public class LoginController {
    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private JwtUtil jwtUtil;
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    @Login4j
    public UserInfoDTO login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        // 验证用户名和密码
        UserInfoDTO userInfoDTO = userInfoService.validate(loginDTO.getUsername(), loginDTO.getPassword());
        // 创建 token
        // 自定义载荷数据
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userInfoDTO.getId());
        claims.put("username", userInfoDTO.getUsername());
        claims.put("email", userInfoDTO.getEmail());
        // 生成Token
        String token = jwtUtil.login(Long.parseLong(String.valueOf(userInfoDTO.getId())), claims);
        // 将token写入响应头，带加到前端
        response.setHeader("Authorization", token);
        // 同时将token写入响应体，方便前端sessionStorage保存（避免CORS导致响应头丢失）
        userInfoDTO.setToken(token);
        return userInfoDTO;
    }
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result logout(Long userId) {
        jwtUtil.logout(userId);
        return Result.success();
    }
}