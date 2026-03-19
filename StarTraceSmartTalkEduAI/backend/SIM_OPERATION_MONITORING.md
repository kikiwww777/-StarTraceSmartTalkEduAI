# 内嵌 Sim 操作监控方案

## 一、监控架构概览

内嵌 Sim 操作监控采用**三层监控架构**：

```
┌─────────────────────────────────────────────────────────┐
│  前端监控层（Frontend Monitoring）                        │
│  - iframe 消息监听                                        │
│  - 执行状态实时上报                                       │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│  Webhook 接收层（Webhook Receiver）                       │
│  - Sim 执行完成通知                                       │
│  - HMAC 签名验证                                          │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│  数据持久化层（Data Persistence）                         │
│  - Token 使用记录                                         │
│  - 执行日志存储                                           │
│  - 配额统计更新                                           │
└─────────────────────────────────────────────────────────┘
```

## 二、监控内容清单

### 2.1 执行层面监控

| 监控项 | 说明 | 数据来源 | 存储表 |
|--------|------|----------|--------|
| **执行ID** | Sim 工作流执行唯一标识 | Sim Webhook/API | `sim_execution_logs.execution_id` |
| **工作流ID** | Sim 工作流标识 | Sim Webhook/API | `sim_execution_logs.workflow_id` |
| **工作空间ID** | Sim 工作空间标识 | Sim Webhook/API | `sim_execution_logs.workspace_id` |
| **执行状态** | success/error/timeout/cancelled | Sim Webhook/API | `sim_execution_logs.status` |
| **执行耗时** | 毫秒级执行时间 | Sim Webhook/API | `sim_execution_logs.duration_ms` |
| **触发类型** | manual/api/webhook/schedule/chat | Sim Webhook/API | `sim_execution_logs.trigger_type` |
| **开始时间** | 执行开始时间戳 | Sim Webhook/API | `sim_execution_logs.start_time` |
| **结束时间** | 执行结束时间戳 | Sim Webhook/API | `sim_execution_logs.end_time` |
| **错误信息** | 执行失败时的错误详情 | Sim Webhook/API | `sim_execution_logs.error_message` |

### 2.2 Token 使用监控

| 监控项 | 说明 | 数据来源 | 存储表 |
|--------|------|----------|--------|
| **输入Token** | 输入到 LLM 的 Token 数量 | Sim API | `student_token_usage.input_tokens` |
| **输出Token** | LLM 输出的 Token 数量 | Sim API | `student_token_usage.output_tokens` |
| **总Token** | 输入+输出 Token 总数 | Sim API | `student_token_usage.total_tokens` |
| **模型名称** | 使用的 LLM 模型 | Sim API | `student_token_usage.model_name` |
| **执行成本** | 本次执行的成本（美元） | Sim API | `student_token_usage.cost_usd` |

### 2.3 学生关联监控

| 监控项 | 说明 | 数据来源 | 存储表 |
|--------|------|----------|--------|
| **学生ID** | 执行操作的学生 | 前端传递 metadata | `student_token_usage.student_id` |
| **课程ID** | 所属课程 | 前端传递 metadata | `student_token_usage.course_id` |
| **班级ID** | 所属班级 | 前端传递 metadata | `student_token_usage.class_id` |
| **实验尝试ID** | 关联的实验尝试记录 | 前端传递 metadata | `student_experiment_attempts.id` |

### 2.4 配额监控

| 监控项 | 说明 | 数据来源 | 存储表 |
|--------|------|----------|--------|
| **当日Token使用** | 学生当日已使用 Token | 实时统计 | `student_daily_quota.tokens_used` |
| **当日执行次数** | 学生当日执行次数 | 实时统计 | `student_daily_quota.execution_count` |
| **成功次数** | 当日成功执行次数 | 实时统计 | `student_daily_quota.success_count` |
| **失败次数** | 当日失败执行次数 | 实时统计 | `student_daily_quota.error_count` |
| **当日成本** | 学生当日总成本 | 实时统计 | `student_daily_quota.cost_usd` |
| **配额限制** | 每日 Token 限额 | 配置表 | `course_quota_config.daily_token_limit` |

