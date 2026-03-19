-- ============================================
-- Sim Studio 完整数据库建表语句
-- 数据库名: sim_studio
-- ============================================

-- 使用数据库
USE sim_studio;

-- ============================================
-- 1. 用户信息表 (users)
-- ============================================
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
    `student_id` VARCHAR(50) NOT NULL COMMENT '学号/工号，唯一标识',
    `name` VARCHAR(100) NOT NULL COMMENT '姓名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `role` VARCHAR(20) NOT NULL DEFAULT 'student' COMMENT '用户角色：student-学生, teacher-教师, admin-管理员',
    `subject` VARCHAR(100) DEFAULT NULL COMMENT '学科/专业',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `gender` TINYINT DEFAULT NULL COMMENT '性别：0-未知，1-男，2-女',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_id` (`student_id`),
    KEY `idx_role` (`role`),
    KEY `idx_subject` (`subject`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- ============================================
-- 2. 课程基本信息表 (courses)
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
-- 3. 课程组表 (course_groups)
-- 老师开设的课程实例（同一课程在不同学期由不同老师开设）
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
    CONSTRAINT `fk_course_groups_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_course_groups_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程组表（老师开设的课程）';

-- ============================================
-- 4. 班级表 (classes)
-- 班级信息表，关联老师、课程和学生
-- ============================================
CREATE TABLE IF NOT EXISTS `classes` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '班级ID，主键',
    `class_name` VARCHAR(200) NOT NULL COMMENT '班级名称',
    `class_code` VARCHAR(50) DEFAULT NULL COMMENT '班级代码',
    `teacher_id` BIGINT NOT NULL COMMENT '班主任/授课老师ID，外键关联users表',
    `course_id` BIGINT NOT NULL COMMENT '课程ID，外键关联courses表',
    `description` TEXT COMMENT '班级描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-停用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_class_code` (`class_code`),
    KEY `idx_teacher_id` (`teacher_id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_classes_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_classes_course` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- ============================================
-- 5. 学生选课关系表 (student_course_relations)
-- 记录学生选课信息（与课程组关联）
-- ============================================
CREATE TABLE IF NOT EXISTS `student_course_relations` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关系ID，主键',
    `student_id` BIGINT NOT NULL COMMENT '学生ID，外键关联users表',
    `group_id` BIGINT NOT NULL COMMENT '课程组ID，外键关联course_groups表',
    `enroll_status` VARCHAR(20) NOT NULL DEFAULT 'ENROLLING' COMMENT '选课状态：ENROLLING-申请中，ENROLLED-已选课，DROPPED-已退课，COMPLETED-已完成',
    `grade` DECIMAL(5,2) DEFAULT NULL COMMENT '成绩',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（选课时间）',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_group` (`student_id`, `group_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_group_id` (`group_id`),
    KEY `idx_enroll_status` (`enroll_status`),
    CONSTRAINT `fk_student_course_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_student_course_group` FOREIGN KEY (`group_id`) REFERENCES `course_groups` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生选课关系表';

-- ============================================
-- 6. 课程课件/文章表 (course_materials)
-- 用于课程详情页发布：文章/课件/文件/外链
-- ============================================
CREATE TABLE IF NOT EXISTS `course_materials` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `group_id` BIGINT NOT NULL COMMENT '课程组ID（course_groups.id）',
    `uploader_id` BIGINT DEFAULT NULL COMMENT '发布者（老师）ID，可选',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `type` VARCHAR(20) NOT NULL COMMENT '类型：ARTICLE/SLIDE/FILE/LINK',
    `content` LONGTEXT DEFAULT NULL COMMENT '内容（纯文本/Markdown/富文本）',
    `url` VARCHAR(1000) DEFAULT NULL COMMENT '外链或文件URL',
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
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_course_materials_group` FOREIGN KEY (`group_id`) REFERENCES `course_groups` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_course_materials_uploader` FOREIGN KEY (`uploader_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程课件/文章表';

-- ============================================
-- 表关系说明
-- ============================================
-- 1. users (用户表)
--    - 存储所有用户信息（学生、教师、管理员）
--
-- 2. courses (课程表)
--    - 存储课程基本信息
--
-- 3. course_groups (课程组表)
--    - 关联 courses 和 users (teacher)
--    - 表示某个老师在某个学期开设的某门课程
--
-- 4. classes (班级表)
--    - 关联 users (teacher) 和 courses
--    - 表示一个班级，有班主任/授课老师，对应一门课程
--
-- 5. student_course_relations (学生选课关系表)
--    - 关联 users (student) 和 course_groups
--    - 记录学生选课信息
--
-- 6. course_materials (课程材料表)
--    - 关联 course_groups 和 users (uploader)
--    - 存储课程相关的课件、文章、文件等

