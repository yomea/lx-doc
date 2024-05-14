package com.laxqnsys.common.model;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import lombok.Data;

/**
 * @author wuzhenhong
 * @date 2024/5/14 10:27
 */
@Data
public class ResponseResult<T> {

    private Integer code;

    private String message;

    private String traceId;

    private T data;

    public static <T> ResponseResult<T> ok() {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setCode(ErrorCodeEnum.SUCCESS.getCode());
        return responseResult;
    }

    public static <T> ResponseResult<T> ok(T data) {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setCode(ErrorCodeEnum.SUCCESS.getCode());
        responseResult.setData(data);
        return responseResult;
    }

    public static <T> ResponseResult fail() {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setCode(ErrorCodeEnum.ERROR.getCode());
        return responseResult;
    }

    public static <T> ResponseResult fail(String msg) {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setCode(ErrorCodeEnum.ERROR.getCode());
        responseResult.setMessage(msg);
        return responseResult;
    }

    public static <T> ResponseResult buildResp(Integer code, String msg) {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setCode(code);
        responseResult.setMessage(msg);
        return responseResult;
    }

    public static <T> ResponseResult buildResp(Integer code, String msg, T data) {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setCode(code);
        responseResult.setMessage(msg);
        responseResult.setData(data);
        return responseResult;
    }
}
