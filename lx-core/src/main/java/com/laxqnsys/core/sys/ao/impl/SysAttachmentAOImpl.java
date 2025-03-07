package com.laxqnsys.core.sys.ao.impl;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.sys.ao.SysAttachmentAO;
import com.laxqnsys.core.sys.model.bo.FileUploadBO;
import com.laxqnsys.core.sys.model.vo.SysAttachmentVO;
import com.laxqnsys.core.sys.service.ISysFileUploadService;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wuzhenhong
 * @date 2024/5/14 17:33
 */
@Service
public class SysAttachmentAOImpl implements SysAttachmentAO {

    @Autowired
    private ISysFileUploadService sysFileUploadService;

    private Pattern imgBase64Pattern = Pattern.compile("^data:image/([a-zA-Z]+);base64,");

    @Override
    public List<String> uploadFiles(MultipartFile[] file) {
        List<String> urlList = Arrays.stream(file).map(f -> {
            FileUploadBO fileUploadBO = sysFileUploadService.upload(f);
            return fileUploadBO.getUrl();
        }).collect(Collectors.toList());
        return urlList;
    }

    @Override
    public String uploadImg(SysAttachmentVO sysAttachmentVO) {
        String encodedString = sysAttachmentVO.getImgData();
        Matcher matcher = imgBase64Pattern.matcher(encodedString);
        if(!matcher.find()) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "上传的图片base64格式不正确，请传入形如：data:image/png;base64,iVBORw0KGgo...的图片格式");
        }
        String matchPrefix = matcher.group(0);
        // 获取后缀
        String suffix = matcher.group(1);
        String base64Image = encodedString.substring(matchPrefix.length());
        byte[] data = Base64.getDecoder().decode(base64Image);
        String fileName = UUID.randomUUID() + "." + suffix;
        FileUploadBO fileUploadBO = sysFileUploadService.upload(data, fileName);
        return fileUploadBO.getUrl();
    }
}
