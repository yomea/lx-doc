package com.laxqnsys.core.sys.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author wuzhenhong
 * @date 2024/5/14 14:18
 */
@Data
public class UserInfoUpdateVO {

    @ApiModelProperty(value = "用户昵称")
    @Length(max = 64, message = "用户昵称最多64个字符")
    private String userName;

    @ApiModelProperty(value = "头像地址")
    private String avatar;
}
