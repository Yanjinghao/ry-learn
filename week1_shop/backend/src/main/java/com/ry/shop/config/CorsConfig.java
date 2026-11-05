// 跨域配置

package com.ry.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许的前端域名(开发环境)
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://127.0.0.1:5173");
        // 允许带 Cookie（现在用 JWT Header，一般 false 也行）
        // allowCredentials(true) 时，allowedOrigin 不能写 *，必须写具体前端地址
        config.setAllowCredentials(true);
        // 允许的头(所有头)（必须包含 Authorization）
        config.addAllowedHeader("*");
        // 允许的方法(所有方法)
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}