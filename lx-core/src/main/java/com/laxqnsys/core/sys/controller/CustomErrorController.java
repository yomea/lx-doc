package com.laxqnsys.core.sys.controller;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.model.ResponseResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    @Autowired
    private ServerProperties serverProperties;

    @RequestMapping("/system/error")
    @ResponseBody
    public ResponseResult handleError(HttpServletRequest request, HttpServletResponse response) {
        Integer code = response.getStatus();
        if (HttpStatus.NOT_FOUND.value() == code) {
            return ResponseResult.fail(ErrorCodeEnum.NOT_FOUND.getCode(), "http状态码为404,资源未找到");
        }
        return ResponseResult.fail(String.format("http状态码为%s,系统错误", code));
    }

    @Override
    public String getErrorPath() {
        return serverProperties.getError().getPath();
    }
}