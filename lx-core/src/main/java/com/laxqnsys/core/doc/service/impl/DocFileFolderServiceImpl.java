package com.laxqnsys.core.doc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.dao.mapper.DocFileFolderMapper;
import com.laxqnsys.core.doc.service.IDocFileFolderService;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * <p>
 * 文档-文件夹 服务实现类
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
@Service
public class DocFileFolderServiceImpl extends ServiceImpl<DocFileFolderMapper, DocFileFolder> implements
    IDocFileFolderService {

    @Override
    public int updateFileCount(List<Long> folderIdList, int i) {
        return super.baseMapper.updateFileCount(folderIdList, i);
    }

    @Override
    public int updateFileCount(Long folderId, int i) {
        return super.baseMapper.updateFileCount(Collections.singletonList(folderId), i);
    }

    @Override
    public int updateFolderCount(Long folderId, int i) {
        return super.baseMapper.updateFolderCount(Collections.singletonList(folderId), i);
    }

    @Override
    public int updateFolderCount(List<Long> folderIdList, int i) {
        return super.baseMapper.updateFolderCount(folderIdList, i);
    }

    @Override
    public int batchDeltaUpdate(List<DocFileFolder> updateList) {
        if (CollectionUtils.isEmpty(updateList)) {
            return 0;
        }
        return super.baseMapper.batchDeltaUpdate(updateList);
    }
}
