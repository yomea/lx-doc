package com.laxqnsys.core.other.properties;

import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/24 15:21
 */
@Data
public class StaticResourceProperties {

    // 静态资源web路径
    private String[] pathPatterns;

    // 静态资源物理路径
    private String[] resourceLocations;

}
