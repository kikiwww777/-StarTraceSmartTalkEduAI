package com.eduai.pojo.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表 users
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID，主键
     */
    private Long id;
    
    /**
     * 学号/工号，唯一标识
     */
    private String studentId;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 密码（加密存储）
     */
    private String password;
    
    /**
     * 用户角色：student-学生, teacher-教师, admin-管理员
     */
    private String role;
    
    /**
     * 学科/专业
     */
    private String subject;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;
    
    /**
     * 头像URL
     */
    private String avatar;
    
    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}




