package com.eduai.pojo.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生实验尝试记录实体
 * 对应表 student_experiment_attempts
 */
@Data
public class StudentExperimentAttempt implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 学生用户ID（users.id）
     */
    private Long studentId;

    /**
     * 模板ID（learning_templates.id）
     */
    private Long templateId;

    /**
     * 课程ID（courses.id，可选）
     */
    private Long courseId;

    /**
     * 班级ID（classes.id，可选）
     */
    private Long classId;

    /**
     * 课程组ID（course_groups.id，可选）
     */
    private Long groupId;

    /**
     * 教师用户ID（users.id，可选，冗余便于查询）
     */
    private Long teacherId;

    /**
     * 第几次尝试（同 student_id+template_id 递增）
     */
    private Integer attemptNo;

    /**
     * 实验状态：STARTED/SUBMITTED/COMPLETED/ABORTED/FAILED/GRADED
     */
    private String status;

    /**
     * 是否完成实验（冗余字段）
     */
    private Boolean isCompleted;

    /**
     * 开始时间
     */
    private LocalDateTime startedAt;

    /**
     * 提交时间（学生点提交）
     */
    private LocalDateTime submittedAt;

    /**
     * 完成时间（系统判定完成）
     */
    private LocalDateTime completedAt;

    /**
     * 用时（秒）
     */
    private Integer durationSeconds;

    /**
     * 学生最终参数信息（JSON字符串）
     */
    private String paramsJson;

    /**
     * 相对模板默认参数的 diff（JSON字符串）
     */
    private String paramsDiffJson;

    /**
     * 学生实验结果（结构化JSON）
     */
    private String studentResultJson;

    /**
     * 学生实验结果（大文本/报告/日志摘要）
     */
    private String studentResultText;

    /**
     * 学生成绩（0-100等）
     */
    private BigDecimal score;

    /**
     * 成绩构成（rubric 明细 JSON）
     */
    private String scoreBreakdownJson;

    /**
     * 批改人（教师/管理员 users.id）
     */
    private Long gradedBy;

    /**
     * 批改时间
     */
    private LocalDateTime gradedAt;

    /**
     * 本次实验总 token 消耗
     */
    private Long tokenTotal;

    /**
     * prompt token
     */
    private Long tokenPrompt;

    /**
     * completion token
     */
    private Long tokenCompletion;

    /**
     * embedding token（如有）
     */
    private Long tokenEmbedding;

    /**
     * 成本金额
     */
    private BigDecimal tokenCost;

    /**
     * 模型名称（如 gpt-4.1-mini）
     */
    private String modelName;

    /**
     * 模型提供方
     */
    private String modelProvider;

    /**
     * 推理温度
     */
    private BigDecimal temperature;

    /**
     * 最大输出 token
     */
    private Integer maxOutputTokens;

    /**
     * 运行环境信息（JSON）
     */
    private String runtimeEnvJson;

    /**
     * 客户端 IP
     */
    private String clientIp;

    /**
     * User Agent
     */
    private String userAgent;

    /**
     * 失败错误码
     */
    private String errorCode;

    /**
     * 失败原因
     */
    private String errorMessage;

    /**
     * 本次实验使用的模板快照（JSON）
     */
    private String templateSnapshotJson;

    /**
     * 扩展字段（JSON）
     */
    private String metadata;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}









































