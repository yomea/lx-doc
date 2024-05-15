package com.laxqnsys.core.doc.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/14 19:23
 */
@Data
public class DocFileResVO extends DocFileFolderBaseResVO {

    @ApiModelProperty(value = "文件类型")
    private String type;

    @ApiModelProperty(value = "图片地址")
    private String img;

    @ApiModelProperty(value = "是否被收藏")
    private boolean collected;

    @ApiModelProperty(value = "创建时间")
    private String createAt;

    @ApiModelProperty(value = "更新时间")
    private String updateAt;
}
