package com.laxqnsys.core.doc.model.vo;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/6/19 17:25
 */
@Data
public class DocSynthFileFolderResVO {

    @ApiModelProperty(value = "文件夹id")
    private Long id;

    @ApiModelProperty(value = "文件夹或者文件名名称")
    private String name;

    @ApiModelProperty(value = "是否文件夹")
    private boolean isFolder;

    @ApiModelProperty(value = "文件类型")
    private String type;

    @ApiModelProperty(value = "图片地址")
    private String img;

    @ApiModelProperty(value = "子文件夹或文件")
    private List<DocSynthFileFolderResVO> children;
}
