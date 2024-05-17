package com.laxqnsys.core.sys.controller;


import com.laxqnsys.common.model.ResponseResult;
import com.laxqnsys.core.sys.ao.SysAttachmentAO;
import com.laxqnsys.core.sys.model.vo.SysAttachmentVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 系统-附件 前端控制器
 * </p>
 *
 * @author author
 * @since 2024-05-13
 */
@RestController
@RequestMapping("")
public class SysAttachmentController {

    @Autowired
    private SysAttachmentAO sysAttachmentAO;

    @PostMapping(value = "/uploadFiles", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseResult<List<String>> uploadFiles(MultipartFile[] file) {
        List<String> urlList = sysAttachmentAO.uploadFiles(file);
        return ResponseResult.ok(urlList);
    }

    @PostMapping(value = "/uploadImg")
    public ResponseResult<String> uploadImg(@RequestBody SysAttachmentVO sysAttachmentVO) {
        String uri = sysAttachmentAO.uploadImg(sysAttachmentVO);
        return ResponseResult.ok(uri);
    }
}
