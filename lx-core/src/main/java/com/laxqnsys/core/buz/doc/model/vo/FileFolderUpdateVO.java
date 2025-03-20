package com.laxqnsys.core.buz.doc.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author wuzhenhong
 * @date 2024/5/14 19:19
 */
@Data
@ApiModel(value = "文件夹更新VO")
public class FileFolderUpdateVO {

    @ApiModelProperty(value = "文件夹id")
    @NotNull(message = "文件夹id不能为空！")
    private Long id;

    @ApiModelProperty(value = "文件名")
    @NotBlank(message = "文件夹名称不能为空！")
    @Length(min = 1, max = 100, message = "文件夹名的字符长度请控制在1-100之间")
    private String name;

}
