package com.laxqnsys.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wuzhenhong
 * @date 2025/2/20 9:53
 */
@Getter
@AllArgsConstructor
public enum FileStorageTypeEnum {

    MYSQL(1, "数据库存储"),
    LOCAL(2, "本地磁盘存储"),
    OSS_FILE_SYS(3, "OSS分布式文件系统存储"),
    MINIO_FILE_SYS(4, "OSS分布式文件系统存储");

    private Integer type;
    private String desc;
}
