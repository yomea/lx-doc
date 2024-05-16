package com.laxqnsys.core.doc.model.vo;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
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

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createAt;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateAt;
}
