package com.laxqnsys.core.other.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wuzhenhong
 * @date 2024/5/14 15:09
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnum {

    //0：正常，-1：删除，1：禁用
    NORMAL(0, "正常"),
    DELETE(-1, "删除"),
    DISABLED(1, "禁用"),
    ;

    private Integer status;

    private String desc;
}
