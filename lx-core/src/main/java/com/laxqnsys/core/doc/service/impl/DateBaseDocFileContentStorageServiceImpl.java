package com.laxqnsys.core.doc.service.impl;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.doc.dao.entity.DocFileContent;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.model.dto.DocFileCopyDTO;
import com.laxqnsys.core.doc.model.vo.DocFileContentResVO;
import com.laxqnsys.core.doc.service.IDocFileContentService;
import com.laxqnsys.core.doc.service.IDocFileContentStorageService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

/**
 * 默认实现
 * @author wuzhenhong
 * @date 2025/2/28 14:33
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "lx.doc.storage", name = "type", havingValue = "dataBase", matchIfMissing = true)
public class DateBaseDocFileContentStorageServiceImpl implements IDocFileContentStorageService {

    @Autowired
    private IDocFileContentService docFileContentService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public boolean create(DocFileFolder fileFolder, Runnable afterSuccess) {
        DocFileContent saveFileContent = new DocFileContent();
        saveFileContent.setVersion(0);
        saveFileContent.setCreatorId(fileFolder.getCreatorId());
        saveFileContent.setCreateAt(fileFolder.getCreateAt());
        saveFileContent.setUpdateAt(fileFolder.getUpdateAt());
        saveFileContent.setContent(fileFolder.getContent());
        saveFileContent.setId(fileFolder.getId());
        return transactionTemplate.execute(status -> {
            boolean success = docFileContentService.save(saveFileContent);
            if (success) {
                afterSuccess.run();
            }
            return success;
        });
    }

    @Override
    public boolean update(DocFileFolder fileFolder, Runnable afterSuccess) {
        DocFileContent docFileContent = this.getByFileId(fileFolder.getId());
        DocFileContent update = new DocFileContent();
        update.setId(docFileContent.getId());
        update.setContent(fileFolder.getContent());
        update.setUpdateAt(fileFolder.getUpdateAt());
        return transactionTemplate.execute(status -> {
            boolean success = docFileContentService.updateById(update);
            if (success) {
                afterSuccess.run();
            }
            return success;
        });
    }

    @Override
    public boolean delete(DocFileFolder docFileFolder) {
        throw new UnsupportedOperationException("暂不支持物理删除文件！");
    }

    @Override
    public DocFileContentResVO getFileContent(DocFileFolder docFileFolder) {
        Long id = docFileFolder.getId();
        DocFileContent docFileContent = this.getByFileId(id);

        DocFileContentResVO resVO = new DocFileContentResVO();
        resVO.setId(id);
        resVO.setName(Objects.nonNull(docFileFolder) ? docFileFolder.getName() : "");
        resVO.setContent(docFileContent.getContent());
        resVO.setUpdateAt(docFileContent.getUpdateAt());
        resVO.setCreateAt(docFileContent.getCreateAt());
        return resVO;
    }

    @Override
    public void downloadFileContent(DocFileFolder docFileFolder, HttpServletResponse response) {

        DocFileContent fileContent = this.getByFileId(docFileFolder.getId());
        OutputStream os = null;
        InputStream fis = null;
        try {
            os = response.getOutputStream();
            String fileName = docFileFolder.getName();
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            fileName = fileName.replace("+", "%20");    //IE下载文件名空格变+号问题
            response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            response.setContentType("application/octet-stream; charset=utf-8");
            String content = fileContent.getContent();
            if (StringUtils.hasText(content)) {
                os.write(content.getBytes(StandardCharsets.UTF_8));
            }
            os.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "获取文档内容失败！");
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "获取文档内容失败！");
            }
        }
    }

    @Override
    public boolean copy(List<DocFileFolder> fileFolders, Runnable afterSuccess) {
        List<DocFileCopyDTO> updateList = fileFolders.stream().map(file -> {
            DocFileCopyDTO update = new DocFileCopyDTO();
            update.setOldFileId(file.getOldId());
            update.setNewFileId(file.getId());
            return update;
        }).collect(Collectors.toList());
        Long userId = LoginContext.getUserId();
        return transactionTemplate.execute(status -> {
            int row = docFileContentService.copyByFileIdList(updateList, userId);
            boolean success = row > 0;
            if (success) {
                afterSuccess.run();
            }
            return success;
        });
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
