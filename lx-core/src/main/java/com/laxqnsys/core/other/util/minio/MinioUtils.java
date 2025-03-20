package com.laxqnsys.core.other.util.minio;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.other.properties.MinioFileUploadProperties;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import java.util.Objects;
import org.springframework.util.StringUtils;

/**
 * @author wuzhenhong
 * @date 2025/3/12 18:08
 */
public class MinioUtils {

    public static final MinioClient createMinioClient(MinioFileUploadProperties minio) {
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
        MinioClient minioClient = MinioClient.builder().endpoint(endpoint)
            .credentials(minio.getAccessKey(), minio.getSecretKey()).build();
        // 检查 bucket 是否存在
        boolean exists;
        try {
            exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "检查名为【%s】的 bucket 是否存在时出错！", e);
        }
        if (!exists) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "名为【%s】的 bucket 不存在，请自行创建并赋予可读权限！");
        }
        return minioClient;
    }
}
