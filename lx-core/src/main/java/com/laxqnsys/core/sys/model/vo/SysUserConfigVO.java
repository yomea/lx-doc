package com.laxqnsys.core.sys.model.vo;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/14 16:35
 */
@Data
public class SysUserConfigVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "业务类型, 目前只有lx-doc")
    private String bussinessType;

    @ApiModelProperty(value = "子类型，布局类型")
    private String layoutType;

    @ApiModelProperty(value = "配置规则JSON")
    @NotBlank(message = "配置规则JSON不能为空！")
    private String rule;

    @ApiModelProperty(value = "用户ID")
    private Long userId;
}
