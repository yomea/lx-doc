package com.laxqnsys.core.properties;

import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2025/3/6 20:38
 */
@Data
public class OssFileUploadProperties {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucket;
}
