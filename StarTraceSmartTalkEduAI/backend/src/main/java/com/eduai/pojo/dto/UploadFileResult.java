package com.eduai.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传返回（与前端 UploadFileResult 对齐）
 */
@Data
public class UploadFileResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String url;
    private String filename;
}


