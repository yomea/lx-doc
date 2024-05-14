package com.laxqnsys.common.exception;

/**
 * @author wuzhenhong
 * @date 2024/5/14 10:05
 */
public class BusinessException extends RuntimeException {

    private Integer code;

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public BusinessException(Integer code, String msg, Throwable e) {
        super(msg, e);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
