<template>
  <div class="reports-page fade-in">
    <!-- Header Section -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">教学报告中心</h1>
        <p class="page-subtitle">多维度分析 AI 互动数据，量化学生学情与教学成效。</p>
      </div>
      <div class="header-right">
        <button class="btn-outline-flat">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
            <polyline points="7 10 12 15 17 10" />
            <line x1="12" y1="15" x2="12" y2="3" />
          </svg>
          导出总报表
        </button>
      </div>
    </div>

    <!-- Stats Grid -->
    <div class="stats-grid">
      <div v-for="stat in keyStats" :key="stat.label" class="stat-card">
        <div class="stat-icon" :style="{ backgroundColor: stat.color + '15', color: stat.color }">
          <div v-html="stat.icon"></div>
        </div>
        <div class="stat-info">
          <p class="stat-label">{{ stat.label }}</p>
          <p class="stat-value">{{ stat.value }}</p>
          <div class="stat-trend" :class="stat.trend > 0 ? 'up' : 'down'">
            <span>{{ stat.trend > 0 ? '↑' : '↓' }} {{ Math.abs(stat.trend) }}%</span>
            <span class="trend-text">较上周</span>
          </div>
        </div>
      </div>
    </div>

    <div class="reports-layout">
      <!-- Main Chart Area -->
      <div class="chart-section academic-box">
        <div class="section-header">
          <h2 class="section-title">互动频率趋势</h2>
          <div class="chart-legend">
            <span class="legend-item"><i class="dot blue"></i> AI 互动</span>
            <span class="legend-item"><i class="dot slate"></i> 平均基准</span>
          </div>
        </div>
        <div class="mock-chart-container">
          <!-- Simple CSS Grid Based Chart for visualization -->
          <div class="chart-bars">
            <div 
              v-for="(val, index) in trendData" 
              :key="index" 
              class="bar-wrapper"
            >
              <div class="bar-fill" :style="{ height: val + '%' }">
                <span class="bar-tooltip">{{ val }} 次</span>
              </div>
              <span class="bar-label">Day {{ index + 1 }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Recent Reports List -->
      <div class="recent-list-section academic-box">
        <div class="section-header">
          <h2 class="section-title">最近分析报告</h2>
          <button class="btn-text">查看全部</button>
        </div>
        <div class="reports-list">
          <div v-for="report in recentReports" :key="report.id" class="report-row">
            <div class="report-main">
              <div class="report-icon-mini">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" /><polyline points="14 2 14 8 20 8" />
                </svg>
              </div>
              <div class="report-details">
                <h4 class="report-name">{{ report.name }}</h4>
                <p class="report-meta">{{ report.course }} · {{ report.date }}</p>
              </div>
            </div>
            <div class="report-score">
              <span class="score-badge" :class="getScoreClass(report.score)">{{ report.score }}</span>
            </div>
            <button class="btn-view-mini">分析明细</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const keyStats = [
  {
    label: '总互动人次',
    value: '1,280',
    trend: 12.5,
    color: '#2563eb',
    icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>`
  },
  {
    label: '平均互动时长',
    value: '24.5 min',
    trend: 8.2,
    color: '#059669',
    icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>`
  },
  {
    label: 'AI 问题解决率',
    value: '94.2%',
    trend: 2.1,
    color: '#7c3aed',
    icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 11.08 11.12 22 2 12.88 5.64 9.24 11.12 14.77 18.36 7.53 22 11.08"/></svg>`
  }
];

const trendData = [45, 62, 58, 75, 90, 82, 95];

const recentReports = [
  { id: 1, name: '计算数学：微积分交互实验分析', course: 'CS101 高等数学', date: '今天 10:30', score: 'A+' },
  { id: 2, name: '深度学习：损失函数可视化讨论', course: 'AI502 机器学习', date: '昨天 15:45', score: 'A' },
  { id: 3, name: '程序设计：递归逻辑推演报告', course: 'CS204 数据结构', date: '2024-02-05', score: 'B+' },
  { id: 4, name: '提示词工程：结构化 Prompt 实验报告', course: 'EDU201 AI教育应用', date: '2024-02-04', score: 'A' }
];

const getScoreClass = (score: string) => {
  if (score.startsWith('A')) return 'score-high';
  if (score.startsWith('B')) return 'score-medium';
  return 'score-normal';
};
</script>

<style scoped>
.reports-page {
  padding-bottom: 60px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 40px;
}

.page-title {
  font-size: 2rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 8px;
}

.page-subtitle {
  color: #64748b;
  font-size: 0.95rem;
}

.btn-outline-flat {
  display: flex;
  align-items: center;
  gap: 8px;
  background: white;
  color: #475569;
  border: 1px solid #e2e8f0;
  padding: 10px 20px;
  border-radius: 12px;
  font-weight: 700;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-outline-flat:hover {
  background: #f8fafc;
  color: #1e293b;
  border-color: #cbd5e1;
}

/* Stats */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border: 1px solid #f1f5f9;
  border-radius: 20px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-label {
  font-size: 0.85rem;
  font-weight: 700;
  color: #94a3b8;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 6px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.75rem;
  font-weight: 700;
}

.stat-trend.up { color: #10b981; }
.stat-trend.down { color: #ef4444; }

.trend-text {
  color: #94a3b8;
  font-weight: 600;
}

/* Layout */
.reports-layout {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 32px;
}

.academic-box {
  background: white;
  border: 1px solid #f1f5f9;
  border-radius: 24px;
  padding: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.section-title {
  font-size: 1.1rem;
  font-weight: 800;
  color: #1e293b;
}

/* Chart */
.chart-legend {
  display: flex;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.75rem;
  font-weight: 700;
  color: #64748b;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.dot.blue { background: #3b82f6; }
.dot.slate { background: #e2e8f0; }

.mock-chart-container {
  height: 300px;
  display: flex;
  align-items: flex-end;
  padding-bottom: 20px;
}

.chart-bars {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 240px;
}

.bar-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.bar-fill {
  width: 32px;
  background: #3b82f6;
  border-radius: 6px 6px 0 0;
  position: relative;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.bar-fill:hover {
  background: #2563eb;
  transform: scaleX(1.1);
}

.bar-tooltip {
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  background: #1e293b;
  color: white;
  font-size: 0.7rem;
  padding: 4px 8px;
  border-radius: 4px;
  opacity: 0;
  transition: opacity 0.2s;
  pointer-events: none;
}

.bar-fill:hover .bar-tooltip {
  opacity: 1;
}

.bar-label {
  font-size: 0.7rem;
  font-weight: 700;
  color: #94a3b8;
}

/* List */
.reports-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.report-row {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 16px;
  transition: background 0.2s;
}

.report-row:hover {
  background: #f8fafc;
}

.report-main {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
}

.report-icon-mini {
  width: 36px;
  height: 36px;
  background: #f1f5f9;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
}

.report-name {
  font-size: 0.9rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 2px;
}

.report-meta {
  font-size: 0.75rem;
  color: #94a3b8;
}

.score-badge {
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 800;
}

.score-high { background: #ecfdf5; color: #059669; }
.score-medium { background: #fefce8; color: #ca8a04; }
.score-normal { background: #f8fafc; color: #64748b; }

.btn-view-mini {
  background: none;
  border: none;
  font-size: 0.8rem;
  font-weight: 700;
  color: #2563eb;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s;
}

.report-row:hover .btn-view-mini {
  opacity: 1;
}

.btn-text {
  background: none;
  border: none;
  color: #64748b;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
}

.btn-text:hover { color: #1e293b; }

@media (max-width: 1100px) {
  .reports-layout {
    grid-template-columns: 1fr;
  }
}
</style>