### 2.5 详细日志监控

| 监控项 | 说明 | 数据来源 | 存储表 |
|--------|------|----------|--------|
| **Trace跨度** | 各 Block 执行详情 | Sim API (details=full) | `sim_execution_logs.trace_spans` |
| **最终输出** | 工作流最终输出内容 | Sim API (includeFinalOutput=true) | `sim_execution_logs.final_output` |
| **输入数据** | 传入工作流的输入数据 | Sim API | `sim_execution_logs.input_data` |
| **元数据** | 执行相关的元数据 | Sim API | `sim_execution_logs.metadata` |

## 三、监控实现方案

### 3.1 前端监控（iframe 消息监听）

**位置**：`frontend/src/views/ClassDetailView.vue`

**实现方式**：
1. 在 iframe 加载后，监听来自 Sim 的 postMessage 消息
2. 当 Sim 执行工作流时，通过 postMessage 发送执行状态
3. 前端接收消息后，实时上报到后端

**监控事件**：
- `SIM_EXECUTION_STARTED` - 执行开始
- `SIM_EXECUTION_PROGRESS` - 执行进度
- `SIM_EXECUTION_COMPLETED` - 执行完成
- `SIM_EXECUTION_ERROR` - 执行错误

### 3.2 Webhook 监控（Sim 执行完成通知）

**位置**：`backend/src/main/java/com/eduai/Controller/SimWebhookController.java`

**实现方式**：
1. Sim 平台配置 Webhook，指向 `/api/edu/webhook/sim-execution`
2. 每次执行完成时，Sim 发送 POST 请求
3. 后端验证 HMAC 签名
4. 解析执行数据并保存到数据库

**监控数据**：
- 执行ID、工作流ID、状态、耗时、成本
- Token 使用情况
- 错误信息（如果有）

### 3.3 定时同步监控（Sim API 轮询）

**位置**：`backend/src/main/java/com/eduai/config/SimMonitoringScheduler.java`

**实现方式**：
1. 每 5 分钟执行一次
2. 调用 Sim API：`GET /api/v1/logs`
3. 同步最近 10 分钟的执行日志
4. 获取详细数据（Trace、输出等）

**监控数据**：
- 完整的执行日志
- Trace 跨度信息
- 最终输出内容
- 输入数据

### 3.4 配额检查监控（执行前拦截）

**位置**：`backend/src/main/java/com/eduai/config/QuotaCheckInterceptor.java`

**实现方式**：
1. 拦截所有 Sim 执行请求
2. 从请求头获取学生和课程信息
3. 查询当日配额使用情况
4. 检查是否超限
5. 超限则拒绝执行

**检查内容**：
- 当日 Token 使用量 vs 配额限制
- 当日执行次数
- 成本限制（如果配置）

## 四、数据流转过程

### 4.1 学生开始实验流程

```
1. 学生点击"立即实验"按钮
   ↓
2. 前端创建实验尝试记录（student_experiment_attempts）
   ↓
3. 前端打开 Sim iframe，传递 metadata（studentId, courseId, classId, attemptId）
   ↓
4. 前端监听 iframe 消息，准备接收执行状态
```

### 4.2 执行中监控流程

```
1. 学生在 Sim 中执行工作流
   ↓
2. Sim 开始执行，前端通过 postMessage 接收执行开始事件
   ↓
3. 前端实时上报执行状态到后端（可选，用于实时展示）
   ↓
4. Sim 执行完成，发送 Webhook 通知
   ↓
5. 后端接收 Webhook，保存执行日志和 Token 使用记录
   ↓
6. 后端更新学生每日配额统计
```

### 4.3 定时同步流程

```
1. 定时任务触发（每 5 分钟）
   ↓
2. 调用 Sim API 获取最近 10 分钟的日志
   ↓
3. 解析日志数据，补充详细信息（Trace、输出等）
   ↓
4. 更新 sim_execution_logs 表
```

## 五、关键实现点

### 5.1 如何关联学生和 Sim 执行？

**方案一：通过 metadata 传递**
- 前端在打开 Sim 时，将学生信息写入 localStorage
- Sim 执行时，从 localStorage 读取并放入 metadata
- Webhook 通知中包含 metadata，后端解析

