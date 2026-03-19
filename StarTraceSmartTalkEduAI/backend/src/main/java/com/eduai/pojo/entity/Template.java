package com.eduai.pojo.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 教学模板实体类
 * 对应数据库表 templates
 */
@Data
public class Template implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 模板ID，主键
     */
    private String id;
    
    /**
     * 模板标题
     */
    private String title;
    
    /**
     * 分类ID，外键关联templates_category表
     */
    private String category;
    
    /**
     * 模板描述
     */
    private String description;
    
    /**
     * 使用次数
     */
    private Integer usageCount;
    
    /**
     * 评分（0-5分）
     */
    private BigDecimal rating;
    
    /**
     * 主题颜色
     */
    private String color;
    
    /**
     * 图标SVG内容
     */
    private String icon;

    /**
     * 模板预设提示词（用于驱动 AI 对话的系统提示）
     */
    private String prompt;

    /**
     * 绑定的 SIM 工作流 ID
     */
    private String workflowId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 关联的分类信息（不映射到数据库）
     */
    private TemplateCategory categoryInfo;
}





