package com.eduai.Service;

import com.eduai.pojo.entity.StudentExperimentArtifact;
import com.eduai.pojo.entity.StudentExperimentAttempt;
import com.eduai.pojo.entity.StudentExperimentEvent;

/**
 * Sim 实验监测服务接口
 */
public interface SimExperimentService {

    /**
     * 开始一次实验尝试
     */
    StudentExperimentAttempt startAttempt(StudentExperimentAttempt attempt);

    /**
     * 记录一次过程事件
     */
    StudentExperimentEvent recordEvent(StudentExperimentEvent event);

    /**
     * 记录一个实验产物
     */
    StudentExperimentArtifact recordArtifact(StudentExperimentArtifact artifact);

    /**
     * 学生提交实验（更新状态及结果）
     */
    StudentExperimentAttempt submitAttempt(StudentExperimentAttempt attempt);

    /**
     * 标记实验完成（包括耗时和 token 统计）
     */
    StudentExperimentAttempt completeAttempt(StudentExperimentAttempt attempt);

    /**
     * 标记实验失败
     */
    StudentExperimentAttempt failAttempt(StudentExperimentAttempt attempt);
}









































