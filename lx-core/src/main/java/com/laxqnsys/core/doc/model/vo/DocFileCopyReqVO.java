package com.laxqnsys.core.doc.model.vo;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/15 16:59
 */
@Data
public class DocFileCopyReqVO {

    @ApiModelProperty(value = "文件ID集合")
    @NotEmpty(message = "ids必填")
    private List<Long> ids;

    @ApiModelProperty(value = "需要复制到的文件夹目录")
    @NotNull
    private Long newFolderId;
}
