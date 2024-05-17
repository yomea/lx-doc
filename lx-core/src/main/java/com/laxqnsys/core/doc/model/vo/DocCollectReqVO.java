package com.laxqnsys.core.doc.model.vo;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/17 16:27
 */
@Data
public class DocCollectReqVO {

    @ApiModelProperty(value = "ID")
    @NotNull(message = "文件id不能为空！")
    private Long id;
}
