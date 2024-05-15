package com.laxqnsys.core.doc.ao.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.doc.ao.DocFileContentAO;
import com.laxqnsys.core.doc.dao.entity.DocFileContent;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.model.vo.DocFileContentResVO;
import com.laxqnsys.core.doc.model.vo.DocFileCreateReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileMoveReqVO;
import com.laxqnsys.core.doc.model.vo.DocFileUpdateReqVO;
import com.laxqnsys.core.doc.service.IDocFileContentService;
import com.laxqnsys.core.doc.service.IDocFileFolderService;
import com.laxqnsys.core.enums.DelStatusEnum;
import com.laxqnsys.core.enums.FileFolderFormatEnum;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

/**
 * @author wuzhenhong
 * @date 2024/5/15 16:29
 */
@Service
public class DocFileContentAOImpl implements DocFileContentAO {

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private IDocFileFolderService docFileFolderService;

    @Autowired
    private IDocFileContentService docFileContentService;

    @Override
    public DocFileContentResVO getFileContent(Long id) {

        DocFileContent docFileContent = this.getByFileId(id);

        DocFileContentResVO resVO = new DocFileContentResVO();
        resVO.setId(id);
        resVO.setContent(docFileContent.getContent());
        resVO.setUpdateAt(docFileContent.getUpdateAt());
        resVO.setCreateAt(docFileContent.getCreateAt());
        return resVO;
    }

    @Override
    public DocFileContentResVO createFile(DocFileCreateReqVO createReqVO) {

        DocFileFolder parentFolder = docFileFolderService.getById(createReqVO.getFolderId());
        if (Objects.isNull(parentFolder) || DelStatusEnum.NORMAL.getStatus().equals(parentFolder.getStatus())) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "父文件夹不存在或已被删除！");
        }

        String path = parentFolder.getPath();
        DocFileFolder fileFolder = new DocFileFolder();
        fileFolder.setParentId(createReqVO.getFolderId());
        fileFolder.setPath(StringUtils.hasText(path) ? path + "," + createReqVO.getFolderId()
            : String.valueOf(createReqVO.getFolderId()));
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
        saveFileContent.setStatus(fileFolder.getStatus());

        transactionTemplate.execute(status -> {
            docFileFolderService.save(fileFolder);
            docFileFolderService.updateFileCount(Collections.singletonList(createReqVO.getFolderId()), 1);
            saveFileContent.setFileId(fileFolder.getId());
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

        DocFileFolder docFileFolder = docFileFolderService.getById(fileId);
        if (Objects.isNull(docFileFolder)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("id为%s的文件不存在或已被删除！", fileId));
        }

        transactionTemplate.execute(status -> {
            DocFileFolder updateFolder = new DocFileFolder();
            updateFolder.setId(fileId);
            updateFolder.setName(updateReqVO.getName());
            updateFolder.setImg(updateReqVO.getImg());
            updateFolder.setUpdateAt(LocalDateTime.now());
            docFileFolderService.updateById(updateFolder);

            DocFileContent docFileContent = this.getByFileId(fileId);
            DocFileContent update = new DocFileContent();
            update.setId(docFileContent.getId());
            update.setContent(updateReqVO.getContent());
            update.setUpdateAt(updateFolder.getUpdateAt());
            docFileContentService.updateById(docFileContent);
            return null;
        });
    }

    @Override
    public void moveFile(DocFileMoveReqVO reqVO) {

        List<Long> idList = reqVO.getIds();
        List<DocFileFolder> updateList = idList.stream().map(id -> {
            DocFileFolder update = new DocFileFolder();
            update.setId(id);
            update.setParentId(reqVO.getNewFolderId());
            return update;
        }).collect(Collectors.toList());

        List<DocFileFolder> fileFolders = docFileFolderService.listByIds(idList);
        Map<Long, Integer> parentIdMapSizeMap = fileFolders.stream()
            .collect(Collectors.groupingBy(DocFileFolder::getParentId,
                Collectors.summingInt(x -> 1)));
        List<DocFileFolder> updateOldFolderList = parentIdMapSizeMap.entrySet().stream().map(entry -> {
            DocFileFolder update = new DocFileFolder();
            update.setId(entry.getKey());
            update.setFileCount(entry.getValue());
            return update;
        }).collect(Collectors.toList());
        transactionTemplate.execute(status -> {
            docFileFolderService.batchDeltaUpdate(updateOldFolderList);
            docFileFolderService.updateFileCount(Collections.singletonList(reqVO.getNewFolderId()), updateList.size());
            docFileFolderService.updateBatchById(updateList);
            return null;
        });
    }

    private DocFileContent getByFileId(Long fileId) {
        DocFileContent docFileContent = docFileContentService.getOne(Wrappers.<DocFileContent>lambdaQuery()
            .eq(DocFileContent::getFileId, fileId).eq(DocFileContent::getStatus, DelStatusEnum.NORMAL.getStatus())
            .last("limit 1"));
        if (Objects.isNull(docFileContent)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), String.format("id为%s的文件未找到", fileId));
        }
        return docFileContent;
    }
}
