package com.laxqnsys.core.properties;

import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2025/3/14 11:34
 */
@Data
public class RestTemplateProperties {

    private Boolean enable;

    private Integer connectNum = 20;

    private Long keepAliveDuration = 300L;

    private Long connectTimeout = 3L;

    private Long readTimeout = 10L;
}
