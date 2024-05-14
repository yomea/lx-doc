package com.laxqnsys.core.sys.model.vo;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author wuzhenhong
 * @date 2024/5/14 14:18
 */
@Data
public class UserInfoVO {

    @ApiModelProperty(value = "用户id")
    private Long id;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "用户昵称")
    @Length(max = 64, message = "用户昵称最多64个字符")
    private String userName;

    @ApiModelProperty(value = "头像地址")
    private String avatar;

    @ApiModelProperty(value = "注册时间", example = "2024-01-01 10:10:10")
    private String createAt;
}
