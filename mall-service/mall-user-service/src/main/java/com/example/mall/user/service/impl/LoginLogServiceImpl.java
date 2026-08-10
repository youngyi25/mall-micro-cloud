package com.example.mall.user.service.impl;

import com.example.mall.user.domain.entity.LoginLog;
import com.example.mall.user.mapper.LoginLogMapper;
import com.example.mall.user.service.ILoginLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户登录日志表 服务实现类
 * </p>
 *
 * @author yy
 * @since 2026-07-08
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements ILoginLogService {

}
