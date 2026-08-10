package com.example;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import java.util.Collections;

public class CodeGennerator {
    public static void main(String[] args) {
        String url = "jdbc:mysql://192.168.150.101:3306/shop_user?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true";
        String username = "root";
        String password = "root";
        String projectPath = "D:/java/mall-micro-cloud/mall-service/mall-user-service/";
        String javaOutputDir = projectPath + "src/main/java";
        String xmlOutputDir = projectPath + "src/main/resources/mapper";
        
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("yy")
                            .disableOpenDir()
                            .outputDir(javaOutputDir);
                })
                .packageConfig(builder ->
                        builder.parent("com.example.mall.service")
                                .entity("domain.entity")
                                .mapper("mapper")
                                .service("service")
                                .serviceImpl("service.impl")
                                .pathInfo(Collections.singletonMap(OutputFile.xml, xmlOutputDir))
                )
                .strategyConfig(builder -> {
                    builder.addInclude("login_log","shipping_address","user_info")
                            .entityBuilder()
                            .enableLombok()
                            .enableTableFieldAnnotation()
                            .controllerBuilder()
                            .enableRestStyle()
                            .serviceBuilder()
                            .mapperBuilder();
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();

        System.out.println("Code generation completed successfully!");
    }
}