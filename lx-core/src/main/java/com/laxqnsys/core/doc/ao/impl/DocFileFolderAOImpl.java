package com.laxqnsys.core.doc.ao.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.doc.ao.AbstractDocFileFolderAO;
import com.laxqnsys.core.doc.ao.DocFileFolderAO;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.model.vo.DocFileAndFolderResVO;
import com.laxqnsys.core.doc.model.vo.DocFileFolderResVO;
import com.laxqnsys.core.doc.model.vo.DocFileResVO;
import com.laxqnsys.core.doc.model.vo.FileFolderCopyVO;
import com.laxqnsys.core.doc.model.vo.FileFolderCreateVO;
import com.laxqnsys.core.doc.model.vo.FileFolderDelVO;
import com.laxqnsys.core.doc.model.vo.FileFolderMoveVO;
import com.laxqnsys.core.doc.model.vo.FileFolderQueryVO;
import com.laxqnsys.core.doc.model.vo.FileFolderUpdateVO;
import com.laxqnsys.core.doc.service.IDocFileFolderService;
import com.laxqnsys.core.enums.DelStatusEnum;
import com.laxqnsys.core.enums.FileFolderFormatEnum;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author wuzhenhong
 * @date 2024/5/14 19:36
 */
@Service
public class DocFileFolderAOImpl extends AbstractDocFileFolderAO implements DocFileFolderAO {

    @Autowired
    private IDocFileFolderService docFileFolderService;

    @Override
    public List<DocFileFolderResVO> getFolderTree(Long folderId) {

        if (Objects.isNull(folderId) || folderId <= 0L) {
            folderId = 0L;
        }
        Long userId = LoginContext.getUserId();
        List<DocFileFolder> fileFolders = docFileFolderService.list(Wrappers.<DocFileFolder>lambdaQuery()
            .eq(DocFileFolder::getParentId, folderId)
            .eq(DocFileFolder::getCreatorId, userId)
            .eq(DocFileFolder::getFormat, FileFolderFormatEnum.FOLDER.getFormat())
            .eq(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus()));

        return fileFolders.stream().map(fileFolder -> {
            DocFileFolderResVO resVO = new DocFileFolderResVO();
            resVO.setId(fileFolder.getId());
            resVO.setName(fileFolder.getName());
            resVO.setLeaf(fileFolder.getFolderCount() > 0);
            return resVO;
        }).collect(Collectors.toList());
    }

