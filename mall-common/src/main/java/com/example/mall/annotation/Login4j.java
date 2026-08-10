package com.example.mall.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Login4j {
    // 日志描述
    String value() default "用户登录";
}
