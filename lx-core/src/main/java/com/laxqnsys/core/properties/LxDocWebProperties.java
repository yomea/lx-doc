package com.laxqnsys.core.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wuzhenhong
 * @date 2024/5/24 15:32
 */
@ConfigurationProperties(prefix = "lx.doc")
@Data
public class LxDocWebProperties {

    // 文件上传类型
    private String fileUploadType;

    private String indexHtmlWebPath;
    // 请求路径白名单
    private List<String> whiteUrlList;

    // 存储配置
    private DocContentStorageProperties storage;

    private List<StaticResourceProperties> staticResources;

    private LocalFileUploadProperties localUpload;
    private OssFileUploadProperties ossUpload;
    private MinioFileUploadProperties minioUpload;
}
