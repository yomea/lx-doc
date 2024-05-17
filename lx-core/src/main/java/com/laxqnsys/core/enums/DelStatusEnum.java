package com.laxqnsys.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wuzhenhong
 * @date 2024/5/14 16:39
 */
@Getter
@AllArgsConstructor
public enum DelStatusEnum {

    NORMAL(0, "正常"),
    DEL(-1, "删除"),
    DISPLAY(-2, "隐藏"),
    ;

    private Integer status;
    private String desc;

}
