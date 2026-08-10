package com.example.mall.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.IllegalSQLInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class MyBatisPlusConfig {
    /**
     * 创建自动填充配置类实例
     */
    @Bean
    public AutoFieldFillHandler autoFieldFillHandler(){
        return new AutoFieldFillHandler();
    }

    /**
     * 自动填充配置类
     */
    class  AutoFieldFillHandler implements MetaObjectHandler {
        @Override
        public void insertFill(MetaObject metaObject) {
            // 自动填充创建时间字段
            fillStrategy(metaObject,"createTime", LocalDateTime.now());
            // 自动填充修改时间字段
            fillStrategy(metaObject,"updateTime", LocalDateTime.now());
            // 自动填充逻辑删除字段deleted:0未删除
            fillStrategy(metaObject,"deleted",0);
            // 自动填充状态:0 正常
            fillStrategy(metaObject,"status",0);
        }

        @Override
        public void updateFill(MetaObject metaObject) {
            // 自动填充修改时间字段
            fillStrategy(metaObject,"updateTime", LocalDateTime.now());
        }
    }
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页拦截器
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        // 添加非法SQL拦截器
        interceptor.addInnerInterceptor(new IllegalSQLInnerInterceptor());
        return interceptor;
    }
}
