package com.laxqnsys.core.doc.ao.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.aspect.lock.ConcurrentLock;
import com.laxqnsys.core.constants.RedissonLockPrefixCons;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.doc.ao.AbstractDocFileFolderAO;
import com.laxqnsys.core.doc.ao.DocFileFolderAO;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.dao.entity.DocRecycle;
import com.laxqnsys.core.doc.model.dto.DocFileCopyDTO;
import com.laxqnsys.core.doc.model.vo.DocFileAndFolderResVO;
import com.laxqnsys.core.doc.model.vo.DocFileFolderResVO;
import com.laxqnsys.core.doc.model.vo.DocFileResVO;
import com.laxqnsys.core.doc.model.vo.DocSynthFileFolderResVO;
import com.laxqnsys.core.doc.model.vo.FileFolderCopyVO;
import com.laxqnsys.core.doc.model.vo.FileFolderCreateVO;
import com.laxqnsys.core.doc.model.vo.FileFolderDelVO;
import com.laxqnsys.core.doc.model.vo.FileFolderMoveVO;
import com.laxqnsys.core.doc.model.vo.FileFolderQueryVO;
import com.laxqnsys.core.doc.model.vo.FileFolderUpdateVO;
import com.laxqnsys.core.enums.DelStatusEnum;
import com.laxqnsys.core.enums.FileFolderFormatEnum;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author wuzhenhong
 * @date 2024/5/14 19:36
 */
