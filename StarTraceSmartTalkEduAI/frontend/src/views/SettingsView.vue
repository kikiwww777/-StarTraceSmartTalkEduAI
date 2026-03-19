<template>
  <div class="settings-page fade-in">
    <!-- Header Section -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">系统设置</h1>
        <p class="page-subtitle">管理您的个人资料、AI 引擎集成及平台偏好设置。</p>
      </div>
    </div>

    <div class="settings-container academic-box">
      <!-- Sidebar Tabs -->
      <aside class="settings-sidebar">
        <button 
          v-for="tab in tabs" 
          :key="tab.id"
          class="sidebar-tab"
          :class="{ active: activeTab === tab.id }"
          @click="activeTab = tab.id"
        >
          <div class="tab-icon" v-html="tab.icon"></div>
          <span>{{ tab.label }}</span>
        </button>
        
        <div class="sidebar-spacer"></div>
        
        <button class="sidebar-tab danger" @click="handleLogout">
          <div class="tab-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
              <polyline points="16 17 21 12 16 7" />
              <line x1="21" y1="12" x2="9" y2="12" />
            </svg>
          </div>
          <span>退出登录</span>
        </button>
      </aside>

      <!-- Main Content Area -->
      <main class="settings-main">
        <!-- Profile Section -->
        <section v-if="activeTab === 'profile'" class="settings-section">
          <h2 class="section-title">个人资料</h2>
          <div class="form-group avatar-upload">
            <div class="current-avatar">
              <img :src="userDisplay.avatar" alt="Avatar" />
              <button class="btn-avatar-edit">更换头像</button>
            </div>
          </div>
          <div class="form-grid">
            <div class="form-item">
              <label>显示名称</label>
              <input type="text" v-model="userDisplay.name" placeholder="您的姓名" />
            </div>
            <div class="form-item">
              <label>电子邮箱</label>
              <input type="email" :value="userDisplay.email" disabled />
              <p class="form-help">不可更改，用于系统登录与配额管理。</p>
            </div>
            <div class="form-item">
              <label>当前权限</label>
              <div class="role-badge">{{ userDisplay.role.toUpperCase() }}</div>
            </div>
          </div>
        </section>

        <!-- AI Engine Section -->
        <section v-if="activeTab === 'engine'" class="settings-section">
          <h2 class="section-title">AI 引擎配置 (SIM)</h2>
          <div class="config-card warning">
            <div class="card-icon">⚠️</div>
            <div class="card-text">
              <h3>SIM 后端服务状态</h3>
              <p>当前处于“离线/模拟模式”。真实接口联调中，配置项暂时无法持久化。</p>
            </div>
          </div>
          <div class="form-grid mt-lg">
            <div class="form-item full">
              <label>SIM Server URL</label>
              <input type="text" v-model="engine.apiUrl" placeholder="https://api.startrace.edu/sim" />
            </div>
            <div class="form-item">
              <label>OpenAI API Key</label>
              <div class="input-with-action">
                <input :type="showKey ? 'text' : 'password'" v-model="engine.apiKey" />
                <button class="btn-toggle" @click="showKey = !showKey">
                  {{ showKey ? '隐藏' : '显示' }}
                </button>
              </div>
            </div>
            <div class="form-item">
              <label>默认推理模型</label>
              <select v-model="engine.defaultModel">
                <option value="gpt-4o">GPT-4o</option>
                <option value="claude-3-sonnet">Claude 3.5 Sonnet</option>
                <option value="gemini-1.5-pro">Gemini 1.5 Pro</option>
              </select>
            </div>
          </div>
        </section>

        <!-- Preferences Section -->
        <section v-if="activeTab === 'prefs'" class="settings-section">
          <h2 class="section-title">界面偏好</h2>
          <div class="form-grid">
            <div class="form-item">
              <label>外观主题</label>
              <div class="theme-selector">
                <button class="theme-btn active">Academic Light</button>
                <button class="theme-btn disabled" title="即将上线">Nebula Dark</button>
              </div>
            </div>
            <div class="form-item">
              <label>系统语言</label>
              <select v-model="prefs.lang">
                <option value="zh">简体中文</option>
                <option value="en">English</option>
              </select>
            </div>
          </div>
        </section>

        <!-- Footer Actions -->
        <div class="settings-footer">
          <button class="btn-save" @click="saveSettings">保存更改</button>
          <button class="btn-cancel">重置</button>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuth } from '../composables/useAuth';

const { currentUser, userRole, logout } = useAuth();
const router = useRouter();
const activeTab = ref('profile');
const showKey = ref(false);

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    logout();
    router.push('/login');
  }
};

const userDisplay = computed(() => ({
  name: currentUser.value?.name || '未登录用户',
  email: currentUser.value?.email || 'no-email@edu.ai',
  role: userRole.value || 'guest',
  avatar: currentUser.value?.avatar || `https://ui-avatars.com/api/?name=${currentUser.value?.name || 'U'}&background=2563eb&color=fff&rounded=true`
}));

