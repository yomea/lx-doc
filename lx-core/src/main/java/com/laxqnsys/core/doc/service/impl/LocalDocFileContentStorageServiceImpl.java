package com.laxqnsys.core.doc.service.impl;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.model.vo.DocFileContentResVO;
import com.laxqnsys.core.doc.service.IDocFileContentStorageService;
import com.laxqnsys.core.properties.DocContentStorageProperties;
import com.laxqnsys.core.properties.LxDocWebProperties;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author wuzhenhong
 * @date 2025/2/28 16:17
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "lx.doc.storage", name = "type", havingValue = "local")
public class LocalDocFileContentStorageServiceImpl implements IDocFileContentStorageService {

    private static final String USER_ID_PREFIX = "user_id_%s";
    private static final String FILE_ID_PREFIX = "file_id_%s";
    // file_{版本}
    private static final String FILE_VERSION_PREFIX = "file_%s";
    private String path;

    public LocalDocFileContentStorageServiceImpl(LxDocWebProperties lxDocWebProperties) {
        DocContentStorageProperties storage = lxDocWebProperties.getStorage();
        String path = storage.getPath();
        if (!StringUtils.hasText(path)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "存储路径未配置！");
        }
        if (!path.endsWith("/") && !path.endsWith("\\")) {
            this.path = path + File.pathSeparator;
        }
    }

    @Override
    public boolean create(DocFileFolder fileFolder, Runnable afterSuccess) {
        String dirPath =
            this.getFileDirPath(fileFolder.getId());
        File file = new File(dirPath);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                    String.format("在文件夹%s下创建文件失败，请检查文件夹权限", this.path));
            }
        }
        String filePath = this.getFilePath(dirPath, fileFolder.getVersion());
        File finalFile = new File(filePath);
        if (!finalFile.exists()) {
            try {
                boolean createSuccess = finalFile.createNewFile();
                if (!createSuccess) {
                    throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                        String.format("在文件夹%s下创建文件失败，请检查文件夹权限", this.path));
                }
            } catch (IOException e) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                    String.format("在文件夹%s下创建文件失败，请检查文件夹权限", this.path), e);
            }
        }
        String content = fileFolder.getContent();
        if (StringUtils.hasText(content)) {
            try (FileOutputStream outputStream = new FileOutputStream(finalFile)) {
                outputStream.write(content.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            } catch (FileNotFoundException e) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                    String.format("在文件夹%s下创建文件失败，请检查文件夹权限", this.path), e);
            } catch (IOException e) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                    String.format("在文件夹%s下创建文件失败，请检查文件夹权限", this.path), e);
            }
        }
        afterSuccess.run();
        return true;
    }

    @Override
    public boolean update(DocFileFolder fileFolder, Runnable afterSuccess) {
        // 只新增版本
        return this.create(fileFolder, afterSuccess);
    }

    @Override
    public boolean delete(DocFileFolder docFileFolder) {
        // 暂时不去删除实际的文件
        throw new UnsupportedOperationException("暂不支持物理删除文件！");
    }

    @Override
    public DocFileContentResVO getFileContent(DocFileFolder docFileFolder) {
        return null;
    }

    @Override
    public void downloadFileContent(DocFileFolder docFileFolder, HttpServletResponse response) {

    }

    @Override
    public boolean copy(List<DocFileFolder> fileFolders, Runnable afterSuccess) {
        return false;
    }

    private String getFileDirPath(Long fileId) {
        return this.path + String.format(USER_ID_PREFIX, LoginContext.getUserId()) + String.format(FILE_ID_PREFIX,
            fileId);
    }

    private String getFilePath(Long fileId, Integer version) {
        return this.path + String.format(USER_ID_PREFIX, LoginContext.getUserId()) + String.format(FILE_ID_PREFIX,
            fileId) + File.pathSeparator + String.format(FILE_VERSION_PREFIX, version);
    }

    private String getFilePath(String dirPath, Integer version) {
        return dirPath + File.pathSeparator + String.format(FILE_VERSION_PREFIX, version);
    }
}
