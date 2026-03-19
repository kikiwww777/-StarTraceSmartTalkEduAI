package com.eduai.pojo.dto;

import lombok.Data;

/**
 * 创建学习模板DTO
 */
@Data
public class CreateLearningTemplateDTO {
    
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
     * JSON模板内容（JSON字符串）
     */
    private String jsonContent;
    
    /**
     * 老师ID
     */
    private Long teacherId;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 班级ID（可选）
     */
    private Long classId;
}

