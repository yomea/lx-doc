package com.laxqnsys.core.doc.ao.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.constants.CommonCons;
import com.laxqnsys.core.doc.ao.DocFileFolderAO;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.model.vo.DocFileAndFolderResVO;
import com.laxqnsys.core.doc.model.vo.DocFileFolderResVO;
import com.laxqnsys.core.doc.model.vo.DocFileResVO;
import com.laxqnsys.core.doc.model.vo.FileFolderQueryVO;
import com.laxqnsys.core.doc.service.IDocFileFolderService;
import com.laxqnsys.core.enums.DelStatusEnum;
import com.laxqnsys.core.enums.FileFolderFormatEnum;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
    public List<DocFileFolderResVO> getFolderTree(Long folderId) {

        if (Objects.isNull(folderId) || folderId <= 0L) {
            folderId = 0L;
        }
        List<DocFileFolder> fileFolders = docFileFolderService.list(Wrappers.<DocFileFolder>lambdaQuery()
            .eq(DocFileFolder::getParentId, folderId)
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
        List<DocFileFolder> fileFolders = docFileFolderService.list(Wrappers.
            <DocFileFolder>query().orderBy(
                StringUtils.hasText(queryVO.getSortField()) && StringUtils.hasText(queryVO.getSortType())
                , "asc".equalsIgnoreCase(queryVO.getSortType()), queryVO.getSortField())
            .lambda()
            .eq(DocFileFolder::getParentId, folderId)
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

        List<DocFileFolder> fileFolders = docFileFolderService.list(Wrappers.<DocFileFolder>lambdaQuery()
            .like(DocFileFolder::getName, name)
            .eq(StringUtils.hasText(queryVO.getFileType()), DocFileFolder::getFileType, queryVO.getFileType()
            ).eq(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus()));

        List<Long> idList = fileFolders.stream()
            .filter(folder -> FileFolderFormatEnum.FOLDER.getFormat().equals(folder.getFormat()))
            .map(DocFileFolder::getPath).filter(StringUtils::hasText)
            .flatMap(path -> Arrays.stream(path.split(",")).map(Long::parseLong))
            .distinct().collect(Collectors.toList());
        Map<Long, DocFileFolder> folderMap = Collections.emptyMap();
        if (!CollectionUtils.isEmpty(idList)) {
            List<DocFileFolder> tempList = docFileFolderService.listByIds(idList);
            folderMap = tempList.stream()
                .collect(Collectors.toMap(DocFileFolder::getId, Function.identity(), (v1, v2) -> v1));
        }
        Map<Long, DocFileFolder> finalFolderMap = folderMap;
        List<DocFileFolderResVO> fileFolderBaseResVOList = fileFolders.stream()
            .filter(folder -> FileFolderFormatEnum.FOLDER.getFormat().equals(folder.getFormat()))
            .map(fileFolder -> {
                DocFileFolderResVO resVO = new DocFileFolderResVO();
                resVO.setId(fileFolder.getId());
                resVO.setName(fileFolder.getName());
                String path = fileFolder.getPath();

                if (StringUtils.hasText(path)) {
                    resVO.setPath(Arrays.stream(path.split(",")).map(Long::parseLong).map(id -> {
                        DocFileFolder docFileFolder = finalFolderMap.get(id);
                        if (Objects.isNull(docFileFolder)) {
                            return null;
                        }
                        DocFileFolderResVO fileFolderResVO = new DocFileFolderResVO();
                        fileFolderResVO.setId(id);
                        fileFolderResVO.setName(docFileFolder.getName());
                        return fileFolderResVO;
                    }).filter(Objects::nonNull).collect(Collectors.toList()));
                } else {
                    resVO.setPath(Collections.emptyList());
                }
                return resVO;
            }).collect(Collectors.toList());

        List<DocFileResVO> fileList = this.filterFileList(fileFolders);

        DocFileAndFolderResVO docFileAndFolderResVO = new DocFileAndFolderResVO();
        docFileAndFolderResVO.setFolderList(fileFolderBaseResVOList);
        docFileAndFolderResVO.setFileList(fileList);

        return docFileAndFolderResVO;
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
                resVO.setCreateAt(CommonCons.YYYY_MM_SS_HH_MM_SS.format(fileFolder.getCreateAt()));
                resVO.setUpdateAt(CommonCons.YYYY_MM_SS_HH_MM_SS.format(fileFolder.getUpdateAt()));
                return resVO;
            }).collect(Collectors.toList());
    }
}
