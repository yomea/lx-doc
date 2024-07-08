package com.laxqnsys.core.doc.model.vo;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author wuzhenhong
 * @date 2024/5/15 16:59
 */
@Data
public class DocFileUpdateReqVO {

    @ApiModelProperty(value = "文件ID")
    @NotNull(message = "id必填")
    private Long id;

    @ApiModelProperty(value = "文件名")
    @Length(min = 1, max = 100, message = "文件名的字符长度请控制在1-100之间")
    private String name;

    @ApiModelProperty(value = "文件内容")
    private String content;

    @ApiModelProperty(value = "封面")
    private String img;
}
