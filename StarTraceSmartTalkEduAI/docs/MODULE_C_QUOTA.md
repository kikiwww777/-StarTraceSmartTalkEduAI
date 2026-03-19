# 模块C：成本与配额管理 - 详细设计文档

## 📋 模块概述

成本与配额管理模块用于控制和监控AI API的使用成本，确保教学活动在预算范围内进行。

---

## 🎯 核心功能

### C-1. 配额设置（教师功能）

**功能描述**：
- 教师为课程设置Token配额上限
- 支持每日/每周/总量限制
- 可为不同学生设置不同配额

**UI设计**：
```
┌─────────────────────────────────────────────────────────────┐
│  ⚙️ 配额设置 - 人工智能导论                                   │
│                                                               │
│  ┌─ 课程级配额 ─────────────────────────────────────────────┐│
│  │                                                           ││
│  │  每日Token上限:     [10,000    ] tokens                  ││
│  │  每周Token上限:     [50,000    ] tokens                  ││
│  │  课程总量上限:      [500,000   ] tokens  (0=无限)        ││
│  │                                                           ││
│  │  周重置日:          [周一 ▼]                              ││
│  │                                                           ││
│  │  [保存设置]                                               ││
│  │                                                           ││
│  └───────────────────────────────────────────────────────────┘│
│                                                               │
│  ┌─ 个人配额调整 ───────────────────────────────────────────┐│
│  │                                                           ││
│  │  学号        姓名      每日限额    每周限额    操作       ││
│  │  ─────────────────────────────────────────────────────── ││
│  │  2021001    张三      10,000      50,000     [编辑]     ││
│  │  2021002    李四      15,000      70,000     [编辑] ✨   ││
│  │  2021003    王五      10,000      50,000     [编辑]     ││
│  │  ...                                                      ││
│  │                                                           ││
│  │  ✨ 表示有个人配额调整                                    ││
│  │                                                           ││
│  └───────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

---

### C-2. 使用统计仪表板

**功能描述**：
- 实时显示课程Token使用情况
- 按时间/学生/模板维度统计
- 成本估算和趋势图表

**UI设计**：
```
┌─────────────────────────────────────────────────────────────┐
│  📊 使用统计 - 人工智能导论                                   │
│                                                               │
│  时间范围: [今日] [本周] [本月] [全部]    导出报告 📥         │
│                                                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐│
│  │ 📈 今日使用     │  │ 📈 本周使用     │  │ 💰 预估成本     ││
│  │                 │  │                 │  │                 ││
│  │    45,320      │  │    187,450     │  │    ¥18.75       ││
│  │    tokens       │  │    tokens       │  │    本周         ││
│  │                 │  │                 │  │                 ││
│  │  ▓▓▓▓▓░░░░ 45% │  │  ▓▓▓░░░░░░ 37% │  │  +12% vs 上周   ││
│  └─────────────────┘  └─────────────────┘  └─────────────────┘│
│                                                               │
│  ┌─ 使用趋势图 ─────────────────────────────────────────────┐│
│  │                                                           ││
│  │  tokens                                                   ││
│  │  50k ┤                                          ░        ││
│  │  40k ┤              ▓                    ▓     ▓▓        ││
│  │  30k ┤        ▓    ▓▓▓        ▓    ▓    ▓▓    ▓▓▓       ││
│  │  20k ┤  ▓    ▓▓▓  ▓▓▓▓   ▓  ▓▓▓  ▓▓▓  ▓▓▓   ▓▓▓▓       ││
│  │  10k ┤ ▓▓▓  ▓▓▓▓  ▓▓▓▓  ▓▓▓ ▓▓▓▓ ▓▓▓▓ ▓▓▓▓  ▓▓▓▓▓      ││
│  │   0  ┼──┴────┴────┴────┴────┴────┴────┴────┴────┴──     ││
│  │       周一  周二  周三  周四  周五  周六  周日  今天       ││
│  │                                                           ││
│  └───────────────────────────────────────────────────────────┘│
│                                                               │
│  ┌─ 使用排行 ───────────────────────────────────────────────┐│
│  │                                                           ││
│  │  排名   学号       姓名     今日使用    本周使用    占比  ││
│  │  ─────────────────────────────────────────────────────── ││
│  │  1     2021005    赵六     8,234       42,150      22%  ││
│  │  2     2021002    李四     6,120       35,890      19%  ││
│  │  3     2021001    张三     5,890       28,450      15%  ││
│  │  ...                                                      ││
│  │                                                           ││
│  └───────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

