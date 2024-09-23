alter table lx_doc.`doc_recycle`
    add key `idx_user_id` (`user_id`),
    drop column `ids`
;

alter table lx_doc.`doc_file_folder`
    add column `shared` tinyint(4) NOT NULL COMMENT '我是否分享了该文件，0：否，1：是' after collected
;

CREATE TABLE IF NOT EXISTS lx_doc.`doc_share`
(
    `id`            bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_id`       bigint(20)          NOT NULL COMMENT '分享人ID',
    `view_count`    int(11)             NOT NULL DEFAULT '0' COMMENT '查看次数',
    `like_count`    int(11)             NOT NULL DEFAULT '0' COMMENT '点赞次数',
    `comment_count` int(11)             NOT NULL DEFAULT '0' COMMENT '评论数',
    `create_at`     DATETIME            NOT NULL COMMENT '分享时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='文档-分享';

CREATE TABLE IF NOT EXISTS lx_doc.`doc_comment`
(
    `id`            bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `parent_id`     bigint(20)          NOT NULL COMMENT '父评论ID',
    `file_id`       bigint(20)          NOT NULL COMMENT '评论的文件ID',
    `user_id`       bigint(20)          NOT NULL COMMENT '评论人ID',
    `create_at`     DATETIME            NOT NULL COMMENT '分享时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='文档-评论';

CREATE TABLE IF NOT EXISTS lx_doc.`doc_relation_level`
(
    `id`        bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `parent_id` bigint(20) unsigned NOT NULL COMMENT '父id',
    `son_id`    bigint(20) unsigned NOT NULL COMMENT '子id',
    `buz_type`  int(11) unsigned    NOT NULL COMMENT '业务类型，1：回收站，2：分享',
    PRIMARY KEY (`id`),
    key `idx_parent_id` (parent_id),
    key `idx_son_id` (son_id)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT ='文档-关联层级';