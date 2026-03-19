package com.eduai.config;

import com.eduai.Mapper.SimExecutionLogMapper;
import com.eduai.Mapper.SimWebhookLogMapper;
import com.eduai.pojo.entity.SimExecutionLog;
import com.eduai.pojo.entity.SimWebhookLog;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 当 Sim 无法向 EDUAI 回调 Webhook 时（例如使用官方镜像、网络限制等），
 * 通过读取 Sim Studio 的 Postgres 数据库，定时同步执行日志到 EDUAI 的 MySQL 表：
 * - sim_execution_logs
 * - sim_webhook_logs（作为“同步记录”，非真实 webhook）
 *
 * 注意：student_token_usage 需要 studentId/courseId 等业务 metadata。
 * 如果 Sim 的 execution_data 中没有这些字段，此同步任务不会写入 student_token_usage。
 */
@Component
public class SimLogSyncScheduler {

    private final SimExecutionLogMapper simExecutionLogMapper;
    private final SimWebhookLogMapper simWebhookLogMapper;
    private final ObjectMapper objectMapper;

    private final JdbcTemplate simJdbc;

    private volatile LocalDateTime lastCursor = LocalDateTime.now().minusMinutes(30);
    private final boolean enabled;

    @Autowired
    public SimLogSyncScheduler(
            SimExecutionLogMapper simExecutionLogMapper,
            SimWebhookLogMapper simWebhookLogMapper,
            ObjectMapper objectMapper,
            @Value("${sim.postgres.enabled:true}") boolean enabled,
            @Value("${sim.postgres.url}") String pgUrl,
            @Value("${sim.postgres.username}") String pgUser,
            @Value("${sim.postgres.password}") String pgPass
    ) {
        this.simExecutionLogMapper = simExecutionLogMapper;
        this.simWebhookLogMapper = simWebhookLogMapper;
        this.objectMapper = objectMapper;
        this.enabled = enabled;
        this.simJdbc = new JdbcTemplate(buildDataSource(pgUrl, pgUser, pgPass));
    }

    private static DataSource buildDataSource(String url, String username, String password) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

