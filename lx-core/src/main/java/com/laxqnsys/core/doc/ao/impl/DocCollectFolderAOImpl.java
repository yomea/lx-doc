package com.laxqnsys.core.doc.ao.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.doc.ao.DocCollectFolderAO;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.model.vo.DocCollectReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileResVO;
import com.laxqnsys.core.doc.service.IDocFileFolderService;
import com.laxqnsys.core.enums.DelStatusEnum;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wuzhenhong
 * @date 2024/5/17 15:53
 */
@Service
public class DocCollectFolderAOImpl implements DocCollectFolderAO {

    @Autowired
    private IDocFileFolderService docFileFolderService;

    @Override
    public List<DocFileResVO> getCollectFileList(String name) {
        Long userId = LoginContext.getUserId();
        return docFileFolderService.list(Wrappers.<DocFileFolder>lambdaQuery()
            .eq(DocFileFolder::getCreatorId, userId)
            .like(DocFileFolder::getName, name)
            .eq(DocFileFolder::getCollected, true)
            .eq(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus()))
            .stream().map(fileFolder -> {
                DocFileResVO resVO = new DocFileResVO();
                resVO.setId(fileFolder.getId());
                resVO.setName(fileFolder.getName());
                resVO.setType(fileFolder.getFileType());
                resVO.setImg(fileFolder.getImg());
                resVO.setCollected(fileFolder.getCollected());
                resVO.setCreateAt(fileFolder.getCreateAt());
                resVO.setUpdateAt(fileFolder.getUpdateAt());
                return resVO;
            }).collect(Collectors.toList());
    }

    @Override
    public void cancelCollect(DocCollectReqVO reqVO) {

        DocFileFolder update = new DocFileFolder();
        update.setId(reqVO.getId());
        update.setCollected(false);
        docFileFolderService.updateById(update);
    }

    @Override
    public void collect(DocCollectReqVO reqVO) {
        DocFileFolder update = new DocFileFolder();
        update.setId(reqVO.getId());
        update.setCollected(true);
        docFileFolderService.updateById(update);
    }
}
