package com.eduai.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学生实验产物/附件实体
 * 对应表 student_experiment_artifacts
 */
@Data
public class StudentExperimentArtifact implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 关联一次实验尝试（student_experiment_attempts.id）
     */
    private Long attemptId;

    /**
     * 产物类型：REPORT/FILE/IMAGE/EXPORT/LOG 等
     */
    private String artifactType;

    /**
     * 标题/说明
     */
    private String title;

    /**
     * 文件 URL（对象存储/静态资源）
     */
    private String url;

    /**
     * 文本内容（可选）
     */
    private String content;

    /**
     * MIME 类型
     */
    private String contentType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}









































