package com.laxqnsys.core.buz.doc.model.vo;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author wuzhenhong
 * @date 2024/5/15 16:59
 */
@Data
public class DocFileCreateReqVO {

    @ApiModelProperty(value = "文件名")
    @NotBlank(message = "文件名必填！")
    @Length(min = 1, max = 100, message = "文件名的字符长度请控制在1-100之间")
    private String name;

    @ApiModelProperty(value = "文件夹id")
    @NotNull(message = "文件夹id必填！")
    private Long folderId;

    @ApiModelProperty(value = "文件类型")
    @NotBlank(message = "文件类型必填！")
    private String type;
}
