package com.laxqnsys.core.config;

import com.laxqnsys.core.interceptor.LoginHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LoginHandlerInterceptor loginHandlerInterceptor = new LoginHandlerInterceptor(stringRedisTemplate);
        loginHandlerInterceptor.addWhiteUrl("/user/login");
        loginHandlerInterceptor.addWhiteUrl("/user/register");
        loginHandlerInterceptor.addWhiteUrl("/static/**");
        registry.addInterceptor(loginHandlerInterceptor);
    }
}
