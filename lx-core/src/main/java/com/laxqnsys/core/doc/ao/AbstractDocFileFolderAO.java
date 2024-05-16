package com.laxqnsys.core.doc.ao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.service.IDocFileFolderService;
import com.laxqnsys.core.enums.DelStatusEnum;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author wuzhenhong
 * @date 2024/5/16 10:53
 */
public abstract class AbstractDocFileFolderAO {

    @Autowired
    protected IDocFileFolderService docFileFolderService;

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

    protected String concatPath(String a, String b) {
        if(!StringUtils.hasText(a) && !StringUtils.hasText(b)) {
            return null;
        }

        if(StringUtils.hasText(a) && !StringUtils.hasText(b)) {
            return a;
        }

        if(!StringUtils.hasText(a) && StringUtils.hasText(b)) {
            return b;
        }

        return a + "," + b;
    }

    protected void getChild(List<Long> parentIdList, List<DocFileFolder> child) {
        if(CollectionUtils.isEmpty(parentIdList)) {
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
