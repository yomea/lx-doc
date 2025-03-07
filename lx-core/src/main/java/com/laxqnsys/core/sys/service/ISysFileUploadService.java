package com.laxqnsys.core.sys.service;

import com.laxqnsys.core.sys.model.bo.FileUploadBO;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wuzhenhong
 * @date 2025/3/6 20:01
 */
public interface ISysFileUploadService {

    FileUploadBO upload(MultipartFile file);

    FileUploadBO upload(byte[] data, String fileName);

}
