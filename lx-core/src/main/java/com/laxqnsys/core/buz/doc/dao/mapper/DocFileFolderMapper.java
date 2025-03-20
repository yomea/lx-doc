package com.laxqnsys.core.buz.doc.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laxqnsys.core.buz.doc.dao.entity.DocFileFolder;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 文档-文件夹 Mapper 接口
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
public interface DocFileFolderMapper extends BaseMapper<DocFileFolder> {

    int updateFileCount(@Param("folderIdList") List<Long> folderIdList, @Param("delta") int i);

    int updateFolderCount(@Param("folderIdList") List<Long> folderIdList, @Param("delta") int i);

    int batchDeltaUpdate(List<DocFileFolder> updateList);
}
