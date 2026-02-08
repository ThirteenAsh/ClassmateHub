package com.thirteenash.config;

import com.thirteenash.security.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加JWT拦截器，排除登录和注册接口
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**") // 需要验证的路径
                .excludePathPatterns("/api/v1/auth/login", "/api/v1/auth/register"); // 不需要验证的路径
    }
}