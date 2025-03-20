package com.laxqnsys.core.other.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wuzhenhong
 * @date 2024/5/14 20:03
 */
@Getter
@AllArgsConstructor
public enum FileFolderFormatEnum {

    FOLDER(1, "文件夹"),
    FILE(2, "文件"),

    ;

    private Integer format;

    private String desc;


}
