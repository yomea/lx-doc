package com.laxqnsys.core.doc.ao.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.doc.ao.DocFileFolderAO;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.model.vo.DocFileAndFolderResVO;
import com.laxqnsys.core.doc.model.vo.DocFileFolderResVO;
import com.laxqnsys.core.doc.model.vo.FileFolderQueryVO;
import com.laxqnsys.core.doc.service.IDocFileFolderService;
import com.laxqnsys.core.enums.DelStatusEnum;
import com.laxqnsys.core.enums.FileFolderFormatEnum;
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
public class DocFileFolderAOImpl implements DocFileFolderAO {

    @Autowired
    private IDocFileFolderService docFileFolderService;

    @Override
    public List<DocFileFolderResVO> getFolderTree(FileFolderQueryVO queryVO) {

        Long folderId = queryVO.getFolderId();
        List<DocFileFolder> fileFolders = docFileFolderService.list(Wrappers.<DocFileFolder>lambdaQuery()
            .eq(Objects.isNull(folderId) || folderId <= 0L, DocFileFolder::getParentId, 0L)
            .eq(Objects.nonNull(folderId) && folderId > 0L, DocFileFolder::getParentId, folderId)
            .eq(DocFileFolder::getFormat, FileFolderFormatEnum.FOLDER.getFormat())
            .eq(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus()));

        return fileFolders.stream().map(fileFolder -> {
            DocFileFolderResVO resVO = new DocFileFolderResVO();
            resVO.setId(fileFolder.getId());
            resVO.setName(fileFolder.getName());
            resVO.setLeaf(fileFolder.getLeaf());
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
        List<DocFileFolder> fileFolders = docFileFolderService.list(Wrappers.
            <DocFileFolder>query().orderBy(
                StringUtils.hasText(queryVO.getSortField()) && StringUtils.hasText(queryVO.getSortType())
                , "asc".equalsIgnoreCase(queryVO.getSortType()), queryVO.getSortField())
            .lambda()
            .eq(DocFileFolder::getParentId, folderId)
            .eq(StringUtils.hasText(queryVO.getFileType()), DocFileFolder::getFileType, queryVO.getFileType()
            ).eq(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus()));

//        fileFolders.stream().filter(folder -> FileFolderFormatEnum.FOLDER.getFormat().equals(folder.getFormat()))
//            .map().collect(Collectors.toList());

        return null;
    }
}
