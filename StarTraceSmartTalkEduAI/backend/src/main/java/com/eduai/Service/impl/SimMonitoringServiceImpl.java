package com.eduai.Service.impl;

import com.eduai.Mapper.SimExecutionLogMapper;
import com.eduai.Mapper.SimWebhookLogMapper;
import com.eduai.Mapper.StudentTokenUsageMapper;
import com.eduai.Service.SimMonitoringService;
import com.eduai.pojo.entity.SimExecutionLog;
import com.eduai.pojo.entity.SimWebhookLog;
import com.eduai.pojo.entity.StudentTokenUsage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Sim 执行监控与 Webhook 处理服务实现
 */
@Service
public class SimMonitoringServiceImpl implements SimMonitoringService {

    @Autowired
    private SimExecutionLogMapper simExecutionLogMapper;

    @Autowired
    private SimWebhookLogMapper simWebhookLogMapper;

    @Autowired
    private StudentTokenUsageMapper studentTokenUsageMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public void handleExecutionWebhook(String rawBody, String signature) {
        LocalDateTime now = LocalDateTime.now();

        SimWebhookLog webhookLog = new SimWebhookLog();
        webhookLog.setRequestBody(rawBody);
        webhookLog.setSignature(signature);
        webhookLog.setSignatureValid(true); // 签名校验在 Controller 中完成后再设置为 true
        webhookLog.setProcessed(false);
        webhookLog.setCreatedAt(now);
        webhookLog.setUpdatedAt(now);

        try {
            JsonNode root = objectMapper.readTree(rawBody);

            // Sim 通知 Webhook（workflow.execution.completed）结构：
            // { id, type, timestamp, data: { executionId, workflowId, workflowName, workspaceId?, status, level, trigger, ... } }
            JsonNode dataNode = root.path("data");

            // 顶层常见字段（尽量兼容不同结构）
            String executionId = firstNonNull(
                    getText(dataNode, "executionId"),
                    getText(root, "executionId")
            );
            String workspaceId = firstNonNull(
                    getText(dataNode, "workspaceId"),
                    getText(root, "workspaceId")
            );
            String workflowId = firstNonNull(
                    getText(dataNode, "workflowId"),
                    getText(root, "workflowId")
            );
            String workflowName = firstNonNull(
                    getText(dataNode, "workflowName"),
                    getText(root, "workflowName")
            );
            String status = firstNonNull(
                    getText(dataNode, "status"),
                    getText(root, "status")
            );
            // Sim payload 使用 trigger 字段；旧解析使用 triggerType
            String triggerType = firstNonNull(
                    getText(dataNode, "trigger"),
                    getText(dataNode, "triggerType"),
                    getText(root, "triggerType")
            );

            webhookLog.setExecutionId(executionId);
            webhookLog.setWorkspaceId(workspaceId);
            webhookLog.setWorkflowId(workflowId);
            webhookLog.setStatus(status);

            // token & cost 通常在 data.tokenUsage / data.metrics / data.cost 中
            JsonNode tokenUsage = !dataNode.path("tokenUsage").isMissingNode()
                    ? dataNode.path("tokenUsage")
                    : root.path("tokenUsage");
            JsonNode metrics = !dataNode.path("metrics").isMissingNode()
                    ? dataNode.path("metrics")
                    : root.path("metrics");

            Integer inputTokens = getInt(tokenUsage, "inputTokens");
            Integer outputTokens = getInt(tokenUsage, "outputTokens");
            Integer totalTokens = getInt(tokenUsage, "totalTokens");
            if (totalTokens == null && inputTokens != null && outputTokens != null) {
                totalTokens = inputTokens + outputTokens;
            }

            BigDecimal costUsd = getDecimal(metrics, "costUsd");
            if (costUsd == null) costUsd = getDecimal(tokenUsage, "costUsd");
            if (costUsd == null) {
                JsonNode costNode = dataNode.path("cost");
                if (!costNode.isMissingNode() && !costNode.isNull()) {
                    costUsd = getDecimal(costNode, "total");
                }
            }

            Integer durationMs = getInt(metrics, "durationMs");
            if (durationMs == null) {
                durationMs = getInt(dataNode, "totalDurationMs");
            }

            webhookLog.setDurationMs(durationMs);
            webhookLog.setCostUsd(costUsd);

            // 先插入 webhook 日志（方便排查）
            simWebhookLogMapper.insert(webhookLog);

            // 构造执行日志
            SimExecutionLog exec = new SimExecutionLog();
            exec.setExecutionId(executionId);
            exec.setWorkspaceId(workspaceId);
            exec.setWorkflowId(workflowId);
            exec.setWorkflowName(workflowName);
            exec.setTriggerType(triggerType);
            exec.setStatus(status);
            exec.setLevel(firstNonNull(getText(dataNode, "level"), "info"));
            exec.setDurationMs(durationMs);
            exec.setCostUsd(costUsd);
            exec.setModel(firstNonNull(getText(dataNode, "model"), getText(root, "model")));
            exec.setInputTokens(inputTokens);
            exec.setOutputTokens(outputTokens);
            exec.setTotalTokens(totalTokens);
            exec.setStartTime(parseDateTime(dataNode, "startedAt"));
            exec.setEndTime(parseDateTime(dataNode, "endedAt"));
            exec.setErrorMessage(firstNonNull(getText(dataNode, "errorMessage"), getText(root, "errorMessage")));

            JsonNode traceNode = !dataNode.path("traceSpans").isMissingNode()
                    ? dataNode.path("traceSpans")
                    : root.path("traceSpans");
            if (!traceNode.isMissingNode() && !traceNode.isNull()) {
                exec.setTraceSpans(traceNode.toString());
            }
            JsonNode finalOutput = !dataNode.path("finalOutput").isMissingNode()
                    ? dataNode.path("finalOutput")
                    : root.path("finalOutput");
            if (!finalOutput.isMissingNode() && !finalOutput.isNull()) {
                exec.setFinalOutput(finalOutput.toString());
            }
            JsonNode inputData = !dataNode.path("inputData").isMissingNode()
                    ? dataNode.path("inputData")
                    : root.path("inputData");
            if (!inputData.isMissingNode() && !inputData.isNull()) {
                exec.setInputData(inputData.toString());
            }
            JsonNode metadataNode = !dataNode.path("metadata").isMissingNode()
                    ? dataNode.path("metadata")
                    : root.path("metadata");
            if (!metadataNode.isMissingNode() && !metadataNode.isNull()) {
                exec.setMetadata(metadataNode.toString());
            }

            exec.setSyncedAt(now);
            exec.setCreatedAt(now);
            exec.setUpdatedAt(now);
            simExecutionLogMapper.insert(exec);

            // 解析 metadata 中的学生 / 课程 / 班级信息，用于写入 student_token_usage
            if (!metadataNode.isMissingNode() && !metadataNode.isNull()) {
                Map<String, Object> metadata = objectMapper.convertValue(
                        metadataNode, new TypeReference<Map<String, Object>>() {
                        });

                Long studentId = getLongFromMap(metadata, "studentId");
                Long courseId = getLongFromMap(metadata, "courseId");
                Long classId = getLongFromMap(metadata, "classId");

                if (studentId != null && courseId != null) {
                    StudentTokenUsage usage = new StudentTokenUsage();
                    usage.setStudentId(studentId);
                    usage.setCourseId(courseId);
                    usage.setClassId(classId);
                    usage.setWorkflowId(workflowId);
                    usage.setExecutionId(executionId);
                    usage.setWorkspaceId(workspaceId);
                    usage.setModelName(exec.getModel());
                    usage.setInputTokens(inputTokens);
                    usage.setOutputTokens(outputTokens);
                    usage.setTotalTokens(totalTokens);
                    usage.setCostUsd(costUsd);
                    usage.setDurationMs(durationMs);
                    usage.setTriggerType(triggerType);
                    usage.setStatus(status);
                    usage.setErrorMessage(exec.getErrorMessage());
                    usage.setCreatedAt(now);
                    usage.setUpdatedAt(now);

                    studentTokenUsageMapper.insert(usage);
                }
            }

            webhookLog.setSignatureValid(true);
            webhookLog.setProcessed(true);
            webhookLog.setUpdatedAt(now);
            simWebhookLogMapper.updateProcessed(webhookLog);

        } catch (Exception e) {
            webhookLog.setSignatureValid(true);
            webhookLog.setProcessed(false);
            webhookLog.setErrorMessage(e.getMessage());
            webhookLog.setUpdatedAt(now);
            simWebhookLogMapper.updateProcessed(webhookLog);
            // 不中断请求，让 Sim 认为 Webhook 接收成功，但记录错误便于排查
        }
    }

    private static String getText(JsonNode node, String field) {
        JsonNode v = node.path(field);
        if (v.isMissingNode() || v.isNull()) {
            return null;
        }
        return v.asText();
    }

    private static Integer getInt(JsonNode node, String field) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        JsonNode v = node.path(field);
        if (v.isMissingNode() || v.isNull() || !v.isNumber()) {
            return null;
        }
        return v.intValue();
    }

    private static BigDecimal getDecimal(JsonNode node, String field) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        JsonNode v = node.path(field);
        if (v.isMissingNode() || v.isNull() || !v.isNumber()) {
            return null;
        }
        return new BigDecimal(v.asText());
    }

    private static LocalDateTime parseDateTime(JsonNode node, String field) {
        String text = getText(node, field);
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(text);
        } catch (Exception e) {
            return null;
        }
    }

    private static Long getLongFromMap(Map<String, Object> map, String key) {
        Object v = map.get(key);
        if (v == null) {
            return null;
        }
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        try {
            return Long.parseLong(v.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String firstNonNull(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.isEmpty()) {
                return v;
            }
        }
        return null;
    }
}


