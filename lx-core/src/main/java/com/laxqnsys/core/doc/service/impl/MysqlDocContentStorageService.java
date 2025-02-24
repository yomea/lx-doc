package com.laxqnsys.core.doc.service.impl;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.doc.dao.entity.DocFileContent;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.service.IDocContentStorageService;
import com.laxqnsys.core.doc.service.IDocFileContentService;
import com.laxqnsys.core.enums.FileStorageTypeEnum;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wuzhenhong
 * @date 2025/2/20 9:52
 */
@Service
public class MysqlDocContentStorageService implements IDocContentStorageService {

    @Autowired
    private IDocFileContentService docFileContentService;

    @Override
    public Integer storageType() {
        return FileStorageTypeEnum.MYSQL.getType();
    }

    @Override
    public boolean create(DocFileFolder file, String content) {

        DocFileContent saveFileContent = new DocFileContent();
        saveFileContent.setVersion(0);
        saveFileContent.setCreatorId(file.getCreatorId());
        saveFileContent.setCreateAt(file.getCreateAt());
        saveFileContent.setUpdateAt(file.getUpdateAt());
        saveFileContent.setContent(content);
        return docFileContentService.save(saveFileContent);
    }

    @Override
    public boolean update(DocFileFolder file, String content) {

    }

    @Override
    public boolean delete(Long id) {

    }

    @Override
    public void download(Long fileId, HttpServletResponse response) {

    }

    private DocFileContent getByFileId(Long fileId) {
        DocFileContent docFileContent = docFileContentService.getById(fileId);
        if (Objects.isNull(docFileContent)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), String.format("id为%s的文件未找到", fileId));
        }
        if (!docFileContent.getCreatorId().equals(LoginContext.getUserId())) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "非法访问！");
        }
        return docFileContent;
    }

}