---

### C-3. 个人使用统计（学生视角）

**功能描述**：
- 查看个人配额使用情况
- 剩余配额提醒
- 使用历史记录

**UI设计**：
```
┌─────────────────────────────────────────────────────────────┐
│  📊 我的使用情况                                             │
│                                                               │
│  ┌─ 配额状态 ───────────────────────────────────────────────┐│
│  │                                                           ││
│  │  今日剩余           本周剩余            总量剩余          ││
│  │                                                           ││
│  │   5,680            32,150             456,780            ││
│  │   tokens            tokens             tokens            ││
│  │                                                           ││
│  │  ▓▓▓▓▓▓░░░░ 57%   ▓▓▓▓▓▓░░░░ 64%    ▓▓▓▓▓▓▓▓▓░ 91%    ││
│  │                                                           ││
│  └───────────────────────────────────────────────────────────┘│
│                                                               │
│  ┌─ 最近使用记录 ───────────────────────────────────────────┐│
│  │                                                           ││
│  │  时间              模板            输入      输出     合计││
│  │  ─────────────────────────────────────────────────────── ││
│  │  14:32:15         代码调试助手     450       820     1,270││
│  │  14:28:03         智能辅导助手     320       680     1,000││
│  │  13:45:22         作业评分助手     890      1,450    2,340││
│  │  ...                                                      ││
│  │                                                           ││
│  └───────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

---

### C-4. 配额超限处理

**流程描述**：
1. 用户发起请求前检查配额
2. 如果配额不足，返回友好提示
3. 教师可临时提升学生配额
4. 定时任务重置每日/每周配额

**超限提示UI**：
```
┌─────────────────────────────────────────────────────────────┐
│  ⚠️ 配额不足                                                 │
│                                                               │
│  您今日的Token配额已用完。                                    │
│                                                               │
│  • 今日已使用: 10,000 / 10,000 tokens                        │
│  • 重置时间: 明天 00:00                                       │
│                                                               │
│  您可以：                                                     │
│  1. 等待配额重置                                              │
│  2. 联系教师申请临时配额                                      │
│                                                               │
│  [我知道了]                                                   │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

---

## 🗄️ 数据库设计

### quota_settings 表（配额设置）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(36) | 主键 |
| course_id | VARCHAR(36) | 课程ID |
| daily_limit | INT | 每日Token上限 |
| weekly_limit | INT | 每周Token上限 |
| total_limit | INT | 总量上限(0=无限) |
| reset_day | ENUM | 周重置日(monday/sunday) |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### user_quota_override 表（个人配额调整）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(36) | 主键 |
| course_id | VARCHAR(36) | 课程ID |
| user_id | VARCHAR(36) | 用户ID |
| daily_limit | INT | 个人每日上限 |
| weekly_limit | INT | 个人每周上限 |
| reason | VARCHAR(255) | 调整原因 |
| created_by | VARCHAR(36) | 设置人ID |
| created_at | DATETIME | 创建时间 |

### quota_usage 表（配额使用记录）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(36) | 主键 |
| user_id | VARCHAR(36) | 用户ID |
| course_id | VARCHAR(36) | 课程ID |
| date | DATE | 日期 |
| tokens_used | INT | 已使用Token数 |
| request_count | INT | 请求次数 |
| UNIQUE KEY | | (user_id, course_id, date) |

### execution_logs 表（执行日志）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(36) | 主键 |
| user_id | VARCHAR(36) | 用户ID |
| course_id | VARCHAR(36) | 课程ID |
| template_id | VARCHAR(36) | 模板ID |
| model_name | VARCHAR(100) | 模型名称 |
| input_tokens | INT | 输入Token数 |
| output_tokens | INT | 输出Token数 |
| total_tokens | INT | 总Token数 |
| cost_usd | DECIMAL(10,6) | 成本(USD) |
| duration_ms | INT | 执行时长(ms) |
| executed_at | DATETIME | 执行时间 |

