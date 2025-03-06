package com.laxqnsys.core.sys.service.impl;

import com.laxqnsys.core.sys.model.bo.FileUploadBO;
import com.laxqnsys.core.sys.service.ISysFileUploadService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wuzhenhong
 * @date 2025/3/6 20:16
 */
@Service
@ConditionalOnProperty(prefix = "lx.doc", name = "fileUploadType", havingValue = "minio")
public class SysMinioFileUploadServiceImpl implements ISysFileUploadService {

    public SysMinioFileUploadServiceImpl() {

    }


    @Override
    public FileUploadBO upload(MultipartFile file) {

        return null;
    }

    @Override
    public FileUploadBO upload(byte[] data, String fileName) {
        return null;
    }
}
