package edu.neu.oaas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations("file:D:/avatar/");
        registry.addResourceHandler("/CourseCover/**")
                .addResourceLocations("file:E:/OAASystem/OAASystem/src/main/resources/static/CourseCover/");
        registry.addResourceHandler("/Video/**")
                .addResourceLocations("file:E:/OAASystem/OAASystem/src/main/resources/static/Video/");
        registry.addResourceHandler("/tenant-icons/**")
                .addResourceLocations("file:D:/tenant-icons/");
        registry.addResourceHandler("/icons/**")
                .addResourceLocations("file:E:/OAASystem/OAASystem/src/main/resources/static/icons/");
        registry.addResourceHandler("/ConferenceCover/**")
                .addResourceLocations("file:E:/OAASystem/OAASystem/src/main/resources/static/ConferenceCover/");
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:E:/OAASystem/OAASystem/src/main/resources/static/images/");

    }
    //这样配置后，当用户访问 /avatar/{fileName} 时，Spring Boot会从 D:/avatar/ 目录加载对应的文件。

//    @Bean
//    public MultipartResolver multipartResolver() {
//        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//        multipartResolver.setMaxUploadSize(10485760); // 10 MB
//        return multipartResolver;
//    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("utf-8");
        multipartResolver.setMaxUploadSize(500000000); //  MB
        return multipartResolver;
    }
}



