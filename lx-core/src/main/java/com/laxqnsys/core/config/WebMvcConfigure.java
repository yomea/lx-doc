package com.laxqnsys.core.config;

import com.laxqnsys.core.converter.StringToLongConverter;
import com.laxqnsys.core.interceptor.LoginHandlerInterceptor;
import com.laxqnsys.core.properties.LxDocWebProperties;
import com.laxqnsys.core.properties.StaticResourceProperties;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.FormatterRegistry;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wuzhenhong
 * @date 2024/5/14 9:13
 */
@Configuration
@EnableConfigurationProperties(value = LxDocWebProperties.class)
public class WebMvcConfigure implements WebMvcConfigurer {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private LxDocWebProperties lxDocWebProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LoginHandlerInterceptor loginHandlerInterceptor = new LoginHandlerInterceptor(stringRedisTemplate);
        List<String> whiteUrlList = Optional.ofNullable(lxDocWebProperties.getWhiteUrlList())
            .orElse(Collections.emptyList());
        whiteUrlList.stream().forEach(loginHandlerInterceptor::addWhiteUrl);
        registry.addInterceptor(loginHandlerInterceptor);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToLongConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        List<StaticResourceProperties> staticResources = lxDocWebProperties.getStaticResources();
        if (CollectionUtils.isEmpty(staticResources)) {
            return;
        }
        staticResources.stream().forEach(staticResource -> {
            String[] pathPatterns = staticResource.getPathPatterns();
            String[] resourceLocations = staticResource.getResourceLocations();
            if (Objects.isNull(pathPatterns) || pathPatterns.length == 0 || Objects.isNull(resourceLocations)
                || resourceLocations.length == 0) {
                return;
            }
            pathPatterns = Arrays.stream(pathPatterns).filter(StringUtils::hasText).collect(Collectors.toList()).toArray(new String[0]);
            resourceLocations = Arrays.stream(resourceLocations).filter(StringUtils::hasText).map(location -> {
                if(!location.endsWith("/")) {
                    location += "/";
                }
                return location;
            }).collect(Collectors.toList()).toArray(new String[0]);
            if (Objects.isNull(pathPatterns) || pathPatterns.length == 0 || Objects.isNull(resourceLocations)
                || resourceLocations.length == 0) {
                return;
            }
            registry.addResourceHandler(pathPatterns)
                .addResourceLocations(resourceLocations);
        });

    }
}
