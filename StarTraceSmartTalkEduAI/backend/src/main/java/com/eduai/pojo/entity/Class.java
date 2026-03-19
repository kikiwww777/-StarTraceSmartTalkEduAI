package com.eduai.pojo.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 班级实体类
 * 对应数据库表 classes
 */
@Data
public class Class implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 班级ID，主键
     */
    private Long id;
    
    /**
     * 班级名称
     */
    private String className;
    
    /**
     * 班级代码
     */
    private String classCode;
    
    /**
     * 班主任/授课老师ID，外键关联users表
     */
    private Long teacherId;
    
    /**
     * 课程ID，外键关联courses表
     */
    private Long courseId;
    
    /**
     * 班级描述
     */
    private String description;
    
    /**
     * 状态：1-正常，0-停用
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
     * 关联的教师信息（不映射到数据库）
     */
    private User teacher;
    
    /**
     * 关联的课程信息（不映射到数据库）
     */
    private Course course;
    
    /**
     * 关联的课程组信息（不映射到数据库，用于获取开课时间、学期等信息）
     */
    private CourseGroup courseGroup;
}

