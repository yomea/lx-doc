CREATE TABLE IF NOT EXISTS lx_doc.`sys_user_info`
(
    `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_name` varchar(64)           DEFAULT NULL COMMENT '昵称，最多64个字符',
    `account`   varchar(32)  NOT NULL COMMENT '账户名，2-20个字符',
    `password`  varchar(256) NOT NULL COMMENT '密码，通过AES对称加密',
    `avatar`    varchar(1024)         DEFAULT NULL COMMENT '头像地址',
    `create_at` DATETIME     NOT NULL COMMENT '注册时间',
    `version`   int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
    `update_at` DATETIME     NOT NULL COMMENT '更新时间',
    `status`    int          NOT NULL DEFAULT '0' COMMENT '0：正常，-1：删除，1：禁用',
    PRIMARY KEY (`id`),
    UNIQUE `uk_account`(account)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='系统-用户信息';

CREATE TABLE IF NOT EXISTS lx_doc.`sys_attachment`
(
    `id`         bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name`       varchar(256)          DEFAULT NULL COMMENT '附件名称',
    `path`       varchar(512) NOT NULL COMMENT '附件存储路径',
    `remark`     varchar(512)          DEFAULT NULL COMMENT '附件备注',
    `size`       bigint(20) NOT NULL DEFAULT '0' COMMENT '附件文件大小 M',
    `version`    int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
    `creator_id` bigint(20) NOT NULL COMMENT '上传人ID',
    `create_at`  DATETIME     NOT NULL COMMENT '上传时间',
    `update_at`  DATETIME     NOT NULL COMMENT '更新时间',
    `status`     int          NOT NULL DEFAULT '0' COMMENT '0：正常，-1：删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='系统-附件';

CREATE TABLE IF NOT EXISTS lx_doc.`sys_user_config`
(
    `id`             bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `config_type`    varchar(32) NOT NULL COMMENT '配置类型',
    `config_content` varchar(1024)        DEFAULT NULL COMMENT '配置规则JSON',
    `version`        int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
    `user_id`        bigint(20) NOT NULL COMMENT '用户ID',
    `create_at`      DATETIME    NOT NULL COMMENT '创建时间',
    `update_at`      DATETIME    NOT NULL COMMENT '更新时间',
    `status`         int         NOT NULL DEFAULT '0' COMMENT '0：正常，-1：删除',
    PRIMARY KEY (`id`),
    unique `uk_user_id_config_type`(user_id, config_type)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='系统-用户配置';

CREATE TABLE IF NOT EXISTS lx_doc.`doc_file_folder`
(
    `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `parent_id`    bigint(20) unsigned NOT NULL COMMENT '父文件夹ID，顶级节点为0',
    `name`         varchar(128) NOT NULL COMMENT '文件名',
    `file_count`   int(11) NOT NULL DEFAULT '0' COMMENT '文件计数，本菜单下的直接文件数量',
    `folder_count` int(11) NOT NULL DEFAULT '0' COMMENT '菜单计数，本菜单下的直接菜单数量',
    `format`       int(11) NOT NULL COMMENT '文件格式，1：文件夹，2：文件',
    `file_type`    varchar(32) NOT NULL COMMENT '文件类型，excel，word，pdf，思维导图，白板',
    `collected`    tinyint(4) NOT NULL COMMENT '是否被收藏，0：否，1：是',
    `img`          varchar(1024) DEFAULT NULL COMMENT '封面图',
    `version`      int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
    `creator_id`   bigint(20) NOT NULL COMMENT '创建人ID',
    `create_at`    datetime    NOT NULL COMMENT '创建时间',
    `update_at`    datetime    NOT NULL COMMENT '更新时间',
    `status`       int(11) NOT NULL DEFAULT '0' COMMENT '0：正常，-1：删除',
    PRIMARY KEY (`id`),
    key            `idx_parent_creator_id`(parent_id, creator_id),
    key            `idx_creator_id`(creator_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='文档-文件夹';

CREATE TABLE IF NOT EXISTS lx_doc.`doc_file_content`
(
    `id`         bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `content`    longtext COMMENT '文件数据，excel，思维导图等文件内容',
    `version`    int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
    `creator_id` bigint(20) NOT NULL COMMENT '创建人ID',
    `create_at`  datetime NOT NULL COMMENT '创建时间',
    `update_at`  datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='文档-文件内容';

CREATE TABLE IF NOT EXISTS lx_doc.`doc_collect_folder`
(
    `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name`      varchar(128) NOT NULL COMMENT '文件名',
    `user_id`   bigint(20) NOT NULL COMMENT '收藏人ID',
    `create_at` DATETIME    NOT NULL COMMENT '收藏时间',
    `status`    int         NOT NULL DEFAULT '0' COMMENT '0：正常，-1：删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='文档-文件收藏夹（暂时定位个人云文档，未使用，后续协作，分享之后再启用）';

CREATE TABLE IF NOT EXISTS lx_doc.`doc_recycle`
(
    `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name`      varchar(128)        NOT NULL COMMENT '文件名',
    `user_id`   bigint(20)          NOT NULL COMMENT '回收人ID',
    `create_at` DATETIME            NOT NULL COMMENT '回收时间',
    PRIMARY KEY (`id`),
    key `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='文档-回收站';

CREATE TABLE IF NOT EXISTS lx_doc.`doc_relation_level`
(
    `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `parent_id` bigint(20) unsigned NOT NULL COMMENT '父id',
    `son_id`    bigint(20) unsigned NOT NULL COMMENT '子id',
    `user_id`   bigint(20)          NOT NULL COMMENT '回收人ID',
    PRIMARY KEY (`id`),
    key `idx_parent_id` (parent_id),
    key `idx_son_id` (son_id),
    key `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='文档-关联层级';