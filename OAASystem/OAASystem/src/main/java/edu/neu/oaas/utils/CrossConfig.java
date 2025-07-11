package edu.neu.oaas.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@Configuration
public class CrossConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许的域名 - 使用allowedOriginPatterns代替allowedOrigin("*")以支持credentials
        config.addAllowedOriginPattern("*"); // 允许所有域名
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://localhost:5174");
        config.addAllowedOrigin("http://localhost:5175"); // 添加前端实际端口
        config.addAllowedOrigin("http://127.0.0.1:5173");
        config.addAllowedOrigin("http://127.0.0.1:5174");
        config.addAllowedOrigin("http://127.0.0.1:5175");
        config.addAllowedOrigin("http://localhost:9091");
        config.addAllowedOrigin("http://localhost:9092");
        config.addAllowedOrigin("http://localhost:9090");
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://localhost:8081");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:3001");
        config.addAllowedOrigin("http://172.18.24.58:5173/");
        
        config.addAllowedHeader("*"); // 允许的请求头
        config.addAllowedMethod("*"); // 允许的请求方法
        config.setAllowCredentials(true); // 允许携带凭证

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 注册CORS配置

        return new CorsFilter(source);
    }
}

