-- EduAI 数据库初始化脚本
-- 模块A: 课程与权限管理

-- 课程表
CREATE TABLE IF NOT EXISTS courses (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    instructor_id VARCHAR(36) NOT NULL,
    workspace_id VARCHAR(36),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 课程成员表
CREATE TABLE IF NOT EXISTS course_members (
    id VARCHAR(36) PRIMARY KEY,
    course_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    role ENUM('teacher', 'student') NOT NULL DEFAULT 'student',
    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_course_user (course_id, user_id),
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_courses_instructor ON courses(instructor_id);
CREATE INDEX idx_members_user ON course_members(user_id);
CREATE INDEX idx_members_course ON course_members(course_id);
