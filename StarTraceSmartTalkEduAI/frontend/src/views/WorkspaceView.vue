<template>
  <div class="workspace-page fade-in">
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">互动工作区</h1>
        <p class="page-subtitle">基于 SIM 互动的 AI 智能体编排与实验中心</p>
      </div>
    </div>

    <div class="workspace-layout">
      <!-- Workspace Status Cards -->
      <div class="workspace-grid">
        <div class="work-card">
          <div class="card-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#2563eb" stroke-width="2">
              <path d="M12 2v8M12 14v8M2 12h8M14 12h8M4.93 4.93l5.66 5.66M13.41 13.41l5.66 5.66M19.07 4.93l-5.66 5.66M10.59 13.41l-5.66 5.66"/>
            </svg>
          </div>
          <h3 class="card-name">星溯智语 EduAI Studio</h3>
          <p class="card-info">快速进入基于语音与视觉生成的实时互动实验环境。</p>
          <button class="btn-launch-flat">
            开始实验
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <line x1="5" y1="12" x2="19" y2="12" />
              <polyline points="12 5 19 12 12 19" />
            </svg>
          </button>
        </div>

        <div class="work-card">
          <div class="card-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#2563eb" stroke-width="2">
              <rect x="2" y="3" width="20" height="14" rx="2" ry="2" />
              <line x1="8" y1="21" x2="16" y2="21" />
              <line x1="12" y1="17" x2="12" y2="21" />
            </svg>
          </div>
          <h3 class="card-name">智能体编排</h3>
          <p class="card-info">可视化配置 Agent 的感知、思考与动作逻辑链条。</p>
          <button class="btn-launch-flat secondary">
            配置中心
          </button>
        </div>
      </div>

      <!-- Construction Notice (Flat Box Style) -->
      <div class="construction-frame">
        <div class="notice-box">
          <div class="notice-icon">🏗️</div>
          <div class="notice-text">
            <h3>正在深度开发中</h3>
            <p>后端 SIM (Simulation Integration Module) 正在接入，即将支持跨平台的实时交互体验。</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Logic will be added in Phase 2
</script>

<style scoped>
.workspace-page {
  padding-bottom: 60px;
}

.page-header {
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

.workspace-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.work-card {
  background: white;
  border-radius: 20px;
  border: 1px solid #f1f5f9;
  padding: 32px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.work-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 12px 30px rgba(0,0,0,0.03);
  transform: translateY(-4px);
}

.card-icon {
  width: 48px;
  height: 48px;
  background: #f8fafc;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}

.card-name {
  font-size: 1.25rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 12px;
}

.card-info {
  font-size: 0.95rem;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 32px;
}

.btn-launch-flat {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #2563eb;
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 700;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-launch-flat:hover {
  background: #1d4ed8;
  transform: translateX(4px);
}

.btn-launch-flat.secondary {
  background: #f8fafc;
  color: #475569;
  border: 1px solid #e2e8f0;
}

.btn-launch-flat.secondary:hover {
  background: #f1f5f9;
  color: #1e293b;
  transform: none;
}

/* Construction Frame - Flat Square Style */
.construction-frame {
  margin-top: 40px;
}

.notice-box {
  background: #f8fafc;
  border: 1px dashed #cbd5e1;
  border-radius: 16px;
  padding: 32px;
  display: flex;
  align-items: center;
  gap: 24px;
}

.notice-icon {
  font-size: 2.5rem;
}

.notice-text h3 {
  font-size: 1.1rem;
  font-weight: 800;
  color: #475569;
  margin-bottom: 4px;
}

.notice-text p {
  font-size: 0.9rem;
  color: #94a3b8;
}
</style>

