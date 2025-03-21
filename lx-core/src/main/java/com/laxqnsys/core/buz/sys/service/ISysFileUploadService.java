package com.laxqnsys.core.buz.sys.service;

import com.laxqnsys.core.buz.sys.model.bo.FileUploadBO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wuzhenhong
 * @date 2025/3/6 20:01
 */
public interface ISysFileUploadService {

    FileUploadBO upload(MultipartFile file);

    FileUploadBO upload(byte[] data, String fileName);

    boolean delete(String url);
}