**方案二：通过 workflowId 映射**
- 创建 workflowId 到学生/课程的映射表
- 执行时通过 workflowId 查找关联关系

**推荐方案**：方案一（metadata 传递），更灵活且不依赖额外映射表

### 5.2 如何获取 Token 使用详情？

**方案**：通过 Sim API 查询
```
GET /api/v1/logs?workspaceId={workspaceId}&executionId={executionId}&details=full
```

返回数据包含：
- `tokenUsage.inputTokens`
- `tokenUsage.outputTokens`
- `tokenUsage.totalTokens`
- `cost`

### 5.3 如何实现配额实时检查？

**方案**：拦截器 + 缓存
1. 在拦截器中检查配额
2. 使用 Redis 缓存当日配额使用量（减少数据库查询）
3. 每次执行后更新缓存

## 六、监控数据查询示例

### 6.1 查询学生当日使用情况

```sql
SELECT 
    tokens_used,
    execution_count,
    success_count,
    error_count,
    cost_usd
FROM student_daily_quota
WHERE student_id = 1 
  AND course_id = 1 
  AND date = CURDATE();
```

### 6.2 查询学生所有执行记录

```sql
SELECT 
    execution_id,
    workflow_id,
    status,
    total_tokens,
    cost_usd,
    duration_ms,
    created_at
FROM student_token_usage
WHERE student_id = 1 
  AND course_id = 1
ORDER BY created_at DESC;
```

### 6.3 查询执行详细日志

```sql
SELECT 
    execution_id,
    workflow_name,
    status,
    duration_ms,
    cost_usd,
    trace_spans,
    final_output,
    error_message
FROM sim_execution_logs
WHERE execution_id = 'exec_123456';
```

## 七、告警规则实现

### 7.1 连续失败告警

```java
// 检查最近 N 次执行是否全部失败
List<StudentTokenUsage> recentExecutions = 
    tokenUsageMapper.selectByStudentAndDateRange(
        studentId, courseId, 
        LocalDateTime.now().minusHours(1), 
        LocalDateTime.now()
    );
    
long failureCount = recentExecutions.stream()
    .filter(e -> "error".equals(e.getStatus()))
    .count();
    
if (failureCount >= 3) {
    // 发送告警
}
```

### 7.2 失败率阈值告警

```java
// 检查过去 Y 小时内的失败率
StudentDailyQuota quota = dailyQuotaMapper.selectByStudentCourseAndDate(
    studentId, courseId, LocalDate.now()
);

if (quota.getExecutionCount() > 0) {
    double failureRate = (double) quota.getErrorCount() / quota.getExecutionCount();
    if (failureRate > 0.5) { // 失败率超过 50%
        // 发送告警
    }
}
```

### 7.3 延迟阈值告警

```java
// 检查执行耗时
if (executionLog.getDurationMs() > 30000) { // 超过 30 秒
    // 发送告警
}
```

## 八、监控配置

### 8.1 application.yml 配置

```yaml
sim:
  api:
    url: http://localhost:3000
    key: your-sim-api-key-here
  webhook:
    secret: your-webhook-secret-here
  monitoring:
    # 定时同步间隔（毫秒）
    sync-interval: 300000  # 5 分钟
    # 同步时间范围（分钟）
    sync-time-range: 10
    # 是否启用配额检查
    quota-check-enabled: true
```

### 8.2 Sim 平台 Webhook 配置

1. 登录 Sim 平台
2. Settings → Webhooks → Add Webhook
3. URL: `http://your-backend-url/api/edu/webhook/sim-execution`
4. Events: Execution Completed
5. Secret: 与 `sim.webhook.secret` 保持一致

## 九、监控数据可视化建议

### 9.1 学生端监控面板

- 当日 Token 使用量（进度条）
- 当日执行次数
- 最近执行记录列表
- 配额剩余量

### 9.2 教师端监控面板

- 班级整体使用情况
- 学生使用排名
- 执行成功率统计
- 成本分析报表

### 9.3 管理员端监控面板

- 全平台使用统计
- 异常执行告警
- 配额配置管理
- 成本趋势分析

