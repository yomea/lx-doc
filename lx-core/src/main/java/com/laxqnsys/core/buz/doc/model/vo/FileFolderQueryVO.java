package com.laxqnsys.core.buz.doc.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/14 19:19
 */
@Data
@ApiModel(value = "文件夹查询VO")
public class FileFolderQueryVO {

    @ApiModelProperty(value = "文件夹id")
    private Long folderId;

    @ApiModelProperty(value = "文件名-模糊搜索")
    private String name;

    /**
     * @see
     */
    @ApiModelProperty(value = "文件类型")
    private String fileType;

    @ApiModelProperty(value = "排序字段：createAt（创建时间）、updateAt（更新时间）、name（名称）")
    private String sortField;

    @ApiModelProperty(value = "排序类型：asc（正序）、desc（倒序）")
    private String sortType;
}
