package com.laxqnsys.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wuzhenhong
 * @date 2024/5/14 10:06
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {

    SUCCESS(0, "成功"),
    UN_LOGIN(401, "未登录"),
    NOT_FOUND(404, "资源未找到"),
    ERROR(500, "失败");

    public Integer code;
    private String desc;
}
