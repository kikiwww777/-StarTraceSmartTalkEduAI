package com.eduai.pojo.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 邀请学生DTO
 */
@Data
public class InviteStudentDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 课程组ID
     */
    private Long groupId;
    
    /**
     * 学生ID
     */
    private Long studentId;
}