    @Scheduled(fixedDelayString = "60000")
    public void syncRecentExecutions() {
        if (!enabled) {
            return;
        }
        // 拉取最近一段时间新增的执行日志（按 created_at 游标）
        final LocalDateTime windowStart = lastCursor;
        final LocalDateTime now = LocalDateTime.now();

        List<Map<String, Object>> rows;
        try {
            rows = simJdbc.queryForList(
                    """
                    SELECT
                      execution_id,
                      workflow_id,
                      workspace_id,
                      level,
                      trigger,
                      started_at,
                      ended_at,
                      total_duration_ms,
                      cost,
                      execution_data,
                      created_at
                    FROM workflow_execution_logs
                    WHERE created_at > ?
                    ORDER BY created_at ASC
                    LIMIT 200
                    """,
                    Timestamp.valueOf(windowStart)
            );
        } catch (CannotGetJdbcConnectionException e) {
            // Postgres not reachable yet (e.g., Sim docker services not running) - try again next tick.
            return;
        } catch (Exception e) {
            // Best-effort scheduler; don't fail the scheduling thread.
            return;
        }

        for (Map<String, Object> row : rows) {
            try {
                String executionId = asString(row.get("execution_id"));
                String workflowId = asString(row.get("workflow_id"));
                String workspaceId = asString(row.get("workspace_id"));
                String level = asString(row.get("level"));
                String trigger = asString(row.get("trigger"));

                Integer durationMs = asInt(row.get("total_duration_ms"));
                LocalDateTime startedAt = asLocalDateTime(row.get("started_at"));
                LocalDateTime endedAt = asLocalDateTime(row.get("ended_at"));

                String costJson = asString(row.get("cost"));
                String executionDataJson = asString(row.get("execution_data"));

                BigDecimal costTotal = null;
                Integer inputTokens = 0;
                Integer outputTokens = 0;
                Integer totalTokens = 0;

                try {
                    if (costJson != null && !costJson.isBlank()) {
                        JsonNode costNode = objectMapper.readTree(costJson);
                        if (costNode.hasNonNull("total")) {
                            costTotal = new BigDecimal(costNode.get("total").asText("0"));
                        }
                        JsonNode tokens = costNode.path("tokens");
                        inputTokens = tokens.path("input").asInt(0);
                        outputTokens = tokens.path("output").asInt(0);
                        totalTokens = tokens.path("total").asInt(inputTokens + outputTokens);
                    }
                } catch (Exception ignored) {
                    // best-effort
                }

                // 写入 sim_execution_logs
                SimExecutionLog exec = new SimExecutionLog();
                exec.setExecutionId(executionId);
                exec.setWorkspaceId(workspaceId);
                exec.setWorkflowId(workflowId);
                exec.setWorkflowName(null);
                exec.setTriggerType(trigger);
                exec.setStatus("error".equalsIgnoreCase(level) ? "error" : "success");
                exec.setLevel(level != null ? level : "info");
                exec.setDurationMs(durationMs != null ? durationMs : 0);
                exec.setCostUsd(costTotal != null ? costTotal : BigDecimal.ZERO);
                exec.setModel(null);
                exec.setInputTokens(inputTokens);
                exec.setOutputTokens(outputTokens);
                exec.setTotalTokens(totalTokens);
                exec.setStartTime(startedAt);
                exec.setEndTime(endedAt);
                exec.setErrorMessage(null);

                // execution_data 中尽量提取 traceSpans/finalOutput/triggerData
                try {
                    if (executionDataJson != null && !executionDataJson.isBlank()) {
                        JsonNode data = objectMapper.readTree(executionDataJson);
                        JsonNode trace = data.path("traceSpans");
                        if (!trace.isMissingNode() && !trace.isNull()) {
                            exec.setTraceSpans(trace.toString());
                        }
                        JsonNode finalOut = data.path("finalOutput");
                        if (!finalOut.isMissingNode() && !finalOut.isNull()) {
                            exec.setFinalOutput(finalOut.toString());
                        }
                        // 把 trigger 全量塞进 metadata，便于后续排查/扩展学生映射
                        JsonNode trig = data.path("trigger");
                        if (!trig.isMissingNode() && !trig.isNull()) {
                            exec.setMetadata(trig.toString());
                        } else {
                            exec.setMetadata(data.toString());
                        }
                    }
                } catch (Exception ignored) {
                    // best-effort
                }

                LocalDateTime t = LocalDateTime.now();
                exec.setSyncedAt(t);
                exec.setCreatedAt(t);
                exec.setUpdatedAt(t);

                try {
                    simExecutionLogMapper.insert(exec);
                } catch (DuplicateKeyException dup) {
                    // already synced
                }

                // 写入 sim_webhook_logs（作为同步记录）
                SimWebhookLog wh = new SimWebhookLog();
                wh.setExecutionId(executionId);
                wh.setWorkflowId(workflowId);
                wh.setWorkspaceId(workspaceId);
                wh.setStatus(exec.getStatus());
                wh.setDurationMs(exec.getDurationMs());
                wh.setCostUsd(exec.getCostUsd());
                wh.setRequestBody(buildRequestBody(executionId, row, costJson, executionDataJson));
                wh.setSignature("SYNC");
                wh.setSignatureValid(true);
                wh.setProcessed(true);
                wh.setErrorMessage(null);
                wh.setCreatedAt(t);
                wh.setUpdatedAt(t);
                simWebhookLogMapper.insert(wh);

            } catch (Exception ignored) {
                // best-effort; don't stop the loop
            }
        }

        // only advance cursor after a successful fetch (even if 0 rows)
        lastCursor = now;
    }

    private String buildRequestBody(String executionId, Map<String, Object> row, String costJson, String executionDataJson) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "source", "postgres-sync",
                    "executionId", executionId,
                    "row", row,
                    "cost", costJson,
                    "executionData", executionDataJson
            ));
        } catch (Exception e) {
            return "{\"source\":\"postgres-sync\",\"executionId\":\"" + executionId + "\"}";
        }
    }

    private static String asString(Object v) {
        if (v == null) return null;
        return String.valueOf(v);
    }

    private static Integer asInt(Object v) {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).intValue();
        try {
            return Integer.parseInt(String.valueOf(v));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static LocalDateTime asLocalDateTime(Object v) {
        if (v == null) return null;
        if (v instanceof Timestamp) {
            return ((Timestamp) v).toLocalDateTime();
        }
        if (v instanceof java.util.Date) {
            return new Timestamp(((java.util.Date) v).getTime()).toLocalDateTime();
        }
        try {
            return LocalDateTime.parse(String.valueOf(v));
        } catch (Exception e) {
            return null;
        }
    }
}


