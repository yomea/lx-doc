package com.laxqnsys.core.buz.sys.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/14 18:56
 */
@Data
@ApiModel(value = "附件")
public class SysAttachmentVO {

    @ApiModelProperty(value = "base64格式的图片")
    @NotBlank(message = "图片不能为空！")
    private String imgData;
}
