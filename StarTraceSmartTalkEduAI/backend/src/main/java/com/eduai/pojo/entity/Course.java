package com.eduai.pojo.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程实体类
 * 对应数据库表 courses
 */
@Data
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 课程ID，主键
     */
    private Long id;
    
    /**
     * 课程代码，如CS101
     */
    private String courseCode;
    
    /**
     * 课程名称
     */
    private String name;
    
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
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}



