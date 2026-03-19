-- 课程相关表结构（无外键约束版本）

-- 1. 课程基本信息表
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

-- 2. 课程组表（老师开设的课程）
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
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程组表（老师开设的课程）';

-- 3. 学生选课关系表
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
    KEY `idx_enroll_status` (`enroll_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生选课关系表';

-- ============================================
-- 插入测试数据
-- ============================================

-- 插入课程数据
INSERT INTO `courses` (`course_code`, `name`, `description`, `credit`, `total_hours`, `created_at`, `updated_at`) VALUES
('CS101', '计算机科学导论', '介绍计算机科学的基本概念、编程基础、数据结构与算法等内容，为后续专业课程打下基础。', 3, 48, NOW(), NOW()),
('CS201', '数据结构与算法', '深入学习各种数据结构（数组、链表、栈、队列、树、图等）和常用算法（排序、搜索、动态规划等）。', 4, 64, NOW(), NOW()),
('CS301', '数据库系统', '学习关系型数据库的设计原理、SQL语言、数据库管理系统等内容。', 3, 48, NOW(), NOW()),
('CS202', '面向对象程序设计', '学习面向对象编程思想，掌握Java/C++等面向对象语言的特性与应用。', 3, 48, NOW(), NOW()),
('CS401', '软件工程', '学习软件开发流程、需求分析、系统设计、项目管理等内容。', 3, 48, NOW(), NOW()),
('CS302', '操作系统', '学习操作系统的基本原理、进程管理、内存管理、文件系统等内容。', 4, 64, NOW(), NOW()),
('MATH101', '高等数学', '学习微积分、极限、导数、积分等数学基础知识。', 4, 64, NOW(), NOW()),
('ENG101', '大学英语', '提高英语听说读写能力，掌握基础英语语法和词汇。', 2, 32, NOW(), NOW());

-- 插入课程组数据
-- 注意：teacher_id需要根据实际的users表中的教师ID进行调整
-- 假设teacher_id=1是一位教师
INSERT INTO `course_groups` (`course_id`, `teacher_id`, `semester`, `max_students`, `current_students`, `status`, `created_at`, `updated_at`) VALUES
(1, 1, '2024春季', 50, 32, 1, NOW(), NOW()),
(2, 1, '2024春季', 40, 28, 1, NOW(), NOW()),
(3, 1, '2024秋季', 45, 0, 1, NOW(), NOW()),
(4, 1, '2023秋季', 35, 30, 1, NOW(), NOW()),
(1, 1, '2024秋季', 50, 15, 1, NOW(), NOW()),
(5, 1, '2024春季', 40, 25, 1, NOW(), NOW()),
(6, 1, '2024秋季', 45, 20, 1, NOW(), NOW());

-- 插入学生选课关系数据
-- 注意：student_id需要根据实际的users表中的学生ID进行调整
-- 假设student_id=2,3,4,5,6等是学生
INSERT INTO `student_course_relations` (`student_id`, `group_id`, `enroll_status`, `grade`, `created_at`, `updated_at`) VALUES
-- 学生2选课记录
(2, 1, 'ENROLLED', NULL, NOW(), NOW()),
(2, 2, 'ENROLLED', NULL, NOW(), NOW()),
(2, 4, 'COMPLETED', 85.50, DATE_SUB(NOW(), INTERVAL 3 MONTH), NOW()),
-- 学生3选课记录
(3, 1, 'ENROLLED', NULL, NOW(), NOW()),
(3, 2, 'ENROLLED', NULL, NOW(), NOW()),
(3, 6, 'ENROLLED', NULL, NOW(), NOW()),
(3, 4, 'COMPLETED', 92.00, DATE_SUB(NOW(), INTERVAL 3 MONTH), NOW()),
-- 学生4选课记录
(4, 1, 'ENROLLED', NULL, NOW(), NOW()),
(4, 5, 'ENROLLED', NULL, NOW(), NOW()),
(4, 7, 'ENROLLED', NULL, NOW(), NOW()),
-- 学生5选课记录
(5, 2, 'ENROLLED', NULL, NOW(), NOW()),
(5, 6, 'ENROLLED', NULL, NOW(), NOW()),
(5, 4, 'DROPPED', NULL, DATE_SUB(NOW(), INTERVAL 2 MONTH), NOW()),
-- 学生6选课记录
(6, 1, 'ENROLLED', NULL, NOW(), NOW()),
(6, 3, 'ENROLLED', NULL, NOW(), NOW()),
(6, 7, 'ENROLLED', NULL, NOW(), NOW()),
-- 学生7选课记录
(7, 2, 'ENROLLED', NULL, NOW(), NOW()),
(7, 5, 'ENROLLED', NULL, NOW(), NOW()),
(7, 4, 'COMPLETED', 78.50, DATE_SUB(NOW(), INTERVAL 3 MONTH), NOW()),
-- 学生8选课记录
(8, 1, 'ENROLLED', NULL, NOW(), NOW()),
(8, 6, 'ENROLLED', NULL, NOW(), NOW()),
-- 学生9选课记录
(9, 2, 'ENROLLED', NULL, NOW(), NOW()),
(9, 7, 'ENROLLED', NULL, NOW(), NOW()),
-- 学生10选课记录
(10, 1, 'ENROLLED', NULL, NOW(), NOW()),
(10, 3, 'ENROLLED', NULL, NOW(), NOW()),
(10, 5, 'ENROLLED', NULL, NOW(), NOW());

-- 更新课程组的当前学生数（根据实际选课记录）
UPDATE `course_groups` SET `current_students` = (
    SELECT COUNT(*) 
    FROM `student_course_relations` 
    WHERE `group_id` = `course_groups`.`id` 
    AND `enroll_status` = 'ENROLLED'
) WHERE `id` IN (1, 2, 3, 4, 5, 6, 7);
