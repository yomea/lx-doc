package com.laxqnsys.core.doc.model.vo;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/15 16:59
 */
@Data
public class DocFileMoveReqVO {

    @ApiModelProperty(value = "文件ID集合")
    @NotEmpty(message = "ids必填")
    private List<Long> ids;

    @ApiModelProperty(value = "需要移动到的文件夹目录")
    private Long newFolderId;
}
