package com.laxqnsys.core.other.config;

import com.laxqnsys.core.other.properties.LxDocWebProperties;
import com.laxqnsys.core.other.properties.RestTemplateProperties;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author wuzhenhong
 * @date 2024/5/17 9:19
 */
@Configuration
public class RestTemplateConfig {

    @Autowired
    private LxDocWebProperties lxDocWebProperties;

    @Bean
    @ConditionalOnProperty(prefix = "lx.doc.restTemplate", name = "enable", havingValue = "true", matchIfMissing = true)
    public RestTemplate restTemplate() {
        // 配置 okHttp 连接池，主要用于前后端不分离的场景
        RestTemplateProperties restTemplate = lxDocWebProperties.getRestTemplate();
        if(Objects.isNull(restTemplate)) {
            restTemplate = new RestTemplateProperties();
        }
        ConnectionPool pool = new ConnectionPool(restTemplate.getConnectNum(), restTemplate.getKeepAliveDuration(), TimeUnit.SECONDS);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectionPool(pool)
            .connectTimeout(restTemplate.getConnectTimeout(), TimeUnit.SECONDS)  // 连接超时时间
            .readTimeout(restTemplate.getReadTimeout(), TimeUnit.SECONDS)    // 读取超时时间
            .build();
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory(okHttpClient));
    }
}
