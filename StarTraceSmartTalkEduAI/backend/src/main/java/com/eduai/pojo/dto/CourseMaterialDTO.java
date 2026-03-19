package com.eduai.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 课程课件/文章 DTO（与前端 CourseMaterialDTO 对齐）
 */
@Data
public class CourseMaterialDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String type;   // ARTICLE/SLIDE/FILE/LINK
    private String content;
    private String url;
}


