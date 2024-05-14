package com.laxqnsys.core.sys.ao;

import com.laxqnsys.core.sys.model.vo.SysAttachmentVO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wuzhenhong
 * @date 2024/5/14 17:33
 */
public interface SysAttachmentAO {

    List<String> uploadFiles(MultipartFile[] file);

    String uploadImg(SysAttachmentVO sysAttachmentVO);
}
