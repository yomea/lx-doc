package com.laxqnsys.core.exception;

import com.laxqnsys.common.exception.BusinessException;
import com.laxqnsys.common.model.ResponseResult;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        String trackId = UUID.randomUUID().toString().replace("-", "");
        log.error("defaultErrorHandler->全局拦截异常信息,trackId={}, url={}", trackId, request.getRequestURI(), ex);
        ResponseResult<?> result = this.getMessage(ex);
        result.setTraceId(trackId);
        try {
            response.setStatus(200);
            MappingJackson2HttpMessageConverter mjhmc = new MappingJackson2HttpMessageConverter();
            ServletServerHttpResponse sshr = new ServletServerHttpResponse(
                response);
            mjhmc.write(result, MediaType.APPLICATION_JSON_UTF8, sshr);
        } catch (IOException e) {
            log.error("返回异常", e.getMessage());
        }
        return new ModelAndView();
    }

    private ResponseResult<?> getMessage(Throwable th) {
        ResponseResult<?> responseResult;
        if (th instanceof BusinessException) {
            BusinessException buzzErrorException = (BusinessException) th;
            String exceptionMsg = buzzErrorException.getMessage();
            boolean hasMsg =
                !StringUtils.hasText(exceptionMsg) || exceptionMsg.equals("null") || exceptionMsg.length() > 128;
            responseResult = ResponseResult.buildResp(buzzErrorException.getCode(),
                hasMsg ? "系统异常,请联系管理员!" : exceptionMsg);
        } else if (th instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException notValidException = (MethodArgumentNotValidException) th;
            BindingResult bindingResult = notValidException.getBindingResult();
            String message = this.processValid(bindingResult);
            responseResult = ResponseResult.fail(message);
        } else if (th instanceof HttpRequestMethodNotSupportedException) {
            HttpRequestMethodNotSupportedException notSupportedException = (HttpRequestMethodNotSupportedException) th;
            responseResult = ResponseResult.fail(String.format("请求方法%s不支持", notSupportedException.getMethod()));
        } else if (th.getCause() != null) {
            responseResult = this.getMessage(th.getCause());
        } else {
            responseResult = ResponseResult.fail("系统异常,请联系管理员!");
        }
        return responseResult;
    }

    protected String processValid(BindingResult result) {
        if (result != null && result.getErrorCount() > 0) {
            String mess = "";
            for (ObjectError oe : result.getAllErrors()) {
                if (oe instanceof FieldError) {
                    mess = mess + ((FieldError) oe).getField() + ":" + oe.getDefaultMessage() + ";";
                } else {
                    mess = mess + oe.getDefaultMessage();
                }
            }
            if (mess.endsWith(";")) {
                mess = mess.substring(0, mess.length() - 1);
                return mess;
            }
        }
        return "";
    }
}

