package com.group2.volunteer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private RoleInterceptor roleInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns(
                        "/admin/**",
                        "/projects/create/**",
                        "/organizer/**",
                        "/attendance/submit",
                        "/attendance/submit/**",
                        "/attendance/verify",
                        "/attendance/verify/**",
                        "/profile/**"
                )
                .excludePathPatterns("/login", "/logout", "/", "/css/**", "/js/**");
    }
}
