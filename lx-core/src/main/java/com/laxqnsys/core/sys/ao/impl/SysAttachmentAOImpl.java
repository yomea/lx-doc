package com.laxqnsys.core.sys.ao.impl;

import cn.hutool.core.io.IoUtil;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.sys.ao.SysAttachmentAO;
import com.laxqnsys.core.sys.model.vo.SysAttachmentVO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wuzhenhong
 * @date 2024/5/14 17:33
 */
@Service
public class SysAttachmentAOImpl implements SysAttachmentAO {

    @Value("${file.upload.path}")
    private String fileUploadPath;

    private Pattern imgBase64Pattern = Pattern.compile("^data:image/[a-zA-Z]+;base64,.*");

    @Override
    public List<String> uploadFiles(MultipartFile[] file) {
        List<String> urlList = Arrays.stream(file).map(f -> {
            String path = fileUploadPath + File.separator + f.getOriginalFilename();
            try (InputStream inputStream = f.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(path)) {
                IoUtil.copy(inputStream, outputStream);
            } catch (IOException e) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "上传附件失败！", e);
            }
            return "/static/" + f.getOriginalFilename();
        }).collect(Collectors.toList());
        return urlList;
    }

    @Override
    public String uploadImg(SysAttachmentVO sysAttachmentVO) {
        String encodedString = sysAttachmentVO.getImgData();
        if(!imgBase64Pattern.matcher(encodedString).matches()) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "上传的图片base64格式不正确，请传入形如：data:image/png;base64,iVBORw0KGgo...的图片格式");
        }
        String[] split = encodedString.split(",");
        String dataType = split[0];
        String suffix = dataType.split(";")[0].split("/")[1];
        String base64Image = split[1];
        byte[] data = Base64.getDecoder().decode(base64Image);
        String fileName = UUID.randomUUID() + "." + suffix;
        String path = fileUploadPath + File.separator + fileName;
        try (InputStream inputStream = new ByteArrayInputStream(data);
            FileOutputStream outputStream = new FileOutputStream(path)) {
            IoUtil.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "上传图片失败！", e);
        }
        return "/static/" + fileName;
    }
}
