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

    // 文件上传路径
    private String fileUploadPath;

    private String indexHtmlWebPath;
    // 请求路径白名单
    private List<String> whiteUrlList;

    private List<String> blackUrlList;

    private List<StaticResourceProperies> staticResources;
}
