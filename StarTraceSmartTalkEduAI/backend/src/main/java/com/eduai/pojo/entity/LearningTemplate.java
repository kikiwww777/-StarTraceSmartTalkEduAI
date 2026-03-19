package com.eduai.pojo.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学习模板实体类
 * 对应数据库表 learning_templates
 */
@Data
public class LearningTemplate implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 模板ID，主键
     */
    private Long id;
    
    /**
     * 模板名称
     */
    private String templateName;
    
    /**
     * 模板描述
     */
    private String description;
    
    /**
     * 模板图片URL
     */
    private String imageUrl;
    
    /**
     * JSON模板内容（存储为JSON字符串）
     */
    private String jsonContent;
    
    /**
     * 老师ID（学号），外键关联users表
     */
    private Long teacherId;
    
    /**
     * 课程ID，外键关联courses表
     */
    private Long courseId;
    
    /**
     * 班级ID，外键关联classes表（可选）
     */
    private Long classId;
    
    /**
     * 学生下载量
     */
    private Integer downloadCount;
    
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
     * 关联的班级信息（不映射到数据库）
     */
    private Class classInfo;
}