---

## 🔧 后端API

### GET /api/quota/settings/{courseId}
获取课程配额设置

```java
@GetMapping("/settings/{courseId}")
public Result<QuotaSettingsDTO> getQuotaSettings(
        @PathVariable String courseId,
        @RequestHeader("X-User-Id") String userId) {
    // 检查教师权限
    QuotaSettingsDTO settings = quotaService.getSettings(courseId, userId);
    return Result.ok(settings);
}
```

### PUT /api/quota/settings/{courseId}
更新课程配额设置

```java
@PutMapping("/settings/{courseId}")
public Result<Void> updateQuotaSettings(
        @PathVariable String courseId,
        @RequestBody UpdateQuotaDTO dto,
        @RequestHeader("X-User-Id") String userId) {
    quotaService.updateSettings(courseId, dto, userId);
    return Result.ok(null, "设置已保存");
}
```

### GET /api/quota/usage/{courseId}
获取课程使用统计

```java
@GetMapping("/usage/{courseId}")
public Result<CourseUsageDTO> getCourseUsage(
        @PathVariable String courseId,
        @RequestParam(defaultValue = "week") String period,
        @RequestHeader("X-User-Id") String userId) {
    CourseUsageDTO usage = quotaService.getCourseUsage(courseId, period, userId);
    return Result.ok(usage);
}
```

### GET /api/quota/my-usage
获取个人使用情况

```java
@GetMapping("/my-usage")
public Result<MyUsageDTO> getMyUsage(
        @RequestParam String courseId,
        @RequestHeader("X-User-Id") String userId) {
    MyUsageDTO usage = quotaService.getMyUsage(courseId, userId);
    return Result.ok(usage);
}
```

### POST /api/quota/check
检查配额（执行前调用）

```java
@PostMapping("/check")
public Result<QuotaCheckResult> checkQuota(
        @RequestBody QuotaCheckDTO dto,
        @RequestHeader("X-User-Id") String userId) {
    QuotaCheckResult result = quotaService.checkQuota(
        dto.getCourseId(), userId, dto.getEstimatedTokens()
    );
    return Result.ok(result);
}

// QuotaCheckResult
public class QuotaCheckResult {
    private boolean allowed;
    private int remainingDaily;
    private int remainingWeekly;
    private String message;
}
```

### POST /api/quota/record
记录使用（执行后调用）

```java
@PostMapping("/record")
public Result<Void> recordUsage(
        @RequestBody RecordUsageDTO dto,
        @RequestHeader("X-User-Id") String userId) {
    quotaService.recordUsage(dto, userId);
    return Result.ok(null);
}
```

---

## 📱 前端组件

### QuotaSettingsView.vue
```vue
<template>
  <div class="quota-settings-page">
    <div class="page-header">
      <h1>⚙️ 配额设置</h1>
      <p class="course-name">{{ course?.name }}</p>
    </div>
    
    <!-- 课程级配额 -->
    <section class="settings-section">
      <h2>课程配额</h2>
      <div class="settings-form">
        <div class="form-group">
          <label>每日Token上限</label>
          <div class="input-with-unit">
            <input type="number" v-model="settings.dailyLimit" min="0" />
            <span>tokens</span>
          </div>
        </div>
        <div class="form-group">
          <label>每周Token上限</label>
          <div class="input-with-unit">
            <input type="number" v-model="settings.weeklyLimit" min="0" />
            <span>tokens</span>
          </div>
        </div>
        <div class="form-group">
          <label>课程总量上限</label>
          <div class="input-with-unit">
            <input type="number" v-model="settings.totalLimit" min="0" />
            <span>tokens (0=无限)</span>
          </div>
        </div>
        <div class="form-group">
          <label>周重置日</label>
          <select v-model="settings.resetDay">
            <option value="monday">周一</option>
            <option value="sunday">周日</option>
          </select>
        </div>
        <button @click="saveSettings" :disabled="saving">
          {{ saving ? '保存中...' : '保存设置' }}
        </button>
      </div>
    </section>
    
    <!-- 学生配额列表 -->
    <section class="students-section">
      <h2>学生配额</h2>
      <table class="students-table">
        <thead>
          <tr>
            <th>学号</th>
            <th>姓名</th>
            <th>每日限额</th>
            <th>今日已用</th>
            <th>本周已用</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="student in students" :key="student.userId">
            <td>{{ student.studentId }}</td>
            <td>{{ student.name }}</td>
            <td>
              <span v-if="student.hasOverride" class="custom-badge">自定义</span>
              {{ student.dailyLimit.toLocaleString() }}
            </td>
            <td>{{ student.dailyUsed.toLocaleString() }}</td>
            <td>{{ student.weeklyUsed.toLocaleString() }}</td>
            <td>
              <button @click="editStudentQuota(student)">编辑</button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>
    
    <!-- 编辑学生配额弹窗 -->
    <div v-if="showEditModal" class="modal-overlay">
      <div class="modal-content">
        <h3>编辑 {{ editingStudent?.name }} 的配额</h3>
        <div class="form-group">
          <label>每日限额</label>
          <input type="number" v-model="editForm.dailyLimit" />
        </div>
        <div class="form-group">
          <label>每周限额</label>
          <input type="number" v-model="editForm.weeklyLimit" />
        </div>
        <div class="form-group">
          <label>调整原因</label>
          <input type="text" v-model="editForm.reason" placeholder="可选" />
        </div>
        <div class="form-actions">
          <button @click="resetToDefault">恢复默认</button>
          <button @click="showEditModal = false">取消</button>
          <button @click="saveStudentQuota" class="primary">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>
```

