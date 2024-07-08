package com.laxqnsys.core.sys.controller;

import com.laxqnsys.core.properties.LxDocWebProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author wuzhenhong
 * @date 2024/5/24 16:06
 */
@Controller
public class IndexController {

    @Autowired
    private LxDocWebProperties lxDocWebProperties;

    @GetMapping(value = "/")
    public String index() {
        String indexHtmlWebPath = lxDocWebProperties.getIndexHtmlWebPath();
        return "forward:" + (StringUtils.hasText(indexHtmlWebPath)
            ? indexHtmlWebPath
            : "/system/error");
    }
}
