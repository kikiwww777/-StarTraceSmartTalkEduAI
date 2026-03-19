package com.eduai.pojo.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 班级学生关系实体类
 * 对应数据库表 class_students
 */
@Data
public class ClassStudent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 关系ID，主键
     */
    private Long id;
    
    /**
     * 班级ID，外键关联classes表
     */
    private Long classId;
    
    /**
     * 学生ID，外键关联users表
     */
    private Long studentId;
    
    /**
     * 状态：ENROLLED-在读，DROPPED-退班，COMPLETED-已完成
     */
    private String enrollStatus;
    
    /**
     * 加入班级时间
     */
    private LocalDateTime enrollDate;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 关联的班级信息（不映射到数据库）
     */
    private Class classInfo;
    
    /**
     * 关联的学生信息（不映射到数据库）
     */
    private User student;
}

