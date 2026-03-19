package com.eduai.pojo.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 时间安排DTO
 */
@Data
public class ScheduleSlotDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 教室ID
     */
    private Long classroomId;
    
    /**
     * 星期几（1-7，1表示周一）
     */
    private Integer dayOfWeek;
    
    /**
     * 开始周数
     */
    private Integer sectionStart;
    
    /**
     * 结束周数
     */
    private Integer sectionEnd;
    
    /**
     * 开始时间（HH:mm格式）
     */
    private String startTime;
    
    /**
     * 结束时间（HH:mm格式）
     */
    private String endTime;
}



