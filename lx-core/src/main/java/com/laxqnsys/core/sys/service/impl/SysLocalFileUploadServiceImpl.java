package com.laxqnsys.core.sys.service.impl;

import cn.hutool.core.io.IoUtil;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.properties.LocalFileUploadProperties;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wuzhenhong
 * @date 2025/3/6 20:16
 */
@Service
@ConditionalOnProperty(prefix = "lx.doc", name = "fileUploadType", havingValue = "local", matchIfMissing = true)
public class SysLocalFileUploadServiceImpl implements ISysFileUploadService {

    private static final String FIX_STATIC_PATH = "/static/";

    private String fileUploadPath;

    public SysLocalFileUploadServiceImpl(LxDocWebProperties lxDocWebProperties) {
        LocalFileUploadProperties localUpload = lxDocWebProperties.getLocalUpload();
        if(Objects.isNull(localUpload)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "选择本地存储文件时，上传附件的路径必须配置！");
        }
        String fileUploadPath = localUpload.getFilePath();
        if(!StringUtils.hasText(fileUploadPath)) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "选择本地存储文件时，上传附件的路径必须配置！");
        }
        if(fileUploadPath.endsWith("/") || fileUploadPath.endsWith("\\")) {
            fileUploadPath += File.separator;
        }
        this.fileUploadPath = fileUploadPath;
    }


    @Override
    public FileUploadBO upload(MultipartFile file) {

        String uuid = UUID.randomUUID().toString();
        String randomDir = fileUploadPath + File.separator + uuid;
        File outFile = new File(randomDir);
        if (!outFile.exists() && !outFile.mkdirs()) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                String.format("附件目录%s创建失败！", randomDir));
        }
        String shortPath = uuid + File.separator + file.getOriginalFilename();
        String path = fileUploadPath + File.separator + shortPath;
        try (InputStream inputStream = file.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(path)) {
            IoUtil.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "上传附件失败！", e);
        }
        return FileUploadBO.builder()
            .url(FIX_STATIC_PATH + shortPath)
            .size(file.getSize())
            .build();
    }

    @Override
    public FileUploadBO upload(byte[] data, String fileName) {
        String shortPath = UUID.randomUUID() + File.separator + fileName;
        String path = fileUploadPath + File.separator + shortPath;
        try (InputStream inputStream = new ByteArrayInputStream(data);
            FileOutputStream outputStream = new FileOutputStream(path)) {
            IoUtil.copy(inputStream, outputStream);
            return FileUploadBO.builder()
                .url(FIX_STATIC_PATH + shortPath)
                .size(Long.valueOf(data.length))
                .build();
        } catch (IOException e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "上传图片失败！", e);
        }
    }
}
