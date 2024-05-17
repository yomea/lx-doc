package com.laxqnsys.core.sys.dao.entity;

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
 * 系统-附件
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SysAttachment对象", description = "系统-附件")
public class SysAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "附件名称")
    private String name;

    @ApiModelProperty(value = "附件存储路径")
    private String path;

    @ApiModelProperty(value = "附件备注")
    private String remark;

    @ApiModelProperty(value = "附件文件大小 M")
    private Long size;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "上传人ID")
    private Long creatorId;

    @ApiModelProperty(value = "上传时间")
    private LocalDateTime createAt;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateAt;

    @ApiModelProperty(value = "0：正常，-1：删除")
    private Integer status;


}
