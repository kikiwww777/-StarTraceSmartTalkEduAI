package com.eduai.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 学生实验过程事件实体
 * 对应表 student_experiment_events
 */
@Data
public class StudentExperimentEvent implements Serializable {

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
     * 事件类型：PARAM_CHANGE/RUN/AI_CALL/UPLOAD/SUBMIT/ERROR/CHECKPOINT 等
     */
    private String eventType;

    /**
     * 事件发生时间
     */
    private LocalDateTime eventAt;

    /**
     * 同一次 attempt 内的事件序号
     */
    private Integer seq;

    /**
     * 事件内容（JSON：参数 diff、输入输出摘要、错误堆栈等）
     */
    private String payloadJson;

    /**
     * 事件大文本（可选）
     */
    private String payloadText;

    /**
     * 该事件消耗 token（如 AI_CALL）
     */
    private Long tokenUsed;

    /**
     * 耗时（毫秒，可选）
     */
    private Integer latencyMs;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}









































