package com.laxqnsys.core.doc.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
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
    private Long id;

    @ApiModelProperty(value = "文件名")
    @NotBlank(message = "文件夹名称不能为空！")
    @Length(max = 64, message = "文件夹名称不能超过64个字符")
    private String name;

}
