package com.laxqnsys.core.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger配置类
 */
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
@ConditionalOnProperty(prefix = "swagger", name = "enable", havingValue = "true", matchIfMissing = true)
// 扫描接口的路径
public class SwaggerConfig {

    private static final String WEBAP = "/doc";

    @Bean
    public Docket createApi() {
        return getRestBody("理想文档API", "com.lxqnsys.doc.controller");
    }

    private Docket getRestBody(String title, String path) {
        // 增加请求头信息
        String osName = System.getProperty("os.name").toLowerCase();

        List<Parameter> pars = new ArrayList<>();
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo()).groupName(title)
            .globalOperationParameters(pars)
            .select()
            // 带注解ApiOperation的controller都会扫描出来
            .apis(RequestHandlerSelectors
                .basePackage(path))
            .paths(PathSelectors.any()).build()
            .pathMapping(osName.startsWith("win") ? "" : WEBAP);
    }

    // swagger文档路径：ip+端口+"/swagger-ui.html"
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("理想文档swagger构建api文档")
            .description("理想文档")
            .termsOfServiceUrl("/swagger-ui.html").version("1.0.0")// 版本号
            .build();
    }
}
