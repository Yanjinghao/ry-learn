// swagger配置

package com.ry.shop.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        String schemeName = "BearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("shop API")
                        .version("v0.1")
                        .description("spring-boot接口文档"))
                .components(new Components()
                        .addSecuritySchemes(schemeName,
                                new SecurityScheme()
                                        .name(schemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                // 默认所有接口带锁；登录/注册仍可匿名调
                .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }
}