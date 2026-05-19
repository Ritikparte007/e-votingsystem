package com.example.votingsystem.config;

import com.example.votingsystem.interceptor.AdminIPInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminIPInterceptor adminIPInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminIPInterceptor)
                .addPathPatterns("/admin/**", "/admin", "/verify-voter/**", 
                                 "/add-election", "/toggle-election/**", "/add-candidate")
                .excludePathPatterns("/login", "/logout");
    }
}