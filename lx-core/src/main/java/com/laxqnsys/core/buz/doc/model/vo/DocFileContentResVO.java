package com.laxqnsys.core.buz.doc.model.vo;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/15 16:32
 */
@Data
public class DocFileContentResVO {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "所属文件夹ID")
    private Long folderId;

    @ApiModelProperty(value = "文件名")
    private String name;

    @ApiModelProperty(value = "文件类型")
    private String type;

    @ApiModelProperty(value = "文件数据，excel，思维导图等文件内容")
    private String content;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createAt;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateAt;
}
