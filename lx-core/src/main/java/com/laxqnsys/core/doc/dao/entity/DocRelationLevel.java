package com.laxqnsys.core.doc.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文档-关联层级
 * </p>
 *
 * @author author
 * @since 2025-02-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="DocRelationLevel对象", description="文档-关联层级")
public class DocRelationLevel implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "父id")
    private Long parentId;

    @ApiModelProperty(value = "子id")
    private Long sonId;

    @ApiModelProperty(value = "回收人ID")
    private Long userId;

}
