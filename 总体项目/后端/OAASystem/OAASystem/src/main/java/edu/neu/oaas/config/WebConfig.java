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
        // 使用项目内的静态资源路径，避免硬编码绝对路径
        String projectPath = System.getProperty("user.dir");
        
        // 头像资源
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations("classpath:/static/avatar/", 
                                    "file:" + projectPath + "/src/main/resources/static/avatar/",
                                    "file:./avatar/",
                                    "file:D:/avatar/"); // 兼容 saveAvatar 默认目录
        
        // 课程封面
        registry.addResourceHandler("/CourseCover/**")
                .addResourceLocations("classpath:/static/CourseCover/",
                                    "file:" + projectPath + "/src/main/resources/static/CourseCover/",
                                    "file:./CourseCover/",
                                    "file:E:/OAASystem/OAASystem/src/main/resources/static/CourseCover/"); // 兼容 CourseService 默认目录
        
        // 课程视频
        registry.addResourceHandler("/Video/**")
                .addResourceLocations("classpath:/static/Video/",
                                    "file:" + projectPath + "/src/main/resources/static/Video/",
                                    "file:./Video/");
        
        // 租户图标
        registry.addResourceHandler("/tenant-icons/**")
                .addResourceLocations("classpath:/static/tenant-icons/",
                                    "file:" + projectPath + "/src/main/resources/static/tenant-icons/",
                                    "file:./tenant-icons/");
        
        // 通用图标
        registry.addResourceHandler("/icons/**")
                .addResourceLocations("classpath:/static/icons/",
                                    "file:" + projectPath + "/src/main/resources/static/icons/",
                                    "file:./icons/");
        
        // 会议封面
        registry.addResourceHandler("/ConferenceCover/**")
                .addResourceLocations("classpath:/static/ConferenceCover/",
                                    "file:" + projectPath + "/src/main/resources/static/ConferenceCover/",
                                    "file:./ConferenceCover/");
        
        // 图片资源
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/",
                                    "file:" + projectPath + "/src/main/resources/static/images/",
                                    "file:./images/");
        
        // 添加通用静态资源处理
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        System.out.println("📁 静态资源配置已更新，项目路径: " + projectPath);
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("utf-8");
        multipartResolver.setMaxUploadSize(500000000); // 500 MB
        return multipartResolver;
    }
}



