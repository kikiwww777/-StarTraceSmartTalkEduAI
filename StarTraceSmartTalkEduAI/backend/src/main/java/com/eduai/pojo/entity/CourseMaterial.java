package com.eduai.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程课件/文章实体
 * 对应数据库表 course_materials
 */
@Data
public class CourseMaterial implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 课程组ID
     */
    private Long groupId;

    /**
     * 发布者（老师）ID，可选
     */
    private Long uploaderId;

    /**
     * 标题
     */
    private String title;

    /**
     * 类型：ARTICLE/SLIDE/FILE/LINK
     */
    private String type;

    /**
     * 内容（可选）
     */
    private String content;

    /**
     * 外链或文件URL（可选）
     */
    private String url;

    // FILE 类型元信息（可选）
    private String originalFilename;
    private String storedFilename;
    private String filePath;
    private String contentType;
    private Long fileSize;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


