package com.laxqnsys.core.doc.ao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.service.IDocFileContentService;
import com.laxqnsys.core.doc.service.IDocFileFolderService;
import com.laxqnsys.core.doc.service.IDocRecycleService;
import com.laxqnsys.core.enums.DelStatusEnum;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

/**
 * @author wuzhenhong
 * @date 2024/5/16 10:53
 */
public abstract class AbstractDocFileFolderAO {

    @Autowired
    protected IDocFileFolderService docFileFolderService;

    @Autowired
    protected IDocFileContentService docFileContentService;

    @Autowired
    protected IDocRecycleService docRecycleService;

    @Autowired
    protected TransactionTemplate transactionTemplate;

    protected DocFileFolder getById(Long id) {
        DocFileFolder docFileFolder = docFileFolderService.getById(id);
        if (Objects.isNull(docFileFolder) || !DelStatusEnum.NORMAL.getStatus().equals(docFileFolder.getStatus())) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("id为%s的文件夹不存在或已被删除！", id));
        }
        if (!docFileFolder.getCreatorId().equals(LoginContext.getUserId())) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "非法访问");
        }
        return docFileFolder;
    }

    protected List<DocFileFolder> selectByIdList(List<Long> idList) {
        List<DocFileFolder> docFileFolderList = docFileFolderService.list(Wrappers.<DocFileFolder>lambdaQuery()
            .in(DocFileFolder::getId, idList)
            .eq(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus()));
        if (CollectionUtils.isEmpty(docFileFolderList)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("id为%s的文件夹不存在或已被删除！", idList.stream().map(String::valueOf)
                    .collect(Collectors.joining(","))));
        }
        Set<Long> idSet = docFileFolderList.stream().map(DocFileFolder::getId).collect(Collectors.toSet());
        List<Long> noExitsIdList = idList.stream().filter(id -> !idSet.contains(id)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(noExitsIdList)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("id为%s的文件夹不存在或已被删除！", noExitsIdList.stream().map(String::valueOf)
                    .collect(Collectors.joining(","))));
        }
        Long userId = LoginContext.getUserId();
        List<DocFileFolder> forbidList = docFileFolderList.stream().filter(e -> !e.getCreatorId().equals(userId))
            .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(forbidList)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("id为%s的资源禁止访问！", noExitsIdList.stream().map(String::valueOf)
                    .collect(Collectors.joining(","))));
        }
        return docFileFolderList;
    }

    protected Map<Long, DocFileFolder> getByIdList(List<Long> idList) {
        return this.selectByIdList(idList).stream()
            .collect(Collectors.toMap(DocFileFolder::getId, Function.identity(), (v1, v2) -> v1));
    }

    protected void getChild(List<Long> parentIdList, List<DocFileFolder> child) {
        if (CollectionUtils.isEmpty(parentIdList)) {
            return;
        }
        List<DocFileFolder> fileFolders = docFileFolderService.list(Wrappers.<DocFileFolder>lambdaQuery()
            .in(DocFileFolder::getParentId, parentIdList)
            .eq(DocFileFolder::getStatus, DelStatusEnum.NORMAL.getStatus()));
        child.addAll(fileFolders);
        List<Long> idList = fileFolders.stream().map(DocFileFolder::getId).distinct().collect(Collectors.toList());
        this.getChild(idList, child);
    }
}
