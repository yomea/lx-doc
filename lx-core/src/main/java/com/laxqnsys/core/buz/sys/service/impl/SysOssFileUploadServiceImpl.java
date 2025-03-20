package com.laxqnsys.core.buz.sys.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.buz.sys.model.bo.FileUploadBO;
import com.laxqnsys.core.other.constants.CommonCons;
import com.laxqnsys.core.other.properties.FileUploadProperties;
import com.laxqnsys.core.other.properties.LxDocWebProperties;
import com.laxqnsys.core.other.properties.OssFileUploadProperties;
import com.laxqnsys.core.buz.sys.service.ISysFileUploadService;
import com.laxqnsys.core.other.util.oss.OssUtils;
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
@ConditionalOnProperty(prefix = "lx.doc.fileUpload", name = "type", havingValue = "oss")
public class SysOssFileUploadServiceImpl implements ISysFileUploadService {

    private OSS ossClient;

    private String bucket;

    private String path;

    public SysOssFileUploadServiceImpl(LxDocWebProperties lxDocWebProperties) {
        FileUploadProperties fileUpload = lxDocWebProperties.getFileUpload();
        if (Objects.isNull(fileUpload)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "未配置文件上传属性！");
        }
        OssFileUploadProperties oss = fileUpload.getOss();
        this.ossClient = OssUtils.createOssClient(oss);
        this.bucket = oss.getBucket();
        String path = fileUpload.getPath();
        if(StringUtils.hasText(path)) {
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
            this.ossClient.deleteObject(this.bucket, fileName);
            return true;
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), String.format("删除url为%s的文件失败！", url), e);
        }
    }

    private FileUploadBO doUpload(Supplier<InputStream> supplier, String fileName, String contentType) {
        String filePath = this.getFilePath(fileName);
        try (InputStream inputStream = supplier.get()) {
            int size = inputStream.available();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            this.ossClient.putObject(this.bucket, filePath, inputStream, metadata);
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
        String uriPrefix = CommonCons.FS_URL_PREFIX;
        if (!url.startsWith(uriPrefix)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("url前缀不符合当前oss配置的url=》形入%s[文件路径]文件名", uriPrefix));
        }
        String fileName = url.substring(uriPrefix.length());
        if (!StringUtils.hasText(fileName)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("文件名为空，不符合当前minio配置的url=》形入%s[文件路径]文件名", uriPrefix));
        }
        return fileName;
    }

    private String getFileUrl(String fileName) {
        if (fileName.startsWith(CommonCons.FORWARD_SLANT)) {
            fileName = fileName.substring(1);
        }
        return CommonCons.FS_URL_PREFIX + fileName;
    }

    private String getFilePath(String fileName) {

        String uuid = UUID.randomUUID().toString();
        String shortPath = uuid + CommonCons.FORWARD_SLANT + fileName;
        if (StringUtils.hasText(this.path)) {
            return path + shortPath;
        } else {
            return shortPath;
        }
    }
}
