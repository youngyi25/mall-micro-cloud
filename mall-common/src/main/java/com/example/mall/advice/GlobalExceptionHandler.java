package com.example.mall.advice;

import com.example.mall.constant.ResultCodeEnum;
import com.example.mall.exception.BusinessException;
import com.example.mall.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 捕获自定义业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> businessException(BusinessException e) {
        log.error("【{}】{}", e.getClass().getSimpleName(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常（@Valid、@NotBlank等抛出MethodArgumentNotValidException）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> validException(MethodArgumentNotValidException e) {
        String errMsg = e.getBindingResult().getFieldError().getDefaultMessage();
        log.error("参数校验异常：{}", errMsg);
        return Result.fail(ResultCodeEnum.PARAM_ERROR.getCode(), errMsg);
    }

    /**
     * 兜底：所有未捕获的未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> globalException(Exception e) {
        log.error("全局异常：{}", e.getMessage());
        // 不再打印完整异常栈，避免噪音；排查时可临时放开 e.printStackTrace()
        return Result.fail(ResultCodeEnum.FAIL);
    }

    // 权限异常（SpringSecurity）
    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> accessDenied() {
        return Result.fail(ResultCodeEnum.UNAUTHORIZED);
    }
}
