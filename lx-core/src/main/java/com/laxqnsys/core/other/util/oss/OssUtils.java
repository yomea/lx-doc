package com.laxqnsys.core.other.util.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.other.properties.OssFileUploadProperties;
import java.util.Objects;
import org.springframework.util.StringUtils;

/**
 * @author wuzhenhong
 * @date 2025/3/12 18:08
 */
public class OssUtils {

    public static final OSS createOssClient(OssFileUploadProperties oss) {
        if (Objects.isNull(oss)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "文件上传配置为 lx.doc.fileUpload.type=oss 时未配置 oss 属性");
        }

        String endpoint = oss.getEndpoint();
        if (!StringUtils.hasText(endpoint)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "文件上传配置为 lx.doc.fileUpload.type=oss 时 endpoint 必须配置");
        }

        String bucket = oss.getBucket();
        if (!StringUtils.hasText(bucket)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "文件上传配置为 lx.doc.fileUpload.type=oss 时 bucket 必须配置");
        }

        String accessKey = oss.getAccessKey();
        if (!StringUtils.hasText(accessKey)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "文件上传配置为 lx.doc.fileUpload.type=oss 时 accessKey 必须配置");
        }
        String secretKey = oss.getSecretKey();
        if (!StringUtils.hasText(secretKey)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "文件上传配置为 lx.doc.fileUpload.type=oss 时 secretKey 必须配置");
        }
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKey, secretKey);
        // 检查 bucket 是否存在
        boolean exists;
        try {
            exists = ossClient.doesBucketExist(bucket);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "检查名为【%s】的 bucket 是否存在时出错！", e);
        }
        if (!exists) {
            CreateBucketRequest createBucketRequest= new CreateBucketRequest(bucket);
            createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
            ossClient.createBucket(createBucketRequest);
        }
        return ossClient;
    }
}
