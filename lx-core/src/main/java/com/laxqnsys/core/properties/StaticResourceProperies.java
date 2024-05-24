package com.laxqnsys.core.properties;

import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/24 15:21
 */
@Data
public class StaticResourceProperies {

    // 静态资源web路径
    private String[] pathPatterns;

    // 静态资源物理路径
    private String[] resourceLocations;

}