### QuotaDisplay.vue（配额显示组件）
```vue
<template>
  <div class="quota-display">
    <div class="quota-item">
      <div class="label">今日配额</div>
      <div class="progress-bar">
        <div class="progress" :style="{ width: dailyPercent + '%' }" :class="getColorClass(dailyPercent)"></div>
      </div>
      <div class="numbers">
        {{ formatNumber(usage.dailyUsed) }} / {{ formatNumber(usage.dailyLimit) }}
      </div>
    </div>
    <div class="quota-item">
      <div class="label">本周配额</div>
      <div class="progress-bar">
        <div class="progress" :style="{ width: weeklyPercent + '%' }" :class="getColorClass(weeklyPercent)"></div>
      </div>
      <div class="numbers">
        {{ formatNumber(usage.weeklyUsed) }} / {{ formatNumber(usage.weeklyLimit) }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

interface Usage {
  dailyUsed: number;
  dailyLimit: number;
  weeklyUsed: number;
  weeklyLimit: number;
}

const props = defineProps<{ usage: Usage }>();

const dailyPercent = computed(() => 
  Math.min(100, (props.usage.dailyUsed / props.usage.dailyLimit) * 100)
);

const weeklyPercent = computed(() => 
  Math.min(100, (props.usage.weeklyUsed / props.usage.weeklyLimit) * 100)
);

const getColorClass = (percent: number) => {
  if (percent >= 90) return 'danger';
  if (percent >= 70) return 'warning';
  return 'normal';
};

const formatNumber = (n: number) => n.toLocaleString();
</script>

<style scoped>
.quota-display {
  display: flex;
  gap: 20px;
}

.quota-item {
  flex: 1;
}

.progress-bar {
  height: 8px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  overflow: hidden;
  margin: 8px 0;
}

.progress {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s;
}

.progress.normal { background: #22c55e; }
.progress.warning { background: #eab308; }
.progress.danger { background: #ef4444; }

.numbers {
  font-size: 12px;
  color: rgba(232, 237, 247, 0.6);
}
</style>
```

---

## ✅ 测试用例

| 编号 | 场景 | 预期结果 |
|------|------|----------|
| C-01 | 设置课程配额 | 配额保存成功 |
| C-02 | 设置个人配额 | 覆盖默认配额 |
| C-03 | 使用检查-配额充足 | 返回allowed=true |
| C-04 | 使用检查-配额不足 | 返回allowed=false |
| C-05 | 记录使用 | 正确更新使用量 |
| C-06 | 每日重置 | 凌晨自动重置每日用量 |
| C-07 | 每周重置 | 正确日期重置周用量 |
| C-08 | 使用统计-教师 | 显示全班使用情况 |
| C-09 | 使用统计-学生 | 只显示个人使用 |
| C-10 | 导出报告 | 生成Excel/CSV |
