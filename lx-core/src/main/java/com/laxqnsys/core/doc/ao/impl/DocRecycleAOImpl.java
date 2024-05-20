package com.laxqnsys.core.doc.ao.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.doc.ao.AbstractDocFileFolderAO;
import com.laxqnsys.core.doc.ao.DocRecycleAO;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.dao.entity.DocRecycle;
import com.laxqnsys.core.doc.model.vo.DocFileAndFolderResVO;
import com.laxqnsys.core.doc.model.vo.DocFileFolderResVO;
import com.laxqnsys.core.doc.model.vo.DocFileResVO;
import com.laxqnsys.core.doc.model.vo.DocRecycleReqVO;
import com.laxqnsys.core.doc.service.IDocFileFolderService;
import com.laxqnsys.core.doc.service.IDocRecycleService;
import com.laxqnsys.core.enums.DelStatusEnum;
import com.laxqnsys.core.enums.FileFolderFormatEnum;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author wuzhenhong
 * @date 2024/5/17 16:37
 */
@Service
public class DocRecycleAOImpl extends AbstractDocFileFolderAO implements DocRecycleAO {

    @Autowired
    private IDocRecycleService docRecycleService;

    @Autowired
    private IDocFileFolderService docFileFolderService;

    @Override
    public DocFileAndFolderResVO getRecycleFolderAndFileList(String name) {

        Long userId = LoginContext.getUserId();
        List<DocRecycle> docRecycleList = docRecycleService.list(Wrappers.<DocRecycle>lambdaQuery()
            .eq(DocRecycle::getUserId, userId)
            .like(StringUtils.hasText(name), DocRecycle::getName, name));
        DocFileAndFolderResVO docFileAndFolderResVO = new DocFileAndFolderResVO();
        docFileAndFolderResVO.setFileList(Collections.emptyList());
        docFileAndFolderResVO.setFolderList(Collections.emptyList());
        if (CollectionUtils.isEmpty(docRecycleList)) {
            return docFileAndFolderResVO;
        }
        List<Long> folderIdList = docRecycleList.stream().map(DocRecycle::getFolderId).distinct()
            .collect(Collectors.toList());
        List<DocFileFolder> folderList = docFileFolderService.listByIds(folderIdList);

        List<DocFileFolderResVO> fileFolderBaseResVOList = folderList.stream()
            .filter(folder -> FileFolderFormatEnum.FOLDER.getFormat().equals(folder.getFormat()))
            .map(fileFolder -> {
                DocFileFolderResVO resVO = new DocFileFolderResVO();
                resVO.setId(fileFolder.getId());
                resVO.setName(fileFolder.getName());
                return resVO;
            }).collect(Collectors.toList());

        List<DocFileResVO> fileList = this.filterFileList(folderList);
        docFileAndFolderResVO.setFolderList(fileFolderBaseResVOList);
        docFileAndFolderResVO.setFileList(fileList);
        return docFileAndFolderResVO;
    }

    @Override
    public void restore(DocRecycleReqVO reqVO) {
        Long id = reqVO.getId();
        DocFileFolder docFileFolder = this.getRecycleById(id);

        List<DocFileFolder> childs = new ArrayList<>();
        childs.add(docFileFolder);
        super.getChild(Collections.singletonList(id), childs);

        transactionTemplate.execute(status -> {
           docFileFolderService.update(Wrappers.<DocFileFolder>lambdaUpdate()
               .in(DocFileFolder::getId, childs.stream().map(DocFileFolder::getId)
                   .distinct().collect(Collectors.toList()))
               .set(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus()));
           docRecycleService.remove(Wrappers.<DocRecycle>lambdaQuery()
               .eq(DocRecycle::getUserId, docFileFolder.getCreatorId())
               .eq(DocRecycle::getFolderId, id));
            return null;
        });

    }

    @Override
    public void completelyDelete(DocRecycleReqVO reqVO) {
        Long id = reqVO.getId();
        DocFileFolder docFileFolder = this.getRecycleById(id);
        docRecycleService.remove(Wrappers.<DocRecycle>lambdaQuery()
            .eq(DocRecycle::getUserId, docFileFolder.getCreatorId())
            .eq(DocRecycle::getFolderId, id));
    }

    @Override
    public void emptyRecycle() {
        Long userId = LoginContext.getUserId();
        docRecycleService.remove(Wrappers.<DocRecycle>lambdaQuery()
            .eq(DocRecycle::getUserId, userId));
    }

    private DocFileFolder getRecycleById(Long id) {
        DocFileFolder docFileFolder = docFileFolderService.getById(id);
        Long userId = LoginContext.getUserId();
        if(!docFileFolder.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "禁止访问");
        }
        if(DelStatusEnum.NORMAL.getStatus().equals(docFileFolder.getStatus())) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "该文件未被删除，无需重复恢复");
        }
        return docFileFolder;
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
