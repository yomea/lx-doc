package com.laxqnsys.core.properties;

import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2025/3/6 20:38
 */
@Data
public class MinioFileUploadProperties {

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucket;
}
