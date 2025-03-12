package com.laxqnsys.core.sys.service.impl;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.properties.FileUploadProperties;
import com.laxqnsys.core.properties.LxDocWebProperties;
import com.laxqnsys.core.properties.MinioFileUploadProperties;
import com.laxqnsys.core.sys.model.bo.FileUploadBO;
import com.laxqnsys.core.sys.service.ISysFileUploadService;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wuzhenhong
 * @date 2025/3/6 20:16
 */
@Service
@ConditionalOnProperty(prefix = "lx.doc.fileUpload", name = "type", havingValue = "minio")
public class SysMinioFileUploadServiceImpl implements ISysFileUploadService {

    private MinioClient minioClient;

    private String endpoint;

    private String bucket;

    private String path;

    public SysMinioFileUploadServiceImpl(LxDocWebProperties lxDocWebProperties) {
        FileUploadProperties fileUpload = lxDocWebProperties.getFileUpload();
        if (Objects.isNull(fileUpload)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "未配置文件上传属性！");
        }
        MinioFileUploadProperties minio = fileUpload.getMinio();
        if (Objects.isNull(minio)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "文件上传配置为 lx.doc.fileUpload.type=minio 时未配置 minio 属性");
        }

        String endpoint = minio.getEndpoint();
        if (!StringUtils.hasText(endpoint)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "文件上传配置为 lx.doc.fileUpload.type=minio 时 endpoint 必须配置");
        }

        String bucket = minio.getBucket();
        if (!StringUtils.hasText(bucket)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "文件上传配置为 lx.doc.fileUpload.type=minio 时 bucket 必须配置");
        }

        String accessKey = minio.getAccessKey();
        if (!StringUtils.hasText(accessKey)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "文件上传配置为 lx.doc.fileUpload.type=minio 时 accessKey 必须配置");
        }
        String secretKey = minio.getSecretKey();
        if (!StringUtils.hasText(secretKey)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "文件上传配置为 lx.doc.fileUpload.type=minio 时 secretKey 必须配置");
        }
        String path = fileUpload.getPath();
        if(StringUtils.hasText(path)) {
            this.path = path.replace("\\", "/");
        }
        this.minioClient = MinioClient.builder().endpoint(endpoint)
            .credentials(minio.getAccessKey(), minio.getSecretKey()).build();
        this.endpoint = endpoint;
        this.bucket = bucket;
        // 检查 bucket 是否存在
        boolean exists;
        try {
            exists = this.minioClient.bucketExists(BucketExistsArgs.builder().bucket(this.bucket).build());
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "检查名为【%s】的 bucket 是否存在时出错！", e);
        }
        if (!exists) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "名为【%s】的 bucket 不存在，请自行创建并赋予可读权限！");
        }
    }


    @Override
    public FileUploadBO upload(MultipartFile file) {
        return this.doUpload(() -> {
            try {
                return file.getInputStream();
            } catch (IOException e) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "上传文件失败！", e);
            }
        }, file.getOriginalFilename(), file.getContentType());
    }

    @Override
    public FileUploadBO upload(byte[] data, String fileName) {
        return this.doUpload(() -> new ByteArrayInputStream(data), fileName, MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    @Override
    public boolean delete(String url) {
        String fileName = this.getFileName(url);
        try {
            this.minioClient.removeObject(RemoveObjectArgs.builder().bucket(this.bucket)
                .object(fileName).build());
            return true;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), String.format("删除url为%s的文件失败！", url), e);
        }
    }

    private FileUploadBO doUpload(Supplier<InputStream> supplier, String fileName, String contentType) {
        String filePath = this.getFilePath(fileName);
        try (InputStream inputStream = supplier.get()) {
            int size = inputStream.available();
            this.minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(this.bucket)
                    .object(filePath)
                    .stream(inputStream, size, -1)
                    .contentType(
                        StringUtils.hasText(contentType) ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .build()
            );
            //文件访问 URL
            String url = this.getFileUrl(filePath);
            return FileUploadBO.builder().url(url).size(Long.valueOf(size)).build();
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "上传文件失败");
        }
    }

    private String getFileName(String url) {
        if (!StringUtils.hasText(url)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "url不能为空！");
        }
        url = url.trim();
        String endpoint = this.endpoint;
        if (!endpoint.endsWith("/")) {
            endpoint = endpoint + "/";
        }
        String uriPrefix = endpoint + this.bucket + "/";
        if (!url.startsWith(uriPrefix)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("url前缀不符合当前minio配置的url=》形入%s[文件路径]文件名", uriPrefix));
        }
        String fileName = url.substring(uriPrefix.length());
        if (!StringUtils.hasText(fileName)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("文件名为空，不符合当前minio配置的url=》形入%s[文件路径]文件名", uriPrefix));
        }
        return fileName;
    }

    private String getFileUrl(String fileName) {
        String endpoint = this.endpoint;
        if (!endpoint.endsWith("/")) {
            endpoint = endpoint + "/";
        }
        if (!fileName.startsWith("/")) {
            fileName = "/" + fileName;
        }
        return endpoint + this.bucket + fileName;
    }

    private String getFilePath(String fileName) {

        String uuid = UUID.randomUUID().toString();
        String shortPath = uuid + "/" + fileName;
        if (StringUtils.hasText(this.path)) {
            return path + "/" + shortPath;
        } else {
            return shortPath;
        }
    }
}
