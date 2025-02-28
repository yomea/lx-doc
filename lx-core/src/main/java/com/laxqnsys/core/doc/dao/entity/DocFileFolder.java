package com.laxqnsys.core.doc.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 文档-文件夹
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "DocFileFolder对象", description = "文档-文件夹")
public class DocFileFolder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "父文件夹ID，顶级节点为0")
    private Long parentId;

    @ApiModelProperty(value = "文件名")
    private String name;

    @ApiModelProperty(value = "文件计数，本菜单下的直接文件数量")
    private Integer fileCount;

    @ApiModelProperty(value = "菜单计数，本菜单下的直接菜单数量")
    private Integer folderCount;

    /**
     * @see com.laxqnsys.core.enums.FileFolderFormatEnum
     */
    @ApiModelProperty(value = "文件格式，1：文件夹，2：文件")
    private Integer format;

    @ApiModelProperty(value = "文件类型，1：excel，2：word，3：pdf，4：思维导图，5：白板")
    private String fileType;

    @ApiModelProperty(value = "是否被收藏，0：否，1：是")
    private Boolean collected;

    @ApiModelProperty(value = "封面图")
    private String img;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "创建人ID")
    private Long creatorId;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createAt;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateAt;

    @ApiModelProperty(value = "0：正常，-1：删除")
    private Integer status;

    @TableField(exist = false)
    private Long oldId;

    @ApiModelProperty(value = "文件数据，excel，思维导图等文件内容")
    @TableField(exist = false)
    private String content;
}
