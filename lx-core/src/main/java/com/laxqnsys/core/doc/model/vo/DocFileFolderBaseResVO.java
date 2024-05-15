package com.laxqnsys.core.doc.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/15 14:10
 */
@Data
public class DocFileFolderBaseResVO {

    @ApiModelProperty(value = "文件夹id")
    private Long id;

    @ApiModelProperty(value = "文件夹名称")
    private String name;
}
