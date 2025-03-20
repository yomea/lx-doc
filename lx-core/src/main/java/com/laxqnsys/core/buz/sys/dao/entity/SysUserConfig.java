package com.laxqnsys.core.buz.sys.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 系统-用户配置
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SysUserConfig对象", description = "系统-用户配置")
public class SysUserConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "配置类型")
    private String configType;

    @ApiModelProperty(value = "配置规则JSON")
    private String configContent;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createAt;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateAt;

    @ApiModelProperty(value = "0：正常，-1：删除")
    private Integer status;


}
