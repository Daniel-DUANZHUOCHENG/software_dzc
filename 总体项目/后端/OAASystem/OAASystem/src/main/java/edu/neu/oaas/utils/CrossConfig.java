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
//        config.addAllowedOrigin("http://127.0.0.1:8848"); // 允许的域名
        config.addAllowedOrigin("http://localhost:5173"); // 允许的域名
        config.addAllowedOrigin("http://localhost:5174"); // 允许的域名
        config.addAllowedOrigin("http://localhost:9091"); // 允许的域名
        config.addAllowedOrigin("http://localhost:9092"); // 允许的域名
        config.addAllowedOrigin("http://localhost:9090"); // 允许的域名
        config.addAllowedOrigin("http://172.18.24.58:5173/"); // 允许的域名
        config.addAllowedHeader("*"); // 允许的请求头
        config.addAllowedMethod("*"); // 允许的请求方法
        config.setAllowCredentials(true); // 允许携带凭证

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 注册CORS配置

        return new CorsFilter(source);
    }
}

