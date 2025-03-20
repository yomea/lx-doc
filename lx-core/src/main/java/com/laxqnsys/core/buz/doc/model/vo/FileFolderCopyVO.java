package com.laxqnsys.core.buz.doc.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/14 19:19
 */
@Data
@ApiModel(value = "文件夹复制VO")
public class FileFolderCopyVO {

    @ApiModelProperty(value = "文件夹id")
    @NotNull(message = "id必传")
    private Long id;

    @ApiModelProperty(value = "文件夹id")
    @NotNull(message = "folderId必传")
    private Long folderId;

}
