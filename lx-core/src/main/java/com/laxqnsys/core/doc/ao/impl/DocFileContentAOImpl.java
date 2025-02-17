package com.laxqnsys.core.doc.ao.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.doc.ao.AbstractDocFileFolderAO;
import com.laxqnsys.core.doc.ao.DocFileContentAO;
import com.laxqnsys.core.doc.dao.entity.DocFileContent;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.dao.entity.DocRecycle;
import com.laxqnsys.core.doc.model.dto.DocFileCopyDTO;
import com.laxqnsys.core.doc.model.vo.DocFileContentResVO;
import com.laxqnsys.core.doc.model.vo.DocFileCopyReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileCreateReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileDelReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileMoveReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileUpdateReqVO;
import com.laxqnsys.core.enums.DelStatusEnum;
import com.laxqnsys.core.enums.FileFolderFormatEnum;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author wuzhenhong
 * @date 2024/5/15 16:29
 */
@Service
public class DocFileContentAOImpl extends AbstractDocFileFolderAO implements DocFileContentAO {

    @Override
    public DocFileContentResVO getFileContent(Long id) {

        DocFileContent docFileContent = this.getByFileId(id);
        DocFileFolder docFileFolder = docFileFolderService.getById(id);

        DocFileContentResVO resVO = new DocFileContentResVO();
        resVO.setId(id);
        resVO.setName(Objects.nonNull(docFileFolder) ? docFileFolder.getName() : "");
        resVO.setContent(docFileContent.getContent());
        resVO.setUpdateAt(docFileContent.getUpdateAt());
        resVO.setCreateAt(docFileContent.getCreateAt());
        return resVO;
    }

