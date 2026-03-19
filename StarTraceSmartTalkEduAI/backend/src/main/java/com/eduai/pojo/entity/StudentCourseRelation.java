package com.eduai.pojo.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生选课关系实体类
 * 对应数据库表 student_course_relations
 */
@Data
public class StudentCourseRelation implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 关系ID，主键
     */
    private Long id;
    
    /**
     * 学生ID，外键关联users表
     */
    private Long studentId;
    
    /**
     * 课程组ID，外键关联course_groups表
     */
    private Long groupId;
    
    /**
     * 选课状态：ENROLLING-申请中，ENROLLED-已选课，DROPPED-已退课，COMPLETED-已完成
     */
    private String enrollStatus;
    
    /**
     * 成绩
     */
    private BigDecimal grade;
    
    /**
     * 创建时间（选课时间）
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 关联的课程组信息（不映射到数据库）
     */
    private CourseGroup courseGroup;
    
    /**
     * 关联的学生信息（不映射到数据库）
     */
    private User student;
}


