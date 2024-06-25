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
    // 对于文件夹和文件来说，是通过计数来表示被删除多少次的，只要不等于0就是被删除
    NORMAL(0, "正常"),
    DEL(-1, "删除"),
    DISPLAY(-2, "隐藏"),
    ;

    private Integer status;
    private String desc;

}
