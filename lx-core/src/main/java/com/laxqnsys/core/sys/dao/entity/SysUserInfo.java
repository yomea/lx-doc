package com.laxqnsys.core.sys.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 系统-用户信息
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SysUserInfo对象", description="系统-用户信息")
public class SysUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "昵称，最多64个字符")
    private String userName;

    @ApiModelProperty(value = "账户名，2-20个字符")
    private String account;

    @ApiModelProperty(value = "密码，通过AES对称加密")
    private String password;

    @ApiModelProperty(value = "头像地址")
    private String avatar;

    @ApiModelProperty(value = "注册时间")
    private LocalDateTime createAt;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateAt;

    @ApiModelProperty(value = "0：正常，-1：删除，1：禁用")
    private Integer status;


}