@Service
public class DocFileFolderAOImpl extends AbstractDocFileFolderAO implements DocFileFolderAO {

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
            resVO.setLeaf(fileFolder.getFolderCount() <= 0);
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
                , "asc".equalsIgnoreCase(queryVO.getSortType()), StrUtil.toUnderlineCase(queryVO.getSortField()))
            .lambda()
            .eq(DocFileFolder::getParentId, folderId)
            .eq(DocFileFolder::getCreatorId, userId)
            .apply(StringUtils.hasText(queryVO.getFileType()),
                String.format("(format = %s or (format = %s and file_type = '%s'))",
                    FileFolderFormatEnum.FOLDER.getFormat(), FileFolderFormatEnum.FILE.getFormat(),
                    queryVO.getFileType()))
            /*.eq(StringUtils.hasText(queryVO.getFileType()), DocFileFolder::getFileType, queryVO.getFileType()
            )*/.eq(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus()));

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
            .apply(StringUtils.hasText(queryVO.getFileType()),
                String.format("format = %s or (format = %s and file_type = '%s')",
                    FileFolderFormatEnum.FOLDER.getFormat(), FileFolderFormatEnum.FILE.getFormat(),
                    queryVO.getFileType()))
            .eq(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus()));

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
        } else {
            // 检查父文件夹是否合法合理
            DocFileFolder parent = super.getById(parentFolderId);
            if (FileFolderFormatEnum.FILE.getFormat().equals(parent.getFormat())) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "只允许在文件夹下创建文件！");
            }
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

        Long finalParentFolderId = parentFolderId;
        transactionTemplate.execute(status -> {
            docFileFolderService.save(docFileFolder);
            if (finalParentFolderId > 0L) {
                docFileFolderService.updateFolderCount(finalParentFolderId, 1);
            }
            return null;
        });

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
    @ConcurrentLock(key = RedissonLockPrefixCons.DEL_FOLDER + "${delVO.id}")
    public void deleteFolder(FileFolderDelVO delVO) {
        DocFileFolder docFileFolder = super.getById(delVO.getId());

        List<DocFileFolder> childList = new ArrayList<>();
        childList.add(docFileFolder);
        super.getChild(Collections.singletonList(delVO.getId()), childList);
        List<Long> idList = childList.stream().map(DocFileFolder::getId).distinct().collect(Collectors.toList());
        DocRecycle docRecycle = new DocRecycle();
        docRecycle.setIds(idList.stream().map(String::valueOf).collect(Collectors.joining(",")));
        docRecycle.setFolderId(docFileFolder.getId());
        docRecycle.setName(docFileFolder.getName());
        docRecycle.setUserId(LoginContext.getUserId());
        docRecycle.setCreateAt(LocalDateTime.now());
        transactionTemplate.execute(status -> {
            docFileFolderService.update(Wrappers.<DocFileFolder>lambdaUpdate()
                .in(DocFileFolder::getId, idList)
                .set(DocFileFolder::getStatus, DelStatusEnum.DEL.getStatus()));
            // 扔回收站
            docRecycleService.save(docRecycle);
            Long parentId = docFileFolder.getParentId();
            if (Objects.nonNull(parentId) && parentId > 0L) {
                docFileFolderService.updateFolderCount(parentId, -1);
            }
            return null;
        });
    }

    @Override
    @ConcurrentLock(key = RedissonLockPrefixCons.MOVE_FOLDER + "${moveVO.id}")
    public void moveFolder(FileFolderMoveVO moveVO) {
        Long id = moveVO.getId();
        Long newFolderId = moveVO.getNewFolderId();
        if (id.equals(newFolderId)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "不能移动自身到自身下");
        }
        Map<Long, DocFileFolder> map = super.getByIdList(Arrays.asList(id, newFolderId));
        DocFileFolder parent = map.get(newFolderId);
        if (FileFolderFormatEnum.FILE.getFormat().equals(parent.getFormat())) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "只允许迁移到文件夹下");
        }
        DocFileFolder currentFolder = map.get(id);
        if (currentFolder.getParentId().equals(newFolderId)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "已在指定文件夹下，不要重复移入！");
        }
        DocFileFolder update = new DocFileFolder();
        update.setId(id);
        update.setParentId(newFolderId);
        transactionTemplate.execute(status -> {
            docFileFolderService.updateById(update);
            docFileFolderService.updateFolderCount(newFolderId, 1);
            Long parentId = currentFolder.getParentId();
            // 如果不是顶级文件夹
            if (Objects.nonNull(parentId) && parentId > 0L) {
                // 减少该目录记录的直属子目录数量
                docFileFolderService.updateFolderCount(parentId, -1);
            }
            return null;
        });
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
            resVO.setName(folder.getName());
            resVOS.add(resVO);
        }
        return resVOS;
    }

    @Override
    public void copyFolder(FileFolderCopyVO copyVO) {
        Map<Long, DocFileFolder> map = super.getByIdList(Arrays.asList(copyVO.getId(), copyVO.getFolderId()));
        DocFileFolder docFileFolder = map.get(copyVO.getId());
        // 如果是复制到同一个目录，不做任何操作
        /*if (docFileFolder.getParentId().equals(copyVO.getFolderId())) {
            return;
        }*/
        // 目标目录
        DocFileFolder targetFolder = map.get(copyVO.getFolderId());
        if (FileFolderFormatEnum.FILE.getFormat().equals(targetFolder.getFormat())) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "只允许复制到文件夹下！");
        }
        // 获取要移动的文件夹及它的所有子目录和文件
        List<DocFileFolder> childs = Lists.newArrayList();
        childs.add(docFileFolder);
        super.getChild(Collections.singletonList(copyVO.getId()), childs);
        Long userId = LoginContext.getUserId();
        childs.stream().forEach(child -> {
            child.setOldId(child.getId());
            child.setId(null);
            child.setCreatorId(userId);
            // 暂时先不显示
            child.setStatus(DelStatusEnum.DISPLAY.getStatus());
        });

        transactionTemplate.execute(status -> {
            // 批量保存
            docFileFolderService.saveBatch(childs);
            Map<Long, Long> oldMapNew = childs.stream()
                .collect(Collectors.toMap(DocFileFolder::getOldId, DocFileFolder::getId, (v1, v2) -> v1));
            List<DocFileFolder> updateList = childs.stream().map(e -> {
                DocFileFolder update = new DocFileFolder();
                update.setId(e.getId());
                if (copyVO.getId().equals(e.getOldId())) {
                    update.setParentId(copyVO.getFolderId());
                } else {
                    update.setParentId(oldMapNew.getOrDefault(e.getParentId(), 0L));
                }
                update.setStatus(DelStatusEnum.NORMAL.getStatus());
                return update;
            }).collect(Collectors.toList());
            docFileFolderService.updateBatchById(updateList);
            List<DocFileCopyDTO> copyList = childs.stream()
                .filter(e -> FileFolderFormatEnum.FILE.getFormat().equals(e.getFormat())).map(file -> {
                    DocFileCopyDTO update = new DocFileCopyDTO();
                    update.setOldFileId(file.getOldId());
                    update.setNewFileId(file.getId());
                    return update;
                }).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(copyList)) {
                docFileContentService.copyByFileIdList(copyList, userId);
            }
            docFileFolderService.updateFolderCount(targetFolder.getId(), 1);
            return null;
        });
    }

    @Override
    public List<DocSynthFileFolderResVO> getAllFolderTree() {
        // 获取当前登录人的所有文件夹
        List<DocFileFolder> docFileFolderList = docFileFolderService.list(Wrappers.<DocFileFolder>lambdaQuery()
            .in(DocFileFolder::getCreatorId, LoginContext.getUserId())
            .eq(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus()));
        if(CollectionUtils.isEmpty(docFileFolderList)) {
            return Collections.emptyList();
        }
        Map<Long, List<DocFileFolder>> parentIdMapFolderMap = docFileFolderList.stream().collect(Collectors.groupingBy(DocFileFolder::getParentId));
        // 获取顶级目录
        List<DocFileFolder> topFolders = parentIdMapFolderMap.get(0L);
        if(CollectionUtils.isEmpty(topFolders)) {
            return Collections.emptyList();
        }
        return topFolders.stream().map(folder -> this.buildDocSynthFileFolderResVO(folder, parentIdMapFolderMap))
            .collect(Collectors.toList());
    }

    private DocSynthFileFolderResVO buildDocSynthFileFolderResVO(DocFileFolder folder,
        Map<Long, List<DocFileFolder>> parentIdMapFolderMap) {

        DocSynthFileFolderResVO resVO = new DocSynthFileFolderResVO();
        resVO.setId(folder.getId());
        resVO.setName(folder.getName());
        resVO.setType(folder.getFileType());
        resVO.setImg(folder.getImg());
        resVO.setFolder(FileFolderFormatEnum.FOLDER.getFormat().equals(folder.getFormat()));

        List<DocFileFolder> children = parentIdMapFolderMap.getOrDefault(folder.getId(), Collections.emptyList());
        resVO.setChildren(children.stream().map(e -> this.buildDocSynthFileFolderResVO(e, parentIdMapFolderMap))
            .collect(Collectors.toList()));
        return resVO;
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
