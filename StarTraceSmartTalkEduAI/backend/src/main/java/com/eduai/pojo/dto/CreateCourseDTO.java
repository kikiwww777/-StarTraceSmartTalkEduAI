package com.eduai.pojo.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 创建课程DTO
 */
@Data
public class CreateCourseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 课程代码
     */
    private String courseCode;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 课程描述
     */
    private String description;
    
    /**
     * 学分
     */
    private Integer credit;
    
    /**
     * 总学时
     */
    private Integer totalHours;
    
    /**
     * 学期
     */
    private String semester;
    
    /**
     * 最大学生数
     */
    private Integer maxStudents;
}

