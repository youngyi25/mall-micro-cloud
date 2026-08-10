package com.example.mall.user.service;

import com.example.mall.user.domain.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mall.user.dto.UserInfoDTO;

/**
 * <p>
 * 后台用户表 服务类
 * </p>
 *
 * @author yy
 * @since 2026-07-08
 */
public interface IUserInfoService extends IService<UserInfo> {
    /**
     * 验证用户名和密码
     * @param username
     * @param password
     * @return
     */
    UserInfoDTO validate(String username, String password);

}
