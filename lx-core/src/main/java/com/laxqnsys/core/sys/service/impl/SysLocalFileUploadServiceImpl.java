package com.laxqnsys.core.sys.service.impl;

import cn.hutool.core.io.IoUtil;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.properties.FileUploadProperties;
import com.laxqnsys.core.properties.LxDocWebProperties;
import com.laxqnsys.core.sys.model.bo.FileUploadBO;
import com.laxqnsys.core.sys.service.ISysFileUploadService;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wuzhenhong
 * @date 2025/3/6 20:16
 */
@Service
@ConditionalOnProperty(prefix = "lx.doc.fileUpload", name = "type", havingValue = "local", matchIfMissing = true)
public class SysLocalFileUploadServiceImpl implements ISysFileUploadService {

    private static final String FIX_STATIC_PATH = "/static/";

    private String fileUploadPath;

    public SysLocalFileUploadServiceImpl(LxDocWebProperties lxDocWebProperties) {
        FileUploadProperties localUpload = lxDocWebProperties.getFileUpload();
        if (Objects.isNull(localUpload)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "选择本地存储文件时，上传附件的路径必须配置！");
        }
        String fileUploadPath = localUpload.getPath();
        if (!StringUtils.hasText(fileUploadPath)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "选择本地存储文件时，上传附件的路径必须配置！");
        }
        if (fileUploadPath.endsWith("/") || fileUploadPath.endsWith("\\")) {
            fileUploadPath += File.separator;
        }
        this.fileUploadPath = fileUploadPath;
    }


    @Override
    public FileUploadBO upload(MultipartFile file) {
        return this.doUpload(() -> {
            try {
                return file.getInputStream();
            } catch (IOException e) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "上传附件失败！", e);
            }
        }, file.getOriginalFilename(), file.getSize());
    }

    @Override
    public FileUploadBO upload(byte[] data, String fileName) {
        return this.doUpload(() -> new ByteArrayInputStream(data), fileName, Long.valueOf(data.length));
    }

    @Override
    public boolean delete(String url) {
        if(!StringUtils.hasText(url)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "url不能为空！");
        }
        String shortPath;
        if(url.startsWith(FIX_STATIC_PATH)) {
            shortPath = url.substring(FIX_STATIC_PATH.length());
        } else if(url.startsWith(FIX_STATIC_PATH.substring(1))){
            shortPath = url.substring(FIX_STATIC_PATH.length() - 1);
        } else {
            shortPath = url;
        }
        if(!StringUtils.hasText(shortPath)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "url不合法！");
        }
        String path = fileUploadPath + File.separator + shortPath;
        File file = new File(path);
        return file.exists() ? file.delete() : true;
    }

    private FileUploadBO doUpload(Supplier<InputStream> streamSupplier, String fileName, Long size) {

        String uuid = UUID.randomUUID().toString();
        String randomDir = fileUploadPath + File.separator + uuid;
        File outFile = new File(randomDir);
        if (!outFile.exists() && !outFile.mkdirs()) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("附件目录%s创建失败！", randomDir));
        }
        String shortPath = uuid + File.separator + fileName;
        String path = fileUploadPath + File.separator + shortPath;
        try (InputStream inputStream = streamSupplier.get();
            FileOutputStream outputStream = new FileOutputStream(path)) {
            IoUtil.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "上传附件失败！", e);
        }
        return FileUploadBO.builder()
            .url(FIX_STATIC_PATH + shortPath)
            .size(size)
            .build();
    }
}
