package com.laxqnsys.core.sys.controller;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.core.constants.CommonCons;
import com.laxqnsys.core.properties.FileUploadProperties;
import com.laxqnsys.core.properties.LxDocWebProperties;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

/**
 * @author wuzhenhong
 * @date 2024/5/24 16:06
 */
@Controller
public class IndexController {

    @Value("${server.port}")
    private Integer port;

    @Autowired
    private LxDocWebProperties lxDocWebProperties;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(value = {"/", "/login", "/workspace", "/workspace/**"})
    public String index() {
        String indexHtmlWebPath = lxDocWebProperties.getIndexHtmlWebPath();
        return "forward:" + (StringUtils.hasText(indexHtmlWebPath)
            ? indexHtmlWebPath
            : "/system/error");
    }

    @GetMapping(value = {"/{docType}/{id:\\d+}"})
    public String docType(@PathVariable("docType") String docType) {

        return "forward:/" + docType + "/index.html";
    }

    @GetMapping(value = {"/fs/**"})
    public void fs(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        uri = uri.substring(CommonCons.FS_URL_PREFIX.length());
        FileUploadProperties fileUpload = lxDocWebProperties.getFileUpload();
        String forwardUrl;
        if(Objects.isNull(fileUpload)) {
            forwardUrl = "system/error";
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            String type = fileUpload.getType();
            String endpoint;
            if("local".equals(type)) {
                endpoint = "http://localhost:" + port;
            } else if("minio".equals(type)) {
                endpoint = fileUpload.getMinio().getEndpoint();
            } else {
                endpoint = fileUpload.getOss().getEndpoint();
            }
            if(!endpoint.endsWith(CommonCons.FORWARD_SLANT)) {
                endpoint = endpoint + CommonCons.FORWARD_SLANT;
            }
            forwardUrl = endpoint + uri;
        }
        ResponseEntity<byte[]> imageResponse = restTemplate.exchange(
            forwardUrl,
            HttpMethod.GET,
            null,
            byte[].class
        );

        if (imageResponse.getStatusCode() != HttpStatus.OK || imageResponse.getBody() == null) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文件获取失败！");
        }

        String contentType = imageResponse.getHeaders().getContentType().toString();
        response.setContentType(contentType);  // 根据实际类型调整
        response.setContentLength(imageResponse.getBody().length);

        try (InputStream inputStream = new ByteArrayInputStream(imageResponse.getBody());
            OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "文件获取失败！");
        }
//        return "redirect:" + forwardUrl;
    }
}
