package com.example.shopping_website.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 处理静态资源映射
        registry.addResourceHandler("/product_images/**")
                .addResourceLocations("classpath:/static/product_images/");
        
        // 如果还需要处理上传目录外的其他静态资源
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/", "classpath:/static/uploads/");
        
        // 确保其他静态资源也能访问
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}