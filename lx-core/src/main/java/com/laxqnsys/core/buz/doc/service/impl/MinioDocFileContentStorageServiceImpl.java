package com.laxqnsys.core.buz.doc.service.impl;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.buz.doc.service.AbstractFileSystemStorageService;
import com.laxqnsys.core.other.constants.CommonCons;
import com.laxqnsys.core.other.context.LoginContext;
import com.laxqnsys.core.buz.doc.dao.entity.DocFileFolder;
import com.laxqnsys.core.buz.doc.model.vo.DocFileContentResVO;
import com.laxqnsys.core.other.properties.DocContentStorageProperties;
import com.laxqnsys.core.other.properties.LxDocWebProperties;
import com.laxqnsys.core.other.properties.MinioFileUploadProperties;
import com.laxqnsys.core.other.util.minio.MinioUtils;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author wuzhenhong
 * @date 2025/2/28 16:17
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "lx.doc.docStorage", name = "type", havingValue = "minio")
public class MinioDocFileContentStorageServiceImpl extends AbstractFileSystemStorageService {

    private static final int BUFF_SIZE = 1024 * 4;
    private static final String USER_ID_PREFIX = "user_id_%s";
    private static final String FILE_ID_PREFIX = "file_id_%s";
    // file_{版本}
    private static final String FILE_VERSION_PREFIX = "file_%s";

    private MinioClient minioClient;

    private String bucket;

    private String path;

    public MinioDocFileContentStorageServiceImpl(LxDocWebProperties lxDocWebProperties) {
        DocContentStorageProperties docStorage = lxDocWebProperties.getDocStorage();
        if (Objects.isNull(docStorage)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "未配置文档内容上传属性！");
        }
        MinioFileUploadProperties minio = docStorage.getMinio();
        this.minioClient = MinioUtils.createMinioClient(minio);
        this.bucket = minio.getBucket();
        String path = docStorage.getPath();
        if (StringUtils.hasText(path)) {
            this.path = path.replace("\\", CommonCons.FORWARD_SLANT);
            if(this.path.startsWith(CommonCons.FORWARD_SLANT)) {
                this.path = this.path.substring(1);
            }
            if(!this.path.endsWith(CommonCons.FORWARD_SLANT)) {
                this.path = this.path + CommonCons.FORWARD_SLANT;
            }
        }
    }

    @Override
    public boolean create(DocFileFolder fileFolder) {
        String objectName =
            this.getFilePath(fileFolder.getId(), fileFolder.getVersion());
        this.upload(() -> {
            String content = fileFolder.getContent();
            byte[] bytes = StringUtils.hasText(content)
                ? content.getBytes(StandardCharsets.UTF_8)
                : new byte[0];
            return new ByteArrayInputStream(bytes);
        }, objectName, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        return true;
    }

    @Override
    public boolean copy(List<DocFileFolder> fileFolders) {
        fileFolders.forEach(fileFolder -> {
            String srcFilePath = this.getFilePath(fileFolder.getOldId(), fileFolder.getOldVersion());
            CopySource source = CopySource.builder()
                .bucket(this.bucket)
                .object(srcFilePath)
                .build();
            String targetFilePath = this.getFilePath(fileFolder.getId(), fileFolder.getVersion());
            try {
                this.minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(this.bucket)
                    .source(source)
                    .object(targetFilePath)
                    .build());
            } catch (Exception e) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "复制文件失败！", e);
            }
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
        String objectName = this.getFilePath(docFileFolder.getId(), docFileFolder.getVersion());
        try {
            this.minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(this.bucket)
                    .object(objectName)
                .build());
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), String.format("删除文件【%s】失败", objectName), e);
        }
        return true;
    }

    @Override
    public DocFileContentResVO getFileContent(DocFileFolder docFileFolder) {
        Long id = docFileFolder.getId();
        String filePath = this.getFilePath(id, docFileFolder.getVersion());
        GetObjectResponse response = this.getFile(filePath);
        try (
            InputStream inputStream = response;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            this.readFile(inputStream, outputStream);
            byte[] docContentStream = outputStream.toByteArray();
            DocFileContentResVO resVO = new DocFileContentResVO();
            resVO.setId(id);
            resVO.setName(docFileFolder.getName());
            resVO.setContent(new String(docContentStream, StandardCharsets.UTF_8));
            resVO.setUpdateAt(docFileFolder.getUpdateAt());
            resVO.setCreateAt(docFileFolder.getCreateAt());
            return resVO;
        } catch (IOException e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), String.format("读取文件【%s】失败！", filePath), e);
        }
    }

    @Override
    public void downloadFileContent(DocFileFolder docFileFolder, HttpServletResponse response) {
        Long id = docFileFolder.getId();
        String filePath = this.getFilePath(id, docFileFolder.getVersion());
        GetObjectResponse objectResponse = this.getFile(filePath);
        try (InputStream inputStream = objectResponse;
            OutputStream outputStream = response.getOutputStream()
        ) {
            String fileName = docFileFolder.getName();
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
            fileName = fileName.replace("+", "%20");    //IE下载文件名空格变+号问题
            response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            response.setContentType("application/octet-stream; charset=utf-8");
            this.readFile(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "下载文件流失败！", e);
        }
    }

    private void readFile(InputStream inputStream, OutputStream outputStream) throws IOException {
        int len;
        byte[] buff = new byte[BUFF_SIZE];
        while ((len = inputStream.read(buff)) > 0) {
            outputStream.write(buff, 0, len);
        }
    }

    private String getFilePath(Long fileId, Integer version) {
        return this.path + String.format(USER_ID_PREFIX, LoginContext.getUserId()) + CommonCons.FORWARD_SLANT + String.format(
            FILE_ID_PREFIX,
            fileId) + CommonCons.FORWARD_SLANT + String.format(FILE_VERSION_PREFIX, version);
    }

    private String upload(Supplier<InputStream> supplier, String objectName, String contentType) {
        try (InputStream inputStream = supplier.get()) {
            int size = inputStream.available();
            ObjectWriteResponse response = this.minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(this.bucket)
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .contentType(
                        StringUtils.hasText(contentType) ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .build()
            );
            return response.etag();
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "上传文件失败");
        }
    }

    private GetObjectResponse getFile(String objectName) {
        try {
            GetObjectResponse response = this.minioClient.getObject(GetObjectArgs.builder()
                .bucket(this.bucket)
                .object(objectName)
                .build());
            return response;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), String.format("获取文件【%s】失败！", objectName),
                e);
        }
    }
}
