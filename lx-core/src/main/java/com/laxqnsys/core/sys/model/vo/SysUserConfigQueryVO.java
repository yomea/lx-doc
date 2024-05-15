package com.laxqnsys.core.sys.model.vo;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/15 13:50
 */
@Data
public class SysUserConfigQueryVO {

    @ApiModelProperty(value = "业务类型，比如工作台、思维导图、Markdown等")
    @NotBlank(message = "业务类型必传！")
    private String configType;


}
