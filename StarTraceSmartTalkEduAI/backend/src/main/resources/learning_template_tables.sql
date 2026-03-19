-- ============================================
-- 学习模板表 (learning_templates)
-- 用于保存老师上传的JSON学习模板
-- ============================================
CREATE TABLE IF NOT EXISTS `learning_templates` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '模板ID，主键',
    `template_name` VARCHAR(200) NOT NULL COMMENT '模板名称',
    `description` TEXT COMMENT '模板描述',
    `image_url` VARCHAR(500) COMMENT '模板图片URL',
    `json_content` JSON NOT NULL COMMENT 'JSON模板内容',
    `teacher_id` BIGINT NOT NULL COMMENT '老师ID（学号），外键关联users表',
    `course_id` BIGINT NOT NULL COMMENT '课程ID，外键关联courses表',
    `class_id` BIGINT COMMENT '班级ID，外键关联classes表（可选，如果模板关联到具体班级）',
    `download_count` INT NOT NULL DEFAULT 0 COMMENT '学生下载量',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-停用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_learning_templates_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_learning_templates_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_learning_templates_class` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习模板表';

-- ============================================
-- 模板下载记录表 (template_downloads)
-- 用于记录学生下载模板的记录（可选，用于统计和去重）
-- ============================================
CREATE TABLE IF NOT EXISTS `template_downloads` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '下载记录ID，主键',
    `template_id` BIGINT NOT NULL COMMENT '模板ID，外键关联learning_templates表',
    `student_id` BIGINT NOT NULL COMMENT '学生ID，外键关联users表',
    `download_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下载时间',
    PRIMARY KEY (`id`),
    KEY `idx_template_id` (`template_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_download_time` (`download_time`),
    UNIQUE KEY `uk_template_student` (`template_id`, `student_id`) COMMENT '同一学生同一模板只记录一次下载',
    CONSTRAINT `fk_template_downloads_template` FOREIGN KEY (`template_id`) REFERENCES `learning_templates` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_template_downloads_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模板下载记录表';

