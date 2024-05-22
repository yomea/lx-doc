package com.laxqnsys.core.config;

import com.laxqnsys.core.converter.StringToLongConverter;
import com.laxqnsys.core.interceptor.LoginHandlerInterceptor;
import com.laxqnsys.core.manager.service.UserLoginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wuzhenhong
 * @date 2024/5/14 9:13
 */
@Component
public class WebMvcConfigure implements WebMvcConfigurer {

    @Autowired
    private UserLoginManager userLoginManager;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LoginHandlerInterceptor loginHandlerInterceptor = new LoginHandlerInterceptor(userLoginManager);
        loginHandlerInterceptor.addWhiteUrl("/api/login");
        loginHandlerInterceptor.addWhiteUrl("/api/register");
        loginHandlerInterceptor.addWhiteUrl("/static/**");
        loginHandlerInterceptor.addWhiteUrl("/system/error");
        registry.addInterceptor(loginHandlerInterceptor);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToLongConverter());
    }
}
