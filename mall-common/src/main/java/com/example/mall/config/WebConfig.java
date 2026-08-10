package com.example.mall.config;

import com.example.mall.interceptor.AuthorizationInterceptor;
import com.example.mall.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有接口
                .allowCredentials(true) // 是否发送 Cookie
                .allowedOriginPatterns("*") // 支持域
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS") // 放行请求方式
                .maxAge(3600) // OPTIONS预检有效期(秒)，1小时不用重复预检
                .allowedHeaders("*") // 放行所有请求头
                .exposedHeaders("*"); // 暴露所有请求头
    }
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;
    /**
     * 注册拦截器，可以注册多个拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 便于后续测试，暂时关闭微服务内部秘钥拦截器
        //registry.addInterceptor(authorizationInterceptor)
                //.addPathPatterns("/**") // 拦截所有地址（需要登录就能访问）
                // 放行swagger（不需要登录就能访问）
                //.excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**", "/doc.html", "/webjars/**")
                // 放行接口（不需要登录就能访问）
                //.excludePathPatterns("/login","/user/userInfo/register",
                  //      "/product/**","category/**","/brand/**",
                    //    "/page/**","/th/**","/feign/**","/connect/**");
    }
}