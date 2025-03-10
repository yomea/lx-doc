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
    // 首页
    private String indexHtmlWebPath;
    // 请求路径白名单
    private List<String> whiteUrlList;
    // 请求路径黑名单
    private List<String> blackUrlList;
    // 文档内容存储配置
    private DocContentStorageProperties docStorage;
    // 文档内容存储配置
    private FileUploadProperties fileUpload;
    // 静态资源（通常用于前后端不分离）
    private List<StaticResourceProperties> staticResources;
}
