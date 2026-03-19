-- 课程相关表结构（简化版 - 直接执行）
-- 此版本先创建表，然后单独添加外键约束，避免类型不匹配问题

-- ============================================
-- 1. 创建课程基本信息表
-- ============================================
CREATE TABLE IF NOT EXISTS `courses` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '课程ID，主键',
    `course_code` VARCHAR(50) NOT NULL COMMENT '课程代码，如CS101',
    `name` VARCHAR(200) NOT NULL COMMENT '课程名称',
    `description` TEXT COMMENT '课程描述',
    `credit` INT NOT NULL DEFAULT 0 COMMENT '学分',
    `total_hours` INT NOT NULL DEFAULT 0 COMMENT '总学时',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_code` (`course_code`),
    KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程基本信息表';

-- ============================================
-- 2. 创建课程组表（先不添加users表的外键约束）
-- ============================================
CREATE TABLE IF NOT EXISTS `course_groups` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '课程组ID，主键',
    `course_id` BIGINT NOT NULL COMMENT '课程ID，外键关联courses表',
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID，外键关联users表',
    `semester` VARCHAR(50) NOT NULL COMMENT '学期，如：2024春季',
    `max_students` INT NOT NULL DEFAULT 50 COMMENT '最大学生数',
    `current_students` INT NOT NULL DEFAULT 0 COMMENT '当前学生数',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-停开',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_semester` (`semester`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_course_groups_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程组表（老师开设的课程）';

-- ============================================
-- 3. 创建学生选课关系表（先不添加users表的外键约束）
-- ============================================
CREATE TABLE IF NOT EXISTS `student_course_relations` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关系ID，主键',
    `student_id` BIGINT NOT NULL COMMENT '学生ID，外键关联users表',
    `group_id` BIGINT NOT NULL COMMENT '课程组ID，外键关联course_groups表',
    `enroll_status` VARCHAR(20) NOT NULL DEFAULT 'ENROLLED' COMMENT '选课状态：ENROLLED-已选课，DROPPED-已退课，COMPLETED-已完成',
    `grade` DECIMAL(5,2) DEFAULT NULL COMMENT '成绩',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（选课时间）',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_group` (`student_id`, `group_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_group_id` (`group_id`),
    KEY `idx_enroll_status` (`enroll_status`),
    CONSTRAINT `fk_student_course_group` FOREIGN KEY (`group_id`) REFERENCES `course_groups` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生选课关系表';

-- ============================================
-- 4. 添加users表的外键约束
-- 如果users表的id是BIGINT UNSIGNED，请先执行类型转换
-- ============================================

-- 检查users表id字段类型的SQL（执行后查看结果）：
-- SHOW COLUMNS FROM users WHERE Field = 'id';

-- 如果users表的id是BIGINT（SIGNED），直接执行以下SQL：
ALTER TABLE `course_groups` 
ADD CONSTRAINT `fk_course_groups_teacher` 
FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

ALTER TABLE `student_course_relations` 
ADD CONSTRAINT `fk_student_course_student` 
FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

-- 如果users表的id是BIGINT UNSIGNED，请先执行以下SQL修改字段类型，然后再执行上面的外键约束：
-- ALTER TABLE `course_groups` MODIFY `teacher_id` BIGINT UNSIGNED NOT NULL COMMENT '教师ID，外键关联users表';
-- ALTER TABLE `student_course_relations` MODIFY `student_id` BIGINT UNSIGNED NOT NULL COMMENT '学生ID，外键关联users表';

