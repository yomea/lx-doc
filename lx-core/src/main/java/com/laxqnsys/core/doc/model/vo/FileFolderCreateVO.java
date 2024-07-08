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
@ApiModel(value = "文件夹创建VO")
public class FileFolderCreateVO {

    @ApiModelProperty(value = "父文件夹id")
    private Long parentFolderId;

    @ApiModelProperty(value = "文件名")
    @NotBlank(message = "文件夹名称不能为空！")
    @Length(min = 1, max = 100, message = "文件夹名的字符长度请控制在1-100之间")
    private String name;

}
