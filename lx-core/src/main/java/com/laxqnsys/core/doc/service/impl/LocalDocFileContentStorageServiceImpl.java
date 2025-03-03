package com.laxqnsys.core.doc.service.impl;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.context.LoginContext;
import com.laxqnsys.core.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.doc.model.dto.DocFileCopyDTO;
import com.laxqnsys.core.doc.model.vo.DocFileContentResVO;
import com.laxqnsys.core.doc.service.AbstractDocFileContentStorageService;
import com.laxqnsys.core.properties.DocContentStorageProperties;
import com.laxqnsys.core.properties.LxDocWebProperties;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
public class LocalDocFileContentStorageServiceImpl extends AbstractDocFileContentStorageService {

    private static final int BUFF_SIZE = 1024 * 4;
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
    public boolean create(DocFileFolder fileFolder) {
        File finalFile = this.createEmptyFile(fileFolder.getId(), fileFolder.getVersion());
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
        return true;
    }

    @Override
    public boolean copy(List<DocFileFolder> fileFolders) {
        List<DocFileCopyDTO> copyDTOList = fileFolders.stream().map(fileFolder -> {
            String filePath = this.getFilePath(fileFolder.getOldId(), fileFolder.getVersion());
            File file = new File(filePath);
            if (!file.exists()) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                    String.format("id为%s的文件不存在！原因=》文件系统中未找到对应的文件内容", fileFolder.getOldId()));
            }
            File finalFile = this.createEmptyFile(fileFolder.getId(), 0);
            DocFileCopyDTO copyDTO = new DocFileCopyDTO();
            copyDTO.setOldFile(file);
            copyDTO.setNewFile(finalFile);
            return copyDTO;
        }).collect(Collectors.toList());
        copyDTOList.stream().forEach(copyDTO -> {
            File oldFile = copyDTO.getOldFile();
            File newFile = copyDTO.getNewFile();
            this.copyFile(oldFile, newFile);
        });
        return true;
    }

    @Override
    public boolean update(DocFileFolder fileFolder) {
        // 只新增版本
        return this.create(fileFolder);
    }

    @Override
    public boolean delete(DocFileFolder docFileFolder) {
        // 暂时不去删除实际的文件
        throw new UnsupportedOperationException("暂不支持物理删除文件！");
    }

    @Override
    public DocFileContentResVO getFileContent(DocFileFolder docFileFolder) {
        Long id = docFileFolder.getId();
        String filePath = this.getFilePath(id, docFileFolder.getVersion());
        File file = new File(filePath);
        if (!file.exists()) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("id为%s的文件不存在！原因=》文件系统中未找到对应的文件内容", id));
        }
        try (FileInputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            this.copyFile(inputStream, outputStream);
            byte[] docContentStream = outputStream.toByteArray();
            DocFileContentResVO resVO = new DocFileContentResVO();
            resVO.setId(id);
            resVO.setName(Objects.nonNull(docFileFolder) ? docFileFolder.getName() : "");
            resVO.setContent(new String(docContentStream, StandardCharsets.UTF_8));
            resVO.setUpdateAt(docFileFolder.getUpdateAt());
            resVO.setCreateAt(docFileFolder.getCreateAt());
            return resVO;
        } catch (FileNotFoundException e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "获取文件失败！", e);
        } catch (IOException e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "获取文件失败！", e);
        }
    }

    @Override
    public void downloadFileContent(DocFileFolder docFileFolder, HttpServletResponse response) {
        Long id = docFileFolder.getId();
        String filePath = this.getFilePath(id, docFileFolder.getVersion());
        File file = new File(filePath);
        if (!file.exists()) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("id为%s的文件不存在！原因=》文件系统中未找到对应的文件内容", id));
        }
        try (FileInputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = response.getOutputStream()
        ) {
            String fileName = docFileFolder.getName();
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            fileName = fileName.replace("+", "%20");    //IE下载文件名空格变+号问题
            response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            response.setContentType("application/octet-stream; charset=utf-8");
            this.copyFile(inputStream, outputStream);
            outputStream.flush();
        } catch (FileNotFoundException e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "下载文件流失败！", e);
        } catch (IOException e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "下载文件流失败！", e);
        }
    }

    private void copyFile(File srcFile, File targetFile) {
        try (FileInputStream fis = new FileInputStream(srcFile);
            FileOutputStream fos = new FileOutputStream(targetFile);
            FileChannel inChannel = fis.getChannel();
            FileChannel outChannel = fos.getChannel()) {

            long size = inChannel.size();
            long position = 0;

            while (position < size) {
                position += inChannel.transferTo(position, size - position, outChannel);
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "复制文件失败！", e);
        }
    }

    private void copyFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        int len;
        byte[] buff = new byte[BUFF_SIZE];
        while ((len = inputStream.read(buff)) > 0) {
            outputStream.write(buff, 0, len);
        }
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

    private File createEmptyFile(Long id, Integer version) {

        String dirPath =
            this.getFileDirPath(id);
        File file = new File(dirPath);
        if (!file.exists() && !file.mkdirs()) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("在文件夹%s下创建文件失败，请检查文件夹权限", this.path));
        }
        String filePath = this.getFilePath(dirPath, version);
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
        return finalFile;
    }
}
