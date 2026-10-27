package com.example.mall.user.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.exception.BusinessException;
import com.example.mall.user.domain.entity.UserInfo;
import com.example.mall.user.mapper.UserInfoMapper;
import com.example.mall.user.service.IUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mall.util.JsonUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import com.example.mall.user.dto.UserInfoDTO;

/**
 * <p>
 * 后台用户表 服务实现类
 * </p>
 *
 * @author yy
 * @since 2026-07-08
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Override
    public UserInfoDTO validate(String username, String password) {
        // 创建条件构造器

        // 查询用户
        UserInfo userInfo = lambdaQuery().eq(UserInfo::getUsername, username)
                .eq(UserInfo::getStatus, 0)
                .one();
        // 验证用户和密码
        if (userInfo != null) {
            if (BCrypt.checkpw(password, userInfo.getPassword())){
                return JsonUtils.toObj(userInfo, UserInfoDTO.class);
            }else {
                throw new BusinessException(ResultCodeEnum.PASSWORD_ERR);
            }
        }else {
            throw new BusinessException(ResultCodeEnum.ACCOUNT_DISABLED);
        }
    }
}
