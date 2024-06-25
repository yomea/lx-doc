package com.laxqnsys.core.doc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import java.util.List;

/**
 * <p>
 * 文档-文件夹 服务类
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
public interface IDocFileFolderService extends IService<DocFileFolder> {

    int updateFileCount(List<Long> folderIdList, int i);

    int updateFileCount(Long folderId, int i);

    int updateFolderCount(Long folderId, int i);

    int updateFolderCount(List<Long> folderIdList, int i);

    int batchDeltaUpdate(List<DocFileFolder> updateList);

    int updateDelCount(Long folderId, int i);

    int updateDelCount(List<Long> folderIdList, int i);
}
