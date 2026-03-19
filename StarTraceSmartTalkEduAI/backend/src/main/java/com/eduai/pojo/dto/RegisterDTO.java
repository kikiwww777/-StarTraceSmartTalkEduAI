package com.eduai.pojo.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 注册请求DTO
 */
@Data
public class RegisterDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 学号，唯一标识（必填）
     */
    private String studentId;
    
    /**
     * 密码（必填）
     */
    private String password;
    
    /**
     * 用户角色：student-学生, teacher-教师, admin-管理员（可选，默认为student）
     */
    private String role;
}

