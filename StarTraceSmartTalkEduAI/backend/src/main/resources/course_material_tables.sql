-- 课程课件/文章（materials）相关表结构
-- 用于课程详情页发布：文章/课件/文件/外链
-- 说明：
-- - type: ARTICLE / SLIDE / FILE / LINK（与前端 CourseMaterialType 对齐）
-- - 本地上传：文件先保存到本地目录（如 ./uploads/course-material/），数据库保存 url 与文件元信息

CREATE TABLE IF NOT EXISTS `course_materials` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `group_id` BIGINT NOT NULL COMMENT '课程组ID（course_groups.id）',
    `uploader_id` BIGINT DEFAULT NULL COMMENT '发布者（老师）ID，可选',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `type` VARCHAR(20) NOT NULL COMMENT '类型：ARTICLE/SLIDE/FILE/LINK',
    `content` LONGTEXT DEFAULT NULL COMMENT '内容（纯文本/Markdown/富文本，按前端约定）',
    `url` VARCHAR(1000) DEFAULT NULL COMMENT '外链或文件URL（http(s)开头，按前端约定）',

    -- FILE 类型建议填充以下字段（便于后续做下载/校验/迁移对象存储）
    `original_filename` VARCHAR(255) DEFAULT NULL COMMENT '原始文件名（上传时）',
    `stored_filename` VARCHAR(255) DEFAULT NULL COMMENT '服务器保存的文件名（UUID等）',
    `file_path` VARCHAR(1000) DEFAULT NULL COMMENT '服务器本地绝对/相对路径',
    `content_type` VARCHAR(200) DEFAULT NULL COMMENT 'MIME类型',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小（字节）',

    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_group_id` (`group_id`),
    KEY `idx_uploader_id` (`uploader_id`),
    KEY `idx_type` (`type`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程课件/文章表';


