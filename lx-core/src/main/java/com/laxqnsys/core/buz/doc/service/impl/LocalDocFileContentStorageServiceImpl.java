package com.laxqnsys.core.buz.doc.service.impl;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.buz.doc.service.AbstractFileSystemStorageService;
import com.laxqnsys.core.other.context.LoginContext;
import com.laxqnsys.core.buz.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.buz.doc.model.dto.DocFileCopyDTO;
import com.laxqnsys.core.buz.doc.model.vo.DocFileContentResVO;
import com.laxqnsys.core.other.properties.DocContentStorageProperties;
import com.laxqnsys.core.other.properties.LxDocWebProperties;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
@ConditionalOnProperty(prefix = "lx.doc.docStorage", name = "type", havingValue = "local")
public class LocalDocFileContentStorageServiceImpl extends AbstractFileSystemStorageService {

    private static final int BUFF_SIZE = 1024 * 4;
    private static final String USER_ID_PREFIX = "user_id_%s";
    private static final String FILE_ID_PREFIX = "file_id_%s";
    // file_{版本}
    private static final String FILE_VERSION_PREFIX = "file_%s";
    private String path;

    public LocalDocFileContentStorageServiceImpl(LxDocWebProperties lxDocWebProperties) {
        DocContentStorageProperties storage = lxDocWebProperties.getDocStorage();
        if(Objects.isNull(storage)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文档内容存储路径未配置！");
        }
        String path = storage.getPath();
        if (!StringUtils.hasText(path)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文档内容存储路径未配置！");
        }
        if (!path.endsWith("/") && !path.endsWith("\\")) {
            this.path = path + File.separator;
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
            String filePath = this.getFilePath(fileFolder.getOldId(), fileFolder.getOldVersion());
            File file = new File(filePath);
            if (!file.exists()) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                    String.format("id为%s的文件不存在！原因=》文件系统中未找到对应的文件内容", fileFolder.getOldId()));
            }
            File finalFile = this.createEmptyFile(fileFolder.getId(), fileFolder.getVersion());
            DocFileCopyDTO copyDTO = new DocFileCopyDTO();
            copyDTO.setOldFile(file);
            copyDTO.setNewFile(finalFile);
            return copyDTO;
        }).collect(Collectors.toList());
        copyDTOList.forEach(copyDTO -> {
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
        String filePath = this.getFilePath(docFileFolder.getOldId(), docFileFolder.getOldVersion());
        File file = new File(filePath);
        return file.exists() ? file.delete() : true;
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
            resVO.setName(docFileFolder.getName());
            resVO.setContent(new String(docContentStream, StandardCharsets.UTF_8));
            resVO.setUpdateAt(docFileFolder.getUpdateAt());
            resVO.setCreateAt(docFileFolder.getCreateAt());
            return resVO;
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
        return this.path + String.format(USER_ID_PREFIX, LoginContext.getUserId()) + File.separator + String.format(FILE_ID_PREFIX,
            fileId);
    }

    private String getFilePath(Long fileId, Integer version) {
        return this.path + String.format(USER_ID_PREFIX, LoginContext.getUserId()) + File.separator + String.format(FILE_ID_PREFIX,
            fileId) + File.separator + String.format(FILE_VERSION_PREFIX, version);
    }

    private String getFilePath(String dirPath, Integer version) {
        return dirPath + File.separator + String.format(FILE_VERSION_PREFIX, version);
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
