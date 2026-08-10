package com.example.mall.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 显式声明 OpenAPI servers 地址为网关聚合前缀 /api/user（相对路径）。
 *
 * 作用：Knife4j 网关聚合时，各服务通过 /api/user/v3/api-docs 暴露文档；
 * 前端 Swagger UI 拿到 servers.url 后会以它为 base 发请求。
 * 默认 springdoc 会填后端真实地址（如 http://192.168.150.1:9002），
 * 导致前端直接请求 http://{gateway}/login、/shippingAddress/116 等，
 * 网关找不到路由 → 404 "No static resource xxx"。
 *
 * 改为相对路径 /api/user 后，浏览器会拼成 http://{gatewayHost}/api/user/...，
 * 网关的 /api/user/** 路由即可命中并转发。
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server gatewayServer = new Server();
        gatewayServer.setUrl("/api/user");
        gatewayServer.setDescription("网关聚合入口");

        return new OpenAPI()
                .info(new Info()
                        .title("用户服务 API")
                        .version("v1")
                        .description("用户登录、注册、用户信息、收货地址、登录日志"))
                .servers(List.of(gatewayServer));
    }
}
