package com.lxqnsys.doc.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.lxqnsys.doc.handler.CustomLocalDateTimeTypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.context.annotation.Configuration;

/**
 * Description：Mybatis配置
 * @version 1.0.0
 * @date 2021/10/8
 */
@Configuration
public class MybatisConfigurationCustomizer implements ConfigurationCustomizer {
 
    @Override
    public void customize(MybatisConfiguration configuration) {
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        typeHandlerRegistry.register(CustomLocalDateTimeTypeHandler.class);
    }
}