package com.d2vfactory.throwmoney.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Autowired
    private RequestTimeInterceptor requestTimeInterceptor;

    @Autowired
    private HeaderToAttributeInterceptor headerToAttributeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestTimeInterceptor)
                .addPathPatterns("/**/api/**/");

        registry.addInterceptor(headerToAttributeInterceptor)
                .addPathPatterns("/**/api/**/");

    }
}