    @Override
    public DocFileContentResVO createFile(DocFileCreateReqVO createReqVO) {

        DocFileFolder parent = super.getById(createReqVO.getFolderId());
        if(FileFolderFormatEnum.FILE.getFormat().equals(parent.getFormat())) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "只需要在文件夹下创建文件");
        }
        DocFileFolder fileFolder = new DocFileFolder();
        fileFolder.setParentId(createReqVO.getFolderId());
        fileFolder.setName(createReqVO.getName());
        fileFolder.setFileCount(0);
        fileFolder.setFolderCount(0);
        fileFolder.setFormat(FileFolderFormatEnum.FILE.getFormat());
        fileFolder.setFileType(createReqVO.getType());
        fileFolder.setCollected(false);
        fileFolder.setImg(null);
        fileFolder.setVersion(0);
        fileFolder.setCreatorId(LoginContext.getUserId());
        fileFolder.setCreateAt(LocalDateTime.now());
        fileFolder.setUpdateAt(LocalDateTime.now());
        fileFolder.setStatus(DelStatusEnum.NORMAL.getStatus());

        DocFileContent saveFileContent = new DocFileContent();
        saveFileContent.setVersion(0);
        saveFileContent.setCreatorId(fileFolder.getCreatorId());
        saveFileContent.setCreateAt(fileFolder.getCreateAt());
        saveFileContent.setUpdateAt(fileFolder.getUpdateAt());

        transactionTemplate.execute(status -> {
            docFileFolderService.save(fileFolder);
            docFileFolderService.updateFileCount(createReqVO.getFolderId(), 1);
            saveFileContent.setId(fileFolder.getId());
            docFileContentService.save(saveFileContent);
            return null;
        });

        DocFileContentResVO resVO = new DocFileContentResVO();
        resVO.setId(fileFolder.getId());
        resVO.setName(fileFolder.getName());
        resVO.setFolderId(fileFolder.getParentId());
        resVO.setType(fileFolder.getFileType());
        return resVO;
    }

    @Override
    public void updateFile(DocFileUpdateReqVO updateReqVO) {
        Long fileId = updateReqVO.getId();
        // 校验
        super.getById(fileId);
        DocFileContent docFileContent = this.getByFileId(fileId);

        transactionTemplate.execute(status -> {
            DocFileFolder updateFolder = new DocFileFolder();
            updateFolder.setId(fileId);
            updateFolder.setName(updateReqVO.getName());
            updateFolder.setImg(updateReqVO.getImg());
            updateFolder.setUpdateAt(LocalDateTime.now());
            docFileFolderService.updateById(updateFolder);

            DocFileContent update = new DocFileContent();
            update.setId(docFileContent.getId());
            update.setContent(updateReqVO.getContent());
            update.setUpdateAt(updateFolder.getUpdateAt());
            docFileContentService.updateById(update);
            return null;
        });
    }

    @Override
    public void moveFile(DocFileMoveReqVO reqVO) {

        Long newFolderId = reqVO.getNewFolderId();
        DocFileFolder parent = super.getById(newFolderId);
        if(FileFolderFormatEnum.FILE.getFormat().equals(parent.getFormat())) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "只能迁移到文件夹下！");
        }
        List<Long> idList = reqVO.getIds();
        List<DocFileFolder> fileFolders = super.selectByIdList(idList);
        Map<Long, Integer> parentIdMapSizeMap = fileFolders.stream()
            .filter(e -> Objects.nonNull(e.getParentId()) && e.getParentId() > 0L)
            .collect(Collectors.groupingBy(DocFileFolder::getParentId,
                Collectors.summingInt(x -> 1)));
        List<DocFileFolder> updateOldFolderList = parentIdMapSizeMap.entrySet().stream().map(entry -> {
            DocFileFolder update = new DocFileFolder();
            update.setId(entry.getKey());
            update.setFileCount(-entry.getValue());
            return update;
        }).collect(Collectors.toList());
        List<DocFileFolder> updateList = idList.stream().map(id -> {
            DocFileFolder update = new DocFileFolder();
            update.setId(id);
            update.setParentId(reqVO.getNewFolderId());
            return update;
        }).collect(Collectors.toList());
        transactionTemplate.execute(status -> {
            if(!CollectionUtils.isEmpty(updateOldFolderList)) {
                docFileFolderService.batchDeltaUpdate(updateOldFolderList);
            }
            docFileFolderService.updateFileCount(reqVO.getNewFolderId(), updateList.size());
            docFileFolderService.updateBatchById(updateList);
            return null;
        });
    }

    @Override
    public void copyFile(DocFileCopyReqVO reqVO) {

        Long newFolderId = reqVO.getNewFolderId();
        DocFileFolder parent = super.getById(newFolderId);
        if(FileFolderFormatEnum.FILE.getFormat().equals(parent.getFormat())) {
          throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "只能复制到文件夹下！");
        }
        List<Long> originIdList = reqVO.getIds();
        List<DocFileFolder> fileFolders = super.selectByIdList(originIdList).stream()
            .filter(e -> !e.getParentId().equals(reqVO.getNewFolderId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fileFolders)) {
            return;
        }
        LocalDateTime currentLdt = LocalDateTime.now();
        fileFolders.stream().forEach(e -> {
            e.setOldId(e.getId());
            e.setId(null);
            e.setCollected(false);
            e.setCreateAt(currentLdt);
            e.setUpdateAt(currentLdt);
            e.setParentId(reqVO.getNewFolderId());
        });
        Long userId = LoginContext.getUserId();
        transactionTemplate.execute(status -> {
            docFileFolderService.saveBatch(fileFolders);
            docFileFolderService.updateFileCount(reqVO.getNewFolderId(), fileFolders.size());
            List<DocFileCopyDTO> updateList = fileFolders.stream().map(file -> {
                DocFileCopyDTO update = new DocFileCopyDTO();
                update.setOldFileId(file.getOldId());
                update.setNewFileId(file.getId());
                return update;
            }).collect(Collectors.toList());
            docFileContentService.copyByFileIdList(updateList, userId);
            return null;
        });
    }

    @Override
    public void deleteFile(DocFileDelReqVO reqVO) {

        List<Long> idList = reqVO.getIds();
        List<DocFileFolder> fileFolders = super.selectByIdList(idList);
        Map<Long, Integer> parentIdMapSizeMap = fileFolders.stream()
            .filter(e -> Objects.nonNull(e.getParentId()) && e.getParentId() > 0L)
            .collect(Collectors.groupingBy(DocFileFolder::getParentId,
                Collectors.summingInt(x -> 1)));
        List<DocFileFolder> updateOldFolderList = parentIdMapSizeMap.entrySet().stream().map(entry -> {
            DocFileFolder update = new DocFileFolder();
            update.setId(entry.getKey());
            update.setFileCount(-entry.getValue());
            return update;
        }).collect(Collectors.toList());
        LocalDateTime recycleTime = LocalDateTime.now();
        Long userId = LoginContext.getUserId();
        List<DocRecycle> docRecycleList = fileFolders.stream().map(e -> {
            DocRecycle docRecycle = new DocRecycle();
            docRecycle.setId(e.getId());
            docRecycle.setIdList(Collections.singletonList(e.getId()));
            docRecycle.setName(e.getName());
            docRecycle.setUserId(userId);
            docRecycle.setCreateAt(recycleTime);
            return docRecycle;
        }).collect(Collectors.toList());
        transactionTemplate.execute(status -> {
            docFileFolderService.update(Wrappers.<DocFileFolder>lambdaUpdate()
                .in(DocFileFolder::getId, idList)
                .set(DocFileFolder::getStatus, DelStatusEnum.DEL.getStatus()));
//            docFileContentService.update(Wrappers.<DocFileContent>lambdaUpdate()
//                .in(DocFileContent::getFileId, idList)
//                .set(DocFileContent::getStatus, DelStatusEnum.DEL.getStatus()));
            // 扔回收站
            docRecycleService.saveBatch(docRecycleList);
            super.saveRecycleLevel(docRecycleList);
            if(!CollectionUtils.isEmpty(updateOldFolderList)) {
                docFileFolderService.batchDeltaUpdate(updateOldFolderList);
            }
            return null;
        });
    }

    private DocFileContent getByFileId(Long fileId) {
        DocFileContent docFileContent = docFileContentService.getOne(Wrappers.<DocFileContent>lambdaQuery()
            .eq(DocFileContent::getId, fileId)
            .last("limit 1"));
        if (Objects.isNull(docFileContent)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), String.format("id为%s的文件未找到", fileId));
        }
        if (!docFileContent.getCreatorId().equals(LoginContext.getUserId())) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "非法访问！");
        }
        return docFileContent;
    }
}
