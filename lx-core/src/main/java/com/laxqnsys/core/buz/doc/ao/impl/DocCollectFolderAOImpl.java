package com.laxqnsys.core.buz.doc.ao.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.buz.doc.ao.DocCollectFolderAO;
import com.laxqnsys.core.other.context.LoginContext;
import com.laxqnsys.core.buz.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.buz.doc.model.vo.DocCollectReqVO;
import com.laxqnsys.core.buz.doc.model.vo.DocFileResVO;
import com.laxqnsys.core.buz.doc.service.IDocFileFolderService;
import com.laxqnsys.core.other.enums.DelStatusEnum;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wuzhenhong
 * @date 2024/5/17 15:53
 */
@Service
public class DocCollectFolderAOImpl implements DocCollectFolderAO {

    private static final String NO_PERMISSION_MSG = "无权操作该文件！";
    private static final String FILE_NOT_EXIST_MSG = "文件不存在！";

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
        this.checkFilePermission(reqVO.getId());
        DocFileFolder update = new DocFileFolder();
        update.setId(reqVO.getId());
        update.setCollected(false);
        docFileFolderService.updateById(update);
    }

    @Override
    public void collect(DocCollectReqVO reqVO) {
        this.checkFilePermission(reqVO.getId());
        DocFileFolder update = new DocFileFolder();
        update.setId(reqVO.getId());
        update.setCollected(true);
        docFileFolderService.updateById(update);
    }

    /**
     * 校验当前用户是否有权限操作该文件
     *
     * @param fileId 文件ID
     */
    private void checkFilePermission(Long fileId) {
        DocFileFolder fileFolder = docFileFolderService.getById(fileId);
        if (Objects.isNull(fileFolder)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), FILE_NOT_EXIST_MSG);
        }
        Long userId = LoginContext.getUserId();
        if (!fileFolder.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), NO_PERMISSION_MSG);
        }
    }
}
