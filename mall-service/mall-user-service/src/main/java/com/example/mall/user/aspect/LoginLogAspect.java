package com.example.mall.user.aspect;

import com.example.mall.annotation.Login4j;
import com.example.mall.user.domain.entity.LoginLog;
import com.example.mall.user.dto.LoginDTO;
import com.example.mall.user.service.ILoginLogService;
import com.example.mall.util.ClientUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoginLogAspect {
    @Autowired
    private ILoginLogService loginLogService;

    /**
     * 环绕通知：拦截 @LoginLog 注解的方法
     * 需要在切点表达式中声明注解参数，并且必须按顺序：第一个参数是  ProceedingJoinPoint ，第二个参数才是注解
     * @param point 切点
     * @param login4j 注解对象
     *
     */
    @Around("@annotation(login4j)")
    public Object around(ProceedingJoinPoint point, Login4j login4j) throws Throwable {
        // 1. 获取request对象
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 2. 从请求参数获取用户名（入参是 LoginDTO）
        Object[] args = point.getArgs(); // 获取切点参数
        LoginDTO dto =(LoginDTO) args[0];
        // 3. 构建登录日志基础信息
        LoginLog loginLog = new LoginLog();
        loginLog.setUsername(dto.getUsername());
        loginLog.setIp(ClientUtil.getIpAddress(request));
        loginLog.setTerminal(ClientUtil.getClientInfo(request));

        // 4. 执行原登录方法
        try {
            Object result = point.proceed();
            // 4.1  登录成功，记录日志
            loginLog.setRemark(login4j.value() + "：成功");
            loginLogService.save(loginLog);
            return result;
        } catch (Throwable e) {
            // 4.2 登录失败，记录日志
            loginLog.setRemark(login4j.value() + "：失败，原因=" + e.getMessage());
            loginLogService.save(loginLog);
            throw e; // 继续抛出异常，交给全局异常处理器
        }
    }
}
