package com.noble.tardis.modules.authentication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.noble.tardis.modules.authentication.interceptors.AddUserToModelInterceptor;

@Configuration
public class AuthenticationWebConfig implements WebMvcConfigurer {

    @Autowired
    AddUserToModelInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).excludePathPatterns("/api/**", "/public/**", "/session",
                "/forgot-password").addPathPatterns("/**");
    }

}
