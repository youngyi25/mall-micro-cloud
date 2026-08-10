package com.example.mall.user.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.example.mall.user.domain.entity.UserInfo;
import com.example.mall.user.service.IUserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 后台用户表 前端控制器
 * </p>
 *
 * @author yy
 * @since 2026-07-08
 */
@Tag(name="用户模块")
@RestController
@RequestMapping("/userInfo")
public class UserInfoController {
    @Autowired
    private IUserInfoService userInfoService;
    @Operation(summary = "用户注册")
    @SentinelResource("userInfoRegister")
    @PostMapping("/register")
    public Boolean register(@RequestBody UserInfo userInfo){
        // 密码加密
        String hashpw = BCrypt.hashpw(userInfo.getPassword(),BCrypt.gensalt());
        userInfo.setPassword(hashpw);
        // 保存到数据表
        return userInfoService.save(userInfo);
    }
    @Operation(summary = "用户信息修改")
    @SentinelResource("userInfoUpdate")
    @PutMapping("/update")
    public Boolean update(@RequestBody UserInfo userInfo){
        // 密码加密
        if (userInfo.getPassword() != null){
            String hashpw = BCrypt.hashpw(userInfo.getPassword(),BCrypt.gensalt());
            userInfo.setPassword(hashpw);
        }
        return userInfoService.updateById(userInfo);
    }

}
