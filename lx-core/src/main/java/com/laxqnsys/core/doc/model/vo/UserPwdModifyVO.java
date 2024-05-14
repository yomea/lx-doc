package com.laxqnsys.core.doc.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author wuzhenhong
 * @date 2024/5/14 10:54
 */
@Data
@ApiModel(value = "用户基础信息VO")
public class UserPwdModifyVO {

    @NotBlank(message = "旧密码不能为空！")
    @ApiModelProperty(value = "旧密码")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空！")
    @Length(min = 8, max = 16, message = "新密码长度8-16位")
    @Pattern(regexp = "^(?![\\da-z]+$)(?![\\dA-Z]+$)(?![\\d.!#$%^&*]+$)(?![a-zA-Z]+$)(?![a-z.!#$%^&*]+$)(?![A-Z.!#$%^&*]+$)[\\da-zA-z.!#$%^&*]{8,16}", message = "新密码必须包含数字、大写字母、小写字母、特殊字符（!#$%^&*）其中3种")
    @ApiModelProperty(value = "新密码")
    private String newPassword;
}
