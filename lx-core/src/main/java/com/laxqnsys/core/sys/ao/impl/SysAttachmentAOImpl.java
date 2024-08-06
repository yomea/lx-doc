package com.laxqnsys.core.sys.ao.impl;

import cn.hutool.core.io.IoUtil;
import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.properties.LxDocWebProperties;
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
    private LxDocWebProperties lxDocWebProperties;

    private Pattern imgBase64Pattern = Pattern.compile("^data:image/[a-zA-Z]+;base64,.*");

    private final String FIX_STATIC_PATH = "/static/";

    @Override
    public List<String> uploadFiles(MultipartFile[] file) {
        List<String> urlList = Arrays.stream(file).map(f -> {
            String fileUploadPath = lxDocWebProperties.getFileUploadPath();
            String uuid = UUID.randomUUID().toString();
            String randomDir = fileUploadPath + File.separator + uuid;
            File outFile = new File(randomDir);
            if (!outFile.exists() && !outFile.mkdirs()) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                    String.format("附件目录%s创建失败！", randomDir));
            }
            String shortPath = uuid + File.separator + f.getOriginalFilename();
            String path = fileUploadPath + File.separator + shortPath;
            try (InputStream inputStream = f.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(path)) {
                IoUtil.copy(inputStream, outputStream);
            } catch (IOException e) {
                throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "上传附件失败！", e);
            }
            return FIX_STATIC_PATH + shortPath;
        }).collect(Collectors.toList());
        return urlList;
    }

    @Override
    public String uploadImg(SysAttachmentVO sysAttachmentVO) {
        String encodedString = sysAttachmentVO.getImgData();
        if (!imgBase64Pattern.matcher(encodedString).matches()) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(),
                "上传的图片base64格式不正确，请传入形如：data:image/png;base64,iVBORw0KGgo...的图片格式");
        }
        String[] split = encodedString.split(",");
        String dataType = split[0];
        String suffix = dataType.split(";")[0].split("/")[1];
        String base64Image = split[1];
        byte[] data = Base64.getDecoder().decode(base64Image);
        String fileName = UUID.randomUUID() + "." + suffix;
        String fileUploadPath = lxDocWebProperties.getFileUploadPath();
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
