package com.eduai.pojo.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 课程组DTO
 * 用于创建课程组
 */
@Data
public class CourseGroupDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 课程ID
     */
    private Long courseId;
    
    /**
     * 教师ID
     */
    private Long teacherId;
    
    /**
     * 学期
     */
    private String semester;
    
    /**
     * 最大学生数
     */
    private Integer maxStudents;
    
    /**
     * 时间安排（可选）
     */
    private List<ScheduleSlotDTO> scheduleSlots;
}



