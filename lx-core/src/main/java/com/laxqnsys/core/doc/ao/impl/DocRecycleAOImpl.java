package com.laxqnsys.core.doc.ao.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.aspect.lock.ConcurrentLock;
import com.laxqnsys.core.constants.RedissonLockPrefixCons;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author wuzhenhong
 * @date 2024/5/17 16:37
 */
@Service
@Slf4j
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
        List<Long> folderIdList = docRecycleList.stream().map(DocRecycle::getId).distinct()
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
    @ConcurrentLock(key = RedissonLockPrefixCons.RESTORE_FOLDER + "${reqVO.id}")
    public void restore(DocRecycleReqVO reqVO) {
        Long id = reqVO.getId();
        DocRecycle docRecycle = docRecycleService.getOne(Wrappers.<DocRecycle>lambdaQuery()
            .eq(DocRecycle::getId, id).last("limit 1"));
        if(Objects.isNull(docRecycle)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "该文件夹已被恢复请刷新列表！");
        }
        String ids = docRecycle.getIds();
        if(!StringUtils.hasText(ids)) {
            log.error("id为{}的文件数据恢复异常", id);
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "数据恢复异常，请联系管理员！");
        }
        List<Long> childIds = Arrays.stream(ids.split(",")).map(Long::parseLong)
            .distinct().collect(Collectors.toList());
        DocFileFolder docFileFolder = this.getRecycleById(id);
        // 检查父级是否被删除，如果被删除，找到其上未被删除的父级，挂在下面
        Long parentId = docFileFolder.getParentId();
        while(Objects.nonNull(parentId) && parentId != 0L) {
            DocFileFolder parent = docFileFolderService.getById(parentId);
            if (Objects.isNull(parent)) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                    String.format("id为%s的父文件夹不存在", parentId));
            }
            if(DelStatusEnum.NORMAL.getStatus().equals(parent.getStatus())) {
                break;
            }
            parentId = parent.getParentId();
        }
        Long finalParentId = parentId;
        transactionTemplate.execute(status -> {
           docFileFolderService.update(Wrappers.<DocFileFolder>lambdaUpdate()
               .in(DocFileFolder::getId, childIds)
               .set(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus()));
           docRecycleService.remove(Wrappers.<DocRecycle>lambdaQuery()
               .eq(DocRecycle::getUserId, docFileFolder.getCreatorId())
               .eq(DocRecycle::getId, id));
            Integer format = docFileFolder.getFormat();
            if(finalParentId != docFileFolder.getParentId()) {
                docFileFolderService.update(Wrappers.<DocFileFolder>lambdaUpdate()
                    .set(DocFileFolder::getParentId, finalParentId)
                    .eq(DocFileFolder::getId, id));
            }
            if(Objects.nonNull(finalParentId) && finalParentId > 0L) {
                if(FileFolderFormatEnum.FOLDER.getFormat().equals(format)) {
                    docFileFolderService.updateFolderCount(finalParentId, 1);
                }
                if(FileFolderFormatEnum.FILE.getFormat().equals(format)) {
                    docFileFolderService.updateFileCount(finalParentId, 1);
                }
            }
            return null;
        });

    }

    @Override
    public void completelyDelete(DocRecycleReqVO reqVO) {
        Long id = reqVO.getId();
        DocFileFolder docFileFolder = this.getRecycleById(id);
        docRecycleService.remove(Wrappers.<DocRecycle>lambdaQuery()
            .eq(DocRecycle::getUserId, docFileFolder.getCreatorId())
            .eq(DocRecycle::getId, id));
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
