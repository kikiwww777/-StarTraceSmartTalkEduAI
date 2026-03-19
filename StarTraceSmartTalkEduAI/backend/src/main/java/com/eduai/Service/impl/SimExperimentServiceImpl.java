package com.eduai.Service.impl;

import com.eduai.Mapper.StudentExperimentArtifactMapper;
import com.eduai.Mapper.StudentExperimentAttemptMapper;
import com.eduai.Mapper.StudentExperimentEventMapper;
import com.eduai.Service.SimExperimentService;
import com.eduai.pojo.entity.StudentExperimentArtifact;
import com.eduai.pojo.entity.StudentExperimentAttempt;
import com.eduai.pojo.entity.StudentExperimentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Sim 实验监测服务实现
 */
@Service
public class SimExperimentServiceImpl implements SimExperimentService {

    @Autowired
    private StudentExperimentAttemptMapper attemptMapper;

    @Autowired
    private StudentExperimentEventMapper eventMapper;

    @Autowired
    private StudentExperimentArtifactMapper artifactMapper;

    @Override
    @Transactional
    public StudentExperimentAttempt startAttempt(StudentExperimentAttempt attempt) {
        if (attempt.getStudentId() == null || attempt.getTemplateId() == null) {
            throw new IllegalArgumentException("studentId 和 templateId 不能为空");
        }
        // 计算 attempt_no
        Integer maxNo = attemptMapper.selectMaxAttemptNo(attempt.getStudentId(), attempt.getTemplateId());
        int nextNo = (maxNo == null ? 0 : maxNo) + 1;
        attempt.setAttemptNo(nextNo);
        attempt.setStatus("STARTED");
        attempt.setIsCompleted(false);
        LocalDateTime now = LocalDateTime.now();
        if (attempt.getStartedAt() == null) {
            attempt.setStartedAt(now);
        }
        // 设置 token 相关字段的默认值，避免数据库约束错误
        if (attempt.getTokenTotal() == null) {
            attempt.setTokenTotal(0L);
        }
        if (attempt.getTokenPrompt() == null) {
            attempt.setTokenPrompt(0L);
        }
        if (attempt.getTokenCompletion() == null) {
            attempt.setTokenCompletion(0L);
        }
        if (attempt.getTokenEmbedding() == null) {
            attempt.setTokenEmbedding(0L);
        }
        attempt.setCreatedAt(now);
        attempt.setUpdatedAt(now);
        attemptMapper.insert(attempt);
        return attempt;
    }

    @Override
    @Transactional
    public StudentExperimentEvent recordEvent(StudentExperimentEvent event) {
        if (event.getAttemptId() == null) {
            throw new IllegalArgumentException("attemptId 不能为空");
        }
        if (event.getEventAt() == null) {
            event.setEventAt(LocalDateTime.now());
        }
        event.setCreatedAt(LocalDateTime.now());
        if (event.getTokenUsed() == null) {
            event.setTokenUsed(0L);
        }
        eventMapper.insert(event);
        return event;
    }

    @Override
    @Transactional
    public StudentExperimentArtifact recordArtifact(StudentExperimentArtifact artifact) {
        if (artifact.getAttemptId() == null) {
            throw new IllegalArgumentException("attemptId 不能为空");
        }
        artifact.setCreatedAt(LocalDateTime.now());
        artifactMapper.insert(artifact);
        return artifact;
    }

    @Override
    @Transactional
    public StudentExperimentAttempt submitAttempt(StudentExperimentAttempt attempt) {
        if (attempt.getId() == null) {
            throw new IllegalArgumentException("id 不能为空");
        }
        attempt.setStatus("SUBMITTED");
        if (attempt.getSubmittedAt() == null) {
            attempt.setSubmittedAt(LocalDateTime.now());
        }
        attempt.setUpdatedAt(LocalDateTime.now());
        attemptMapper.update(attempt);
        return attemptMapper.selectById(attempt.getId());
    }

    @Override
    @Transactional
    public StudentExperimentAttempt completeAttempt(StudentExperimentAttempt attempt) {
        if (attempt.getId() == null) {
            throw new IllegalArgumentException("id 不能为空");
        }
        attempt.setStatus("COMPLETED");
        attempt.setIsCompleted(true);
        if (attempt.getCompletedAt() == null) {
            attempt.setCompletedAt(LocalDateTime.now());
        }
        // 如未显式传 duration，则根据时间自动计算
        if (attempt.getDurationSeconds() == null && attempt.getStartedAt() != null) {
            attempt.setDurationSeconds(
                    (int) java.time.Duration.between(attempt.getStartedAt(), attempt.getCompletedAt()).getSeconds()
            );
        }
        attempt.setUpdatedAt(LocalDateTime.now());
        attemptMapper.update(attempt);
        return attemptMapper.selectById(attempt.getId());
    }

    @Override
    @Transactional
    public StudentExperimentAttempt failAttempt(StudentExperimentAttempt attempt) {
        if (attempt.getId() == null) {
            throw new IllegalArgumentException("id 不能为空");
        }
        attempt.setStatus("FAILED");
        attempt.setIsCompleted(false);
        attempt.setUpdatedAt(LocalDateTime.now());
        attemptMapper.update(attempt);
        return attemptMapper.selectById(attempt.getId());
    }
}