const tabs = [
  { id: 'profile', label: '个人资料', icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>` },
  { id: 'engine', label: 'AI 引擎', icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>` },
  { id: 'prefs', label: '偏好设置', icon: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/><circle cx="12" cy="12" r="3"/></svg>` }
];

// Mock data for engine settings (currently not in useAuth)
const engine = ref({
  apiUrl: 'https://sim.startrace.ai/v1',
  apiKey: 'sk-............................abcd',
  defaultModel: 'claude-3-sonnet'
});

const prefs = ref({
  lang: 'zh'
});

const saveSettings = () => {
  alert('设置已保存（模拟逻辑）');
};
</script>

<style scoped>
.settings-page {
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

.settings-container {
  display: grid;
  grid-template-columns: 200px 1fr;
  min-height: 600px;
  background: white;
  border: 1px solid #f1f5f9;
  border-radius: 20px;
  overflow: hidden;
}

/* Sidebar */
.settings-sidebar {
  background: white; /* Contrast with App Sidebar */
  padding: 32px 12px;
  border-right: 1px solid #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.sidebar-spacer {
  flex: 1;
}

.sidebar-tab.danger {
  color: #ef4444;
  margin-top: auto;
}

.sidebar-tab.danger:hover {
  background: #fef2f2;
}

.sidebar-tab {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  border-radius: 10px;
  border: none;
  background: transparent;
  color: #94a3b8;
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}

.sidebar-tab:hover {
  background: #f8fafc;
  color: #475569;
}

.sidebar-tab.active {
  background: #eff6ff;
  color: #2563eb;
}

.tab-icon {
  display: flex;
  align-items: center;
}

/* Main Content */
.settings-main {
  padding: 48px;
  max-width: 800px;
}

.settings-section {
  animation: fadeInSlide 0.4s ease;
}

@keyframes fadeInSlide {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.section-title {
  font-size: 1.25rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 32px;
}

.form-grid {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item.full {
  grid-column: span 2;
}

.form-item label {
  font-size: 0.85rem;
  font-weight: 800;
  color: #475569;
}

.form-item input, 
.form-item select {
  padding: 12px 16px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  font-size: 0.95rem;
  outline: none;
  transition: all 0.2s;
}

.form-item input:focus, 
.form-item select:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.05);
}

.form-item input:disabled {
  background: #f8fafc;
  color: #94a3b8;
  cursor: not-allowed;
}

.form-help {
  font-size: 0.75rem;
  color: #94a3b8;
  margin-top: 4px;
}

/* Avatar Upload */
.avatar-upload {
  margin-bottom: 32px;
}

.current-avatar {
  display: flex;
  align-items: center;
  gap: 24px;
}

.current-avatar img {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  object-fit: cover;
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.btn-avatar-edit {
  background: #f1f5f9;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  color: #475569;
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
}

.role-badge {
  display: inline-block;
  padding: 4px 12px;
  background: #eff6ff;
  color: #2563eb;
  border-radius: 100px;
  font-size: 0.7rem;
  font-weight: 800;
  letter-spacing: 0.05em;
  width: fit-content;
}

/* AI Engine - Key Toggle */
.input-with-action {
  position: relative;
  display: flex;
}

.input-with-action input {
  width: 100%;
  padding-right: 60px;
}

.btn-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #3b82f6;
  font-weight: 700;
  font-size: 0.8rem;
  cursor: pointer;
}

/* Config Card */
.config-card {
  padding: 24px;
  border-radius: 16px;
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.config-card.warning {
  background: #fffbeb;
  border: 1px solid #fef3c7;
}

.card-icon {
  font-size: 1.5rem;
}

.card-text h3 {
  font-size: 0.95rem;
  font-weight: 800;
  color: #92400e;
  margin-bottom: 4px;
}

.card-text p {
  font-size: 0.85rem;
  color: #b45309;
}

/* Theme Selector */
.theme-selector {
  display: flex;
  gap: 12px;
}

.theme-btn {
  padding: 10px 20px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  background: white;
  font-weight: 700;
  font-size: 0.85rem;
  cursor: pointer;
}

.theme-btn.active {
  border-color: #3b82f6;
  color: #2563eb;
  background: #eff6ff;
}

.theme-btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #f8fafc;
}

/* Footer Area */
.settings-footer {
  margin-top: 60px;
  padding-top: 32px;
  border-top: 1px solid #f1f5f9;
  display: flex;
  gap: 16px;
}

.btn-save {
  background: #111827;
  color: white;
  border: none;
  padding: 12px 32px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-save:hover {
  background: #000;
  transform: translateY(-2px);
}

.btn-cancel {
  background: white;
  color: #475569;
  border: 1px solid #e2e8f0;
  padding: 12px 32px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
}

.mt-lg {
  margin-top: 32px;
}
</style>
