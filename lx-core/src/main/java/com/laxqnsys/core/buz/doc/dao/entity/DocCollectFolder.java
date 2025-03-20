package com.laxqnsys.core.buz.doc.dao.entity;

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
 * 文档-文件收藏夹
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "DocCollectFolder对象", description = "文档-文件收藏夹")
public class DocCollectFolder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "文件名")
    private String name;

    @ApiModelProperty(value = "收藏人ID")
    private Long userId;

    @ApiModelProperty(value = "收藏时间")
    private LocalDateTime createAt;
}
