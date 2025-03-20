package com.laxqnsys.core.other.properties;

import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/24 15:32
 */
@Data
public class FileUploadProperties {

    // 存储类型
    private String type;
    // 存储路径
    private String path;
    // 使用oss存储时的额外属性
    private OssFileUploadProperties oss;
    // 使用minio存储时的额外属性
    private MinioFileUploadProperties minio;

}