    @Override
    public DocFileAndFolderResVO getFolderAndFileList(FileFolderQueryVO queryVO) {

        Long folderId = queryVO.getFolderId();
        if (Objects.isNull(folderId) || folderId <= 0L) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "搜索某个文件夹下的文件夹和文件时，folderId必传！");
        }
        Long userId = LoginContext.getUserId();
        List<DocFileFolder> fileFolders = docFileFolderService.list(Wrappers.
            <DocFileFolder>query().orderBy(
                StringUtils.hasText(queryVO.getSortField()) && StringUtils.hasText(queryVO.getSortType())
                , "asc".equalsIgnoreCase(queryVO.getSortType()), queryVO.getSortField())
            .lambda()
            .eq(DocFileFolder::getParentId, folderId)
            .eq(DocFileFolder::getCreatorId, userId)
            .eq(StringUtils.hasText(queryVO.getFileType()), DocFileFolder::getFileType, queryVO.getFileType()
            ).eq(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus()));

        List<DocFileFolderResVO> fileFolderBaseResVOList = fileFolders.stream()
            .filter(folder -> FileFolderFormatEnum.FOLDER.getFormat().equals(folder.getFormat()))
            .map(fileFolder -> {
                DocFileFolderResVO resVO = new DocFileFolderResVO();
                resVO.setId(fileFolder.getId());
                resVO.setName(fileFolder.getName());
                return resVO;
            }).collect(Collectors.toList());

        List<DocFileResVO> fileList = this.filterFileList(fileFolders);

        DocFileAndFolderResVO docFileAndFolderResVO = new DocFileAndFolderResVO();
        docFileAndFolderResVO.setFolderList(fileFolderBaseResVOList);
        docFileAndFolderResVO.setFileList(fileList);

        return docFileAndFolderResVO;
    }

    @Override
    public DocFileAndFolderResVO searchFolderAndFile(FileFolderQueryVO queryVO) {

        String name = queryVO.getName();
        if (!StringUtils.hasText(name)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "搜索关键词必填！");
        }
        Long userId = LoginContext.getUserId();
        List<DocFileFolder> fileFolders = docFileFolderService.list(Wrappers.<DocFileFolder>lambdaQuery()
            .like(DocFileFolder::getName, name)
            .eq(DocFileFolder::getCreatorId, userId)
            .eq(StringUtils.hasText(queryVO.getFileType()), DocFileFolder::getFileType, queryVO.getFileType()
            ).eq(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus()));

        List<DocFileFolderResVO> fileFolderBaseResVOList = fileFolders.stream()
            .filter(folder -> FileFolderFormatEnum.FOLDER.getFormat().equals(folder.getFormat()))
            .map(fileFolder -> {
                DocFileFolderResVO resVO = new DocFileFolderResVO();
                resVO.setId(fileFolder.getId());
                resVO.setName(fileFolder.getName());
                return resVO;
            }).collect(Collectors.toList());

        List<DocFileResVO> fileList = this.filterFileList(fileFolders);

        DocFileAndFolderResVO docFileAndFolderResVO = new DocFileAndFolderResVO();
        docFileAndFolderResVO.setFolderList(fileFolderBaseResVOList);
        docFileAndFolderResVO.setFileList(fileList);

        return docFileAndFolderResVO;
    }

    @Override
    public DocFileFolderResVO crateFolder(FileFolderCreateVO createVO) {

        Long parentFolderId = createVO.getParentFolderId();
        if (Objects.isNull(parentFolderId) || parentFolderId <= 0L) {
            // 根文件夹
            parentFolderId = 0L;
        }
        DocFileFolder docFileFolder = new DocFileFolder();
        docFileFolder.setParentId(parentFolderId);
        docFileFolder.setName(createVO.getName());
        docFileFolder.setFileCount(0);
        docFileFolder.setFolderCount(0);
        docFileFolder.setFormat(FileFolderFormatEnum.FOLDER.getFormat());
        docFileFolder.setFileType("");
        docFileFolder.setCollected(false);
        docFileFolder.setVersion(0);
        docFileFolder.setCreatorId(LoginContext.getUserId());
        docFileFolder.setCreateAt(LocalDateTime.now());
        docFileFolder.setUpdateAt(LocalDateTime.now());
        docFileFolder.setStatus(DelStatusEnum.NORMAL.getStatus());

        docFileFolderService.save(docFileFolder);
        if (parentFolderId > 0L) {
            docFileFolderService.updateFolderCount(parentFolderId, 1);
        }

        DocFileFolderResVO fileFolderResVO = new DocFileFolderResVO();
        fileFolderResVO.setId(docFileFolder.getId());
        fileFolderResVO.setName(docFileFolder.getName());
        fileFolderResVO.setCreateAt(docFileFolder.getCreateAt());
        fileFolderResVO.setUpdateAt(docFileFolder.getUpdateAt());

        return fileFolderResVO;
    }

    @Override
    public void updateFolder(FileFolderUpdateVO updateVO) {
        DocFileFolder docFileFolder = super.getById(updateVO.getId());
        String newName = docFileFolder.getName();
        if (newName.trim().equals(updateVO.getName())) {
            return;
        }
        DocFileFolder update = new DocFileFolder();
        update.setId(docFileFolder.getId());
        update.setName(updateVO.getName());
        docFileFolderService.updateById(update);
    }

    @Override
    public void deleteFolder(FileFolderDelVO delVO) {
        DocFileFolder docFileFolder = super.getById(delVO.getId());

        List<DocFileFolder> childList = new ArrayList<>();
        childList.add(docFileFolder);
        super.getChild(Collections.singletonList(delVO.getId()), childList);
        List<Long> idList = childList.stream().map(DocFileFolder::getId).distinct().collect(Collectors.toList());
        transactionTemplate.execute(status -> {
            docFileFolderService.update(Wrappers.<DocFileFolder>lambdaUpdate()
                .in(DocFileFolder::getId, idList)
                .set(DocFileFolder::getStatus, DelStatusEnum.DEL.getStatus()));
            Long parentId = docFileFolder.getParentId();
            if (Objects.nonNull(parentId) && parentId > 0L) {
                docFileFolderService.updateFolderCount(parentId, -1);
            }
            return null;
        });
    }

    @Override
    public void moveFolder(FileFolderMoveVO moveVO) {
        Long id = moveVO.getId();
        DocFileFolder currentFolder = super.getById(id);

        Long newFolderId = moveVO.getNewFolderId();

        DocFileFolder update = new DocFileFolder();
        update.setId(id);
        update.setParentId(newFolderId);
        docFileFolderService.updateById(update);
        docFileFolderService.updateFolderCount(newFolderId, 1);
        Long parentId = currentFolder.getParentId();
        // 如果不是顶级文件夹
        if (Objects.nonNull(parentId) && parentId > 0L) {
            // 减少该目录记录的直属子目录数量
            docFileFolderService.updateFolderCount(parentId, -1);
        }
    }

    @Override
    public List<DocFileFolderResVO> getFolderPath(Long folderId) {
        DocFileFolder docFileFolder = this.getById(folderId);
        List<DocFileFolder> paths = Lists.newArrayList();
        paths.add(docFileFolder);
        this.getParentFolders(docFileFolder.getParentId(), paths);
        List<DocFileFolderResVO> resVOS = new ArrayList<>(paths.size());
        for (int i = paths.size() - 1; i >= 0; i--) {
            DocFileFolder folder = paths.get(i);
            DocFileFolderResVO resVO = new DocFileFolderResVO();
            resVO.setId(folder.getId());
            resVO.setName(resVO.getName());
            resVOS.add(resVO);
        }
        return resVOS;
    }

    @Override
    public void copyFolder(FileFolderCopyVO copyVO) {
        DocFileFolder docFileFolder = super.getById(copyVO.getId());
        // 如果是复制到同一个目录，不做任何操作
        if (docFileFolder.getParentId().equals(copyVO.getFolderId())) {
            return;
        }
        // TODO：待续。。。。。。
        DocFileFolder targetFolder = super.getById(copyVO.getFolderId());
        List<DocFileFolder> childs = Lists.newArrayList();
        super.getChild(Collections.singletonList(copyVO.getId()), childs);
    }

    private void getParentFolders(Long parentId, List<DocFileFolder> parentFileFolders) {
        if (Objects.isNull(parentId) || parentId <= 0L) {
            return;
        }
        DocFileFolder docFileFolder = docFileFolderService.getById(parentId);
        parentFileFolders.add(docFileFolder);
        this.getParentFolders(docFileFolder.getParentId(), parentFileFolders);
    }

    private List<DocFileResVO> filterFileList(List<DocFileFolder> fileFolders) {
        return fileFolders.stream()
            .filter(folder -> FileFolderFormatEnum.FILE.getFormat().equals(folder.getFormat()))
            .map(fileFolder -> {
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
}
