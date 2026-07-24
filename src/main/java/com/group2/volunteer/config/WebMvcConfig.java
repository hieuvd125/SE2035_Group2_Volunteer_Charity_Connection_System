package com.group2.volunteer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/donations/**", "/saved-projects/**");

        registry.addInterceptor(new RoleInterceptor("ADMIN"))
                .addPathPatterns("/admin/**");

        registry.addInterceptor(new RoleInterceptor("ORGANIZER"))
                .addPathPatterns("/organizer/**", "/projects/create/**", "/attendance/verify/**");

        registry.addInterceptor(new RoleInterceptor("VOLUNTEER"))
                .addPathPatterns("/volunteer/**", "/projects/apply/**", "/attendance/submit/**", "/profile/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("src/main/resources/static/uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        "file:/" + uploadPath + "/",
                        "file:uploads/",
                        "classpath:/static/uploads/"
                );
    }
}
