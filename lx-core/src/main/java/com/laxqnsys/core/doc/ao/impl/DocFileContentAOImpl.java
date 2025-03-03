package com.laxqnsys.core.doc.ao.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.aspect.lock.ConcurrentLock;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.doc.ao.AbstractDocFileFolderAO;
import com.laxqnsys.core.doc.ao.DocFileContentAO;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.dao.entity.DocRecycle;
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
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author wuzhenhong
 * @date 2024/5/15 16:29
 */
@Service
public class DocFileContentAOImpl extends AbstractDocFileFolderAO implements DocFileContentAO {

    @Override
    @Deprecated
    public DocFileContentResVO getFileContent(Long id) {
        DocFileFolder docFileFolder = super.getById(id);
        return docFileContentStorageService.getFileContent(docFileFolder);
    }

    @Override
    public DocFileContentResVO createFile(DocFileCreateReqVO createReqVO) {

        DocFileFolder parent = super.getById(createReqVO.getFolderId());
        if (FileFolderFormatEnum.FILE.getFormat().equals(parent.getFormat())) {
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
        // 先置为无效状态
        fileFolder.setStatus(DelStatusEnum.DISPLAY.getStatus());
        // 先保存元数据（目前我们文件id是通过mysql的自增id生成的，所以选择先保存）
        boolean saveSuccess = docFileFolderService.save(fileFolder);
        if(!saveSuccess) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文档保存失败！原因=》文件元数据保存失败！");
        }

        // 保存文档内容
        boolean success = docFileContentStorageService.create(fileFolder, () ->
            transactionTemplate.execute(status -> {
                // 保存成功之后将文件置为生效状态
                boolean statusUpdate = docFileFolderService.lambdaUpdate()
                    .set(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus())
                    .eq(DocFileFolder::getId, fileFolder.getId())
                    .update();
                if (!statusUpdate) {
                    throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                        "文档保存失败！原因=》将文件置为生效状态时失败！");
                }
                int fileCountUpdate = docFileFolderService.updateFileCount(createReqVO.getFolderId(), 1);
                if (fileCountUpdate <= 0) {
                    throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                        "文档保存失败！原因=》更新父文件夹下文件数量时失败！");
                }
                return statusUpdate && fileCountUpdate > 0;
            })
        );
        if (!success) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "保存文档内容失败！");
        }
        DocFileContentResVO resVO = new DocFileContentResVO();
        resVO.setId(fileFolder.getId());
        resVO.setName(fileFolder.getName());
        resVO.setFolderId(fileFolder.getParentId());
        resVO.setType(fileFolder.getFileType());
        return resVO;
    }

    @Override
    @ConcurrentLock(key = "com.laxqnsys.core.doc.ao.impl.DocFileContentAOImpl.updateFile(${updateReqVO.id})")
    public void updateFile(DocFileUpdateReqVO updateReqVO) {
        Long fileId = updateReqVO.getId();
        // 校验
        DocFileFolder fileFolder = super.getById(fileId);
        DocFileFolder updateFolder = new DocFileFolder();
        updateFolder.setId(fileFolder.getId());
        updateFolder.setName(updateReqVO.getName());
        updateFolder.setImg(updateReqVO.getImg());
        updateFolder.setUpdateAt(LocalDateTime.now());
        updateFolder.setContent(updateReqVO.getContent());
        // 增加文件版本
        updateFolder.setVersion(fileFolder.getVersion() + 1);
        boolean success = docFileContentStorageService.update(updateFolder, () -> {
                boolean updateResult = docFileFolderService.updateById(updateFolder);
                if (!updateResult) {
                    throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                        "文档内容更新失败！原因=》更新文件元数据失败！");
                }
                return true;
            }
        );
        if(!success) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文档内容更新失败！");
        }
    }

    @Override
    public void moveFile(DocFileMoveReqVO reqVO) {

        Long newFolderId = reqVO.getNewFolderId();
        DocFileFolder parent = super.getById(newFolderId);
        if (FileFolderFormatEnum.FILE.getFormat().equals(parent.getFormat())) {
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
            if (!CollectionUtils.isEmpty(updateOldFolderList)) {
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
        if (FileFolderFormatEnum.FILE.getFormat().equals(parent.getFormat())) {
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
            // 新增文件先置为失效，暂时不可见
            e.setStatus(DelStatusEnum.DISPLAY.getStatus());
        });
        // 批量保存复制的文件
        boolean saveSuccess = docFileFolderService.saveBatch(fileFolders);
        if(!saveSuccess) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文档复制失败！");
        }
        boolean success = docFileContentStorageService.copy(fileFolders, () -> {
            return transactionTemplate.execute(status -> {
                // 复制成功之后，将文件置为失效
                boolean updateStatusResult = docFileFolderService.lambdaUpdate()
                    .set(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus())
                    .in(DocFileFolder::getId, fileFolders.stream().map(DocFileFolder::getId)
                        .collect(Collectors.toList()))
                    .update();
                if(!updateStatusResult) {
                    throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文档内容复制失败！原因=》更新文件元数据状态失败！");
                }
                int updateFileCount = docFileFolderService.updateFileCount(reqVO.getNewFolderId(), fileFolders.size());
                if(updateFileCount <= 0) {
                    throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文档内容复制失败！原因=》更新父文件夹文件数量失败！");
                }
                return updateStatusResult && updateFileCount > 0;
            });
        });
        if(!success) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文档复制失败！");
        }
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
            if (!CollectionUtils.isEmpty(updateOldFolderList)) {
                docFileFolderService.batchDeltaUpdate(updateOldFolderList);
            }
            return null;
        });
    }

    @Override
    public void downloadFileContent(Long id, HttpServletResponse response) {
        DocFileFolder docFileFolder = super.getById(id);
        docFileContentStorageService.downloadFileContent(docFileFolder, response);
    }

    @Override
    public DocFileContentResVO getFileBaseInfo(Long id) {
        DocFileFolder docFileFolder = docFileFolderService.getById(id);
        DocFileContentResVO resVO = new DocFileContentResVO();
        resVO.setId(id);
        resVO.setName(Objects.nonNull(docFileFolder) ? docFileFolder.getName() : "");
        resVO.setUpdateAt(docFileFolder.getUpdateAt());
        resVO.setCreateAt(docFileFolder.getCreateAt());
        return resVO;
    }
}
