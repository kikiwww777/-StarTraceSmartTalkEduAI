<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";
import { useAuth } from "../composables/useAuth";

const { currentUser } = useAuth();

// Stats Data
const tokenUsage = ref({
  current: 34500,
  max: 100000,
  percent: 34.5
});

const activeAgents = ref({
  count: 12,
  avatars: [
    "https://ui-avatars.com/api/?name=Agent+A&background=random",
    "https://ui-avatars.com/api/?name=Agent+B&background=random",
    "https://ui-avatars.com/api/?name=Agent+C&background=random"
  ]
});

const activeCourses = ref({
  count: 3,
  students: 253
});

// Execution Log
const recentLogs = ref([
  { id: 1, title: "文献综述生成", time: "10 分钟前", status: "成功", cost: "12k", statusColor: "green" },
  { id: 2, title: "作业自动批改", time: "2 小时前", status: "成功", cost: "3k", statusColor: "green" },
  { id: 3, title: "知识图谱构建", time: "昨日", status: "失败", cost: "0", statusColor: "red" }
]);

const formatNumber = (num: number) => {
  return num.toLocaleString();
};
</script>

<template>
  <div class="dashboard-container fade-in">
    <!-- Header -->
    <header class="dashboard-header">
      <h1 class="welcome-text">欢迎回来，{{ currentUser?.name || '张教授' }}</h1>
      <p class="sub-text">这是您今日的教学与智能体运行概况</p>
    </header>

    <!-- Stats Cards Row -->
    <div class="stats-row">
      <!-- Card 1: Token Usage -->
      <div class="stat-card">
        <div class="card-top">
          <span class="card-label">今日 TOKEN 消耗</span>
          <span class="usage-badge">已用 {{ tokenUsage.percent }}%</span>
        </div>
        <div class="card-main">
          <div class="big-number">{{ formatNumber(tokenUsage.current) }}</div>
        </div>
        <div class="card-bottom">
          <div class="progress-bar-bg">
            <div class="progress-bar-fill" :style="{ width: tokenUsage.percent + '%' }"></div>
          </div>
          <div class="limit-text">每日上限: {{ formatNumber(tokenUsage.max) }} tokens</div>
        </div>
      </div>

      <!-- Card 2: Active Agents -->
      <div class="stat-card">
        <div class="card-top">
          <span class="card-label">活跃智能体</span>
        </div>
        <div class="card-main">
          <div class="big-number">{{ activeAgents.count }}</div>
        </div>
        <div class="card-bottom">
          <div class="agent-info">
            <span class="info-text">过去 24 小时内有响应</span>
            <div class="avatar-stack">
              <img v-for="(src, index) in activeAgents.avatars" :key="index" :src="src" class="stack-avatar" />
              <div class="stack-more">+8</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Card 3: Active Courses -->
      <div class="stat-card">
        <div class="card-top">
          <span class="card-label">进行中的课程</span>
        </div>
        <div class="card-main">
          <div class="big-number">{{ activeCourses.count }}</div>
        </div>
        <div class="card-bottom course-bottom">
          <span class="info-text">涉及 {{ activeCourses.students }} 名学生</span>
          <button class="btn-view">查看详情</button>
        </div>
      </div>
    </div>

    <!-- Recent Execution Log -->
    <div class="section-container">
      <h3 class="section-title">最近执行记录</h3>
      <div class="log-list">
        <div v-for="log in recentLogs" :key="log.id" class="log-row">
          <div class="log-left">
            <div class="log-title">{{ log.title }}</div>
            <div class="log-time">{{ log.time }}</div>
          </div>
          <div class="log-right">
            <div class="log-status" :class="log.statusColor">{{ log.status }}</div>
            <div class="log-cost">消耗 {{ log.cost }}</div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 32px;
  position: relative;
  min-height: 85vh;
}

/* Header */
.dashboard-header {
  margin-bottom: 8px;
}

.welcome-text {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-main);
  margin-bottom: 8px;
}

.sub-text {
  color: var(--text-secondary);
  font-size: 0.95rem;
}

/* Stats Cards */
.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.stat-card {
  background: white;
  border: 1px solid var(--border-subtle);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 200px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.02);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.06);
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-label {
  color: var(--text-secondary);
  font-weight: 600;
  font-size: 0.9rem;
}

.usage-badge {
  background: #eff6ff;
  color: #3b82f6;
  font-size: 0.75rem;
  padding: 4px 8px;
  border-radius: 4px;
  font-weight: 600;
}

.card-main {
  flex: 1;
  display: flex;
  align-items: center;
}

.big-number {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--text-main);
}

.card-bottom {
  margin-top: auto;
}

/* Progress Bar */
.progress-bar-bg {
  width: 60%;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  margin-bottom: 8px;
  overflow: hidden;
}

.progress-bar-fill {
  background: #3b82f6;
  height: 100%;
  border-radius: 3px;
}

.limit-text {
  font-size: 0.8rem;
  color: var(--text-muted);
}

/* Agent Avatar Stack */
.agent-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-text {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.avatar-stack {
  display: flex;
  align-items: center;
}

.stack-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 2px solid white;
  margin-left: -10px;
}

.stack-avatar:first-child {
  margin-left: 0;
}

.stack-more {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f1f5f9;
  border: 2px solid white;
  margin-left: -10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  color: var(--text-secondary);
  font-weight: 600;
}

/* Course Bottom */
.course-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.btn-view {
  background: #f8fafc;
  border: 1px solid var(--border-subtle);
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  color: var(--text-secondary);
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-view:hover {
  background: #f1f5f9;
  color: var(--text-main);
}

/* Recent Logs */
.section-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-main);
}

.log-list {
  background: white;
  border-radius: 16px;
  border: 1px solid var(--border-subtle);
  overflow: hidden;
}

.log-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #f1f5f9;
  transition: background 0.1s;
}

.log-row:last-child {
  border-bottom: none;
}

.log-row:hover {
  background: #f8fafc;
}

.log-title {
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 4px;
}

.log-time {
  font-size: 0.8rem;
  color: var(--text-muted);
}

.log-right {
  text-align: right;
}

.log-status {
  font-weight: 600;
  font-size: 0.9rem;
  margin-bottom: 4px;
}
.log-status.green { color: #10b981; }
.log-status.red { color: #ef4444; }

.log-cost {
  font-size: 0.8rem;
  color: var(--text-muted);
}


@media (max-width: 1024px) {
  .stats-row {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
}
</style>
