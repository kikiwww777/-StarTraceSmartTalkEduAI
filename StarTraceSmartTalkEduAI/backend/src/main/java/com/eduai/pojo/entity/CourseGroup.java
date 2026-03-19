package com.eduai.pojo.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程组实体类
 * 对应数据库表 course_groups
 */
@Data
public class CourseGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 课程组ID，主键
     */
    private Long id;
    
    /**
     * 课程ID，外键关联courses表
     */
    private Long courseId;
    
    /**
     * 教师ID，外键关联users表
     */
    private Long teacherId;
    
    /**
     * 学期，如：2024春季
     */
    private String semester;
    
    /**
     * 最大学生数
     */
    private Integer maxStudents;
    
    /**
     * 当前学生数
     */
    private Integer currentStudents;
    
    /**
     * 状态：1-正常，0-停开
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 关联的课程信息（不映射到数据库）
     */
    private Course course;
    
    /**
     * 关联的教师信息（不映射到数据库）
     */
    private User teacher;
}



