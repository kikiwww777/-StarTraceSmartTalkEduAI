-- ============================================
-- Sim Studio 测试数据
-- 每个表至少10条数据
-- 注意：请先执行 sim_studio_complete_tables.sql 创建表结构
-- ============================================

USE sim_studio;

-- ============================================
-- 1. 用户信息表 (users) - 插入15条数据
-- ============================================
-- 密码统一使用: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy (对应明文: password123)
INSERT INTO `users` (`student_id`, `name`, `password`, `role`, `subject`, `email`, `phone`, `gender`, `avatar`, `status`, `created_at`, `updated_at`) VALUES
-- 管理员
('ADMIN001', '系统管理员', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin', NULL, 'admin@simstudio.edu.cn', '13800000001', 1, NULL, 1, NOW(), NOW()),

-- 教师 (ID: 2-6)
('T001', '张教授', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'teacher', '计算机科学', 'zhang@simstudio.edu.cn', '13800000002', 1, NULL, 1, NOW(), NOW()),
('T002', '李老师', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'teacher', '软件工程', 'li@simstudio.edu.cn', '13800000003', 2, NULL, 1, NOW(), NOW()),
('T003', '王教授', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'teacher', '数据科学', 'wang@simstudio.edu.cn', '13800000004', 1, NULL, 1, NOW(), NOW()),
('T004', '刘老师', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'teacher', '人工智能', 'liu@simstudio.edu.cn', '13800000005', 2, NULL, 1, NOW(), NOW()),
('T005', '陈教授', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'teacher', '网络安全', 'chen@simstudio.edu.cn', '13800000006', 1, NULL, 1, NOW(), NOW()),

-- 学生 (ID: 7-16)
('S2021001', '张三', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student', '计算机科学', 'zhangsan@student.edu.cn', '13800000007', 1, NULL, 1, NOW(), NOW()),
('S2021002', '李四', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student', '计算机科学', 'lisi@student.edu.cn', '13800000008', 2, NULL, 1, NOW(), NOW()),
('S2021003', '王五', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student', '软件工程', 'wangwu@student.edu.cn', '13800000009', 1, NULL, 1, NOW(), NOW()),
('S2021004', '赵六', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student', '软件工程', 'zhaoliu@student.edu.cn', '13800000010', 2, NULL, 1, NOW(), NOW()),
('S2021005', '钱七', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student', '数据科学', 'qianqi@student.edu.cn', '13800000011', 1, NULL, 1, NOW(), NOW()),
('S2021006', '孙八', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student', '数据科学', 'sunba@student.edu.cn', '13800000012', 2, NULL, 1, NOW(), NOW()),
('S2021007', '周九', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student', '人工智能', 'zhoujiu@student.edu.cn', '13800000013', 1, NULL, 1, NOW(), NOW()),
('S2021008', '吴十', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student', '人工智能', 'wushi@student.edu.cn', '13800000014', 2, NULL, 1, NOW(), NOW()),
('S2021009', '郑十一', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student', '网络安全', 'zhengshiyi@student.edu.cn', '13800000015', 1, NULL, 1, NOW(), NOW()),
('S2021010', '王十二', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'student', '网络安全', 'wangshier@student.edu.cn', '13800000016', 2, NULL, 1, NOW(), NOW());

-- ============================================
-- 2. 课程基本信息表 (courses) - 插入12条数据
-- ============================================
INSERT INTO `courses` (`course_code`, `name`, `description`, `credit`, `total_hours`, `created_at`, `updated_at`) VALUES
('CS101', '计算机科学导论', '介绍计算机科学的基本概念、编程基础、数据结构与算法等内容，为后续专业课程打下基础。', 3, 48, NOW(), NOW()),
('CS201', '数据结构与算法', '深入学习各种数据结构（数组、链表、栈、队列、树、图等）和常用算法（排序、搜索、动态规划等）。', 4, 64, NOW(), NOW()),
('CS301', '数据库系统', '学习关系型数据库的设计原理、SQL语言、数据库管理系统等内容。', 3, 48, NOW(), NOW()),
('CS202', '面向对象程序设计', '学习面向对象编程思想，掌握Java/C++等面向对象语言的特性与应用。', 3, 48, NOW(), NOW()),
('CS401', '软件工程', '学习软件开发流程、需求分析、系统设计、项目管理等内容。', 3, 48, NOW(), NOW()),
('CS302', '操作系统', '学习操作系统的基本原理、进程管理、内存管理、文件系统等内容。', 4, 64, NOW(), NOW()),
('CS402', '计算机网络', '学习网络协议、TCP/IP、HTTP、网络安全等网络相关知识。', 3, 48, NOW(), NOW()),
('CS501', '人工智能基础', '介绍人工智能的基本概念、机器学习、深度学习等前沿技术。', 4, 64, NOW(), NOW()),
('CS502', '大数据技术', '学习大数据处理技术、Hadoop、Spark等大数据框架。', 3, 48, NOW(), NOW()),
('CS601', '网络安全', '学习网络安全原理、加密技术、安全协议、漏洞分析等内容。', 3, 48, NOW(), NOW()),
('CS303', '编译原理', '学习编译器的设计与实现，包括词法分析、语法分析、语义分析等。', 3, 48, NOW(), NOW()),
('CS403', '分布式系统', '学习分布式系统的设计原理、一致性算法、微服务架构等内容。', 3, 48, NOW(), NOW());

-- ============================================
-- 3. 课程组表 (course_groups) - 插入15条数据
-- ============================================
INSERT INTO `course_groups` (`course_id`, `teacher_id`, `semester`, `max_students`, `current_students`, `status`, `created_at`, `updated_at`) VALUES
-- 张教授 (teacher_id=2) 的课程
(1, 2, '2024春季', 50, 0, 1, NOW(), NOW()),
(2, 2, '2024春季', 40, 0, 1, NOW(), NOW()),
(3, 2, '2024秋季', 45, 0, 1, NOW(), NOW()),

-- 李老师 (teacher_id=3) 的课程
(4, 3, '2024春季', 35, 0, 1, NOW(), NOW()),
(5, 3, '2024春季', 40, 0, 1, NOW(), NOW()),
(6, 3, '2024秋季', 45, 0, 1, NOW(), NOW()),

-- 王教授 (teacher_id=4) 的课程
(7, 4, '2024春季', 50, 0, 1, NOW(), NOW()),
(8, 4, '2024秋季', 40, 0, 1, NOW(), NOW()),
(9, 4, '2024秋季', 45, 0, 1, NOW(), NOW()),

-- 刘老师 (teacher_id=5) 的课程
(8, 5, '2024春季', 40, 0, 1, NOW(), NOW()),
(10, 5, '2024春季', 35, 0, 1, NOW(), NOW()),

-- 陈教授 (teacher_id=6) 的课程
(10, 6, '2024秋季', 40, 0, 1, NOW(), NOW()),
(11, 6, '2024春季', 35, 0, 1, NOW(), NOW()),
(12, 6, '2024秋季', 45, 0, 1, NOW(), NOW()),

-- 其他课程
(1, 3, '2024秋季', 50, 0, 1, NOW(), NOW()),
(2, 4, '2024春季', 40, 0, 1, NOW(), NOW());

-- ============================================
-- 4. 班级表 (classes) - 插入12条数据
-- ============================================
INSERT INTO `classes` (`class_name`, `class_code`, `teacher_id`, `course_id`, `description`, `status`, `created_at`, `updated_at`) VALUES
('计算机科学导论-1班', 'CS101-01', 2, 1, '计算机科学导论第一班', 1, NOW(), NOW()),
('数据结构与算法-1班', 'CS201-01', 2, 2, '数据结构与算法第一班', 1, NOW(), NOW()),
('数据库系统-1班', 'CS301-01', 2, 3, '数据库系统第一班', 1, NOW(), NOW()),
('面向对象程序设计-1班', 'CS202-01', 3, 4, '面向对象程序设计第一班', 1, NOW(), NOW()),
('软件工程-1班', 'CS401-01', 3, 5, '软件工程第一班', 1, NOW(), NOW()),
('操作系统-1班', 'CS302-01', 3, 6, '操作系统第一班', 1, NOW(), NOW()),
('计算机网络-1班', 'CS402-01', 4, 7, '计算机网络第一班', 1, NOW(), NOW()),
('人工智能基础-1班', 'CS501-01', 4, 8, '人工智能基础第一班', 1, NOW(), NOW()),
('大数据技术-1班', 'CS502-01', 4, 9, '大数据技术第一班', 1, NOW(), NOW()),
('网络安全-1班', 'CS601-01', 5, 10, '网络安全第一班', 1, NOW(), NOW()),
('编译原理-1班', 'CS303-01', 6, 11, '编译原理第一班', 1, NOW(), NOW()),
('分布式系统-1班', 'CS403-01', 6, 12, '分布式系统第一班', 1, NOW(), NOW());

-- ============================================
-- 5. 学生选课关系表 (student_course_relations) - 插入25条数据
-- ============================================
INSERT INTO `student_course_relations` (`student_id`, `group_id`, `enroll_status`, `grade`, `created_at`, `updated_at`) VALUES
-- 学生7 (张三) 的选课
(7, 1, 'ENROLLED', NULL, NOW(), NOW()),
(7, 2, 'ENROLLED', NULL, NOW(), NOW()),
(7, 7, 'ENROLLED', NULL, NOW(), NOW()),

-- 学生8 (李四) 的选课
(8, 1, 'ENROLLED', NULL, NOW(), NOW()),
(8, 2, 'ENROLLED', NULL, NOW(), NOW()),
(8, 7, 'ENROLLED', NULL, NOW(), NOW()),

-- 学生9 (王五) 的选课
(9, 1, 'ENROLLED', NULL, NOW(), NOW()),
(9, 4, 'ENROLLED', NULL, NOW(), NOW()),
(9, 3, 'ENROLLED', NULL, NOW(), NOW()),

-- 学生10 (赵六) 的选课
(10, 1, 'ENROLLED', NULL, NOW(), NOW()),
(10, 4, 'ENROLLED', NULL, NOW(), NOW()),
(10, 3, 'ENROLLED', NULL, NOW(), NOW()),

-- 学生11 (钱七) 的选课
(11, 2, 'ENROLLED', NULL, NOW(), NOW()),
(11, 5, 'ENROLLED', NULL, NOW(), NOW()),
(11, 9, 'ENROLLED', NULL, NOW(), NOW()),

-- 学生12 (孙八) 的选课
(12, 2, 'ENROLLED', NULL, NOW(), NOW()),
(12, 5, 'ENROLLED', NULL, NOW(), NOW()),
(12, 9, 'ENROLLED', NULL, NOW(), NOW()),

-- 学生13 (周九) 的选课
(13, 4, 'ENROLLED', NULL, NOW(), NOW()),
(13, 8, 'ENROLLED', NULL, NOW(), NOW()),
(13, 6, 'ENROLLED', NULL, NOW(), NOW()),

-- 学生14 (吴十) 的选课
(14, 4, 'ENROLLED', NULL, NOW(), NOW()),
(14, 8, 'ENROLLED', NULL, NOW(), NOW()),
(14, 6, 'ENROLLED', NULL, NOW(), NOW()),

-- 学生15 (郑十一) 的选课
(15, 10, 'ENROLLED', NULL, NOW(), NOW()),
(15, 11, 'ENROLLED', NULL, NOW(), NOW()),
(15, 6, 'ENROLLED', NULL, NOW(), NOW()),

-- 学生16 (王十二) 的选课
(16, 10, 'ENROLLED', NULL, NOW(), NOW()),
(16, 11, 'ENROLLED', NULL, NOW(), NOW()),
(16, 6, 'ENROLLED', NULL, NOW(), NOW());

-- ============================================
-- 6. 课程课件/文章表 (course_materials) - 插入15条数据
-- ============================================
INSERT INTO `course_materials` (`group_id`, `uploader_id`, `title`, `type`, `content`, `url`, `original_filename`, `stored_filename`, `file_path`, `content_type`, `file_size`, `created_at`, `updated_at`) VALUES
-- 课程组1的材料
(1, 2, '计算机科学导论-第一章：计算机基础', 'ARTICLE', '# 第一章：计算机基础\n\n## 1.1 计算机的发展历史\n\n计算机的发展经历了多个阶段...', NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(1, 2, '计算机科学导论-课程大纲', 'SLIDE', NULL, 'https://example.com/slides/cs101-syllabus.pdf', NULL, NULL, NULL, 'application/pdf', NULL, NOW(), NOW()),
(1, 2, '编程环境配置指南', 'FILE', NULL, '/uploads/course-material/env-setup.pdf', '环境配置指南.pdf', 'env-setup-2024.pdf', './uploads/course-material/env-setup-2024.pdf', 'application/pdf', 1024000, NOW(), NOW()),

-- 课程组2的材料
(2, 2, '数据结构与算法-第一章：线性表', 'ARTICLE', '# 第一章：线性表\n\n## 1.1 顺序表\n\n顺序表是一种线性表的存储结构...', NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(2, 2, '算法复杂度分析', 'SLIDE', NULL, 'https://example.com/slides/complexity-analysis.pptx', NULL, NULL, NULL, 'application/vnd.openxmlformats-officedocument.presentationml.presentation', NULL, NOW(), NOW()),
(2, 2, '数据结构实验指导书', 'FILE', NULL, '/uploads/course-material/ds-lab-guide.pdf', '实验指导书.pdf', 'ds-lab-guide-2024.pdf', './uploads/course-material/ds-lab-guide-2024.pdf', 'application/pdf', 2048000, NOW(), NOW()),

-- 课程组3的材料
(3, 2, '数据库系统-第一章：数据库概述', 'ARTICLE', '# 第一章：数据库概述\n\n## 1.1 数据库的基本概念\n\n数据库是长期存储在计算机内的...', NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(3, 2, 'SQL语言参考', 'LINK', NULL, 'https://www.w3schools.com/sql/', NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),

-- 课程组4的材料
(4, 3, '面向对象程序设计-第一章：OOP基础', 'ARTICLE', '# 第一章：面向对象程序设计基础\n\n## 1.1 面向对象的概念\n\n面向对象是一种程序设计思想...', NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(4, 3, 'Java编程规范', 'FILE', NULL, '/uploads/course-material/java-style-guide.pdf', 'Java编程规范.pdf', 'java-style-guide.pdf', './uploads/course-material/java-style-guide.pdf', 'application/pdf', 512000, NOW(), NOW()),

-- 课程组5的材料
(5, 3, '软件工程-第一章：软件工程概述', 'ARTICLE', '# 第一章：软件工程概述\n\n## 1.1 什么是软件工程\n\n软件工程是将系统化的、规范的...', NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(5, 3, '软件开发生命周期', 'SLIDE', NULL, 'https://example.com/slides/sdlc.pptx', NULL, NULL, NULL, 'application/vnd.openxmlformats-officedocument.presentationml.presentation', NULL, NOW(), NOW()),

-- 课程组7的材料
(7, 4, '计算机网络-第一章：网络基础', 'ARTICLE', '# 第一章：网络基础\n\n## 1.1 计算机网络的定义\n\n计算机网络是指将地理位置不同的...', NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),
(7, 4, 'TCP/IP协议详解', 'LINK', NULL, 'https://www.rfc-editor.org/rfc/rfc793', NULL, NULL, NULL, NULL, NULL, NOW(), NOW()),

-- 课程组8的材料
(8, 4, '人工智能基础-第一章：AI概述', 'ARTICLE', '# 第一章：人工智能概述\n\n## 1.1 人工智能的定义\n\n人工智能（AI）是研究、开发用于...', NULL, NULL, NULL, NULL, NULL, NULL, NOW(), NOW());

-- ============================================
-- 更新统计信息
-- ============================================
-- 更新课程组的当前学生数（使用LEFT JOIN确保所有课程组都被更新）
UPDATE `course_groups` AS cg
LEFT JOIN (
    SELECT `group_id`, COUNT(*) AS student_count
    FROM `student_course_relations`
    WHERE `enroll_status` = 'ENROLLED'
    GROUP BY `group_id`
) AS sc ON cg.`id` = sc.`group_id`
SET cg.`current_students` = COALESCE(sc.`student_count`, 0);

-- ============================================
-- 数据统计
-- ============================================
-- users: 15条 (1管理员 + 5教师 + 10学生)
-- courses: 12条
-- course_groups: 15条
-- classes: 12条
-- student_course_relations: 25条
-- course_materials: 15条

