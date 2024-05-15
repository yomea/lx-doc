package com.laxqnsys.core.sys.model.vo;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/14 16:35
 */
@Data
public class SysUserConfigReqVO {

    @ApiModelProperty(value = "配置类型")
    @NotBlank(message = "配置类型不能为空！")
    private String configType;

    @ApiModelProperty(value = "配置规则JSON")
    @NotBlank(message = "配置规则JSON不能为空！")
    private String configContent;
}
