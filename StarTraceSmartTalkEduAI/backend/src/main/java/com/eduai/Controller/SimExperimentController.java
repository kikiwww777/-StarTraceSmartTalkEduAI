package com.eduai.Controller;

import com.eduai.Service.SimExperimentService;
import com.eduai.pojo.Result;
import com.eduai.pojo.entity.StudentExperimentArtifact;
import com.eduai.pojo.entity.StudentExperimentAttempt;
import com.eduai.pojo.entity.StudentExperimentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Sim 实验监测相关接口
 * 提供给 Sim 前端或其他服务调用
 */
@RestController
@RequestMapping("/api/sim/experiments")
public class SimExperimentController {

    @Autowired
    private SimExperimentService simExperimentService;

    /**
     * 开始一次实验尝试
     */
    @PostMapping("/attempts/start")
    public Result<StudentExperimentAttempt> startAttempt(@RequestBody StudentExperimentAttempt attempt) {
        try {
            StudentExperimentAttempt saved = simExperimentService.startAttempt(attempt);
            return Result.success(saved);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("开始实验失败：" + e.getMessage());
        }
    }

    /**
     * 记录一次过程事件
     */
    @PostMapping("/attempts/{attemptId}/events")
    public Result<StudentExperimentEvent> recordEvent(@PathVariable Long attemptId,
                                                      @RequestBody StudentExperimentEvent event) {
        try {
            event.setAttemptId(attemptId);
            StudentExperimentEvent saved = simExperimentService.recordEvent(event);
            return Result.success(saved);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("记录事件失败：" + e.getMessage());
        }
    }

    /**
     * 记录一个实验产物
     */
    @PostMapping("/attempts/{attemptId}/artifacts")
    public Result<StudentExperimentArtifact> recordArtifact(@PathVariable Long attemptId,
                                                            @RequestBody StudentExperimentArtifact artifact) {
        try {
            artifact.setAttemptId(attemptId);
            StudentExperimentArtifact saved = simExperimentService.recordArtifact(artifact);
            return Result.success(saved);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("记录实验产物失败：" + e.getMessage());
        }
    }

    /**
     * 学生提交实验
     */
    @PostMapping("/attempts/{attemptId}/submit")
    public Result<StudentExperimentAttempt> submitAttempt(@PathVariable Long attemptId,
                                                          @RequestBody StudentExperimentAttempt payload) {
        try {
            payload.setId(attemptId);
            StudentExperimentAttempt saved = simExperimentService.submitAttempt(payload);
            return Result.success(saved);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("提交实验失败：" + e.getMessage());
        }
    }

    /**
     * 标记实验完成
     */
    @PostMapping("/attempts/{attemptId}/complete")
    public Result<StudentExperimentAttempt> completeAttempt(@PathVariable Long attemptId,
                                                            @RequestBody StudentExperimentAttempt payload) {
        try {
            payload.setId(attemptId);
            StudentExperimentAttempt saved = simExperimentService.completeAttempt(payload);
            return Result.success(saved);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("完成实验失败：" + e.getMessage());
        }
    }

    /**
     * 标记实验失败
     */
    @PostMapping("/attempts/{attemptId}/fail")
    public Result<StudentExperimentAttempt> failAttempt(@PathVariable Long attemptId,
                                                        @RequestBody StudentExperimentAttempt payload) {
        try {
            payload.setId(attemptId);
            StudentExperimentAttempt saved = simExperimentService.failAttempt(payload);
            return Result.success(saved);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("标记实验失败失败：" + e.getMessage());
        }
    }
}









































