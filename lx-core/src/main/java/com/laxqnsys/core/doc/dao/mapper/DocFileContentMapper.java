package com.laxqnsys.core.doc.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laxqnsys.core.doc.dao.entity.DocFileContent;
import com.laxqnsys.core.doc.model.dto.DocFileCopyDTO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 文档-文件内容 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
public interface DocFileContentMapper extends BaseMapper<DocFileContent> {

    int copyByFileIdList(@Param("list") List<DocFileCopyDTO> copyList, @Param("userId") Long userId);
}
