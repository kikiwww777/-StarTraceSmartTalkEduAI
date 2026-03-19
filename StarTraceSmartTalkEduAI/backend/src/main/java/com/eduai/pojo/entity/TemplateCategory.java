package com.eduai.pojo.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 模板分类实体类
 * 对应数据库表 templates_category
 */
@Data
public class TemplateCategory implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 分类ID，主键
     */
    private String id;
    
    /**
     * 分类标签
     */
    private String label;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}



























































