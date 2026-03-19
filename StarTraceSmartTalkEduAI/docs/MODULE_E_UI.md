# 模块E：前端UI增强 - 详细设计文档

## 📋 模块概述

前端UI增强模块提供统一的用户界面组件和交互体验，包括工作区页面、课程选择器、配额显示组件等。

---

## 🎯 核心功能

### E-1. 工作区页面框架

**功能描述**：
- 课程关联的AI工作区入口
- 可使用已导入的模板
- 查看使用统计
- Phase 1为占位页面，后续对接Sim Studio

**UI设计**：
```
┌─────────────────────────────────────────────────────────────┐
│  🔧 工作区 - 人工智能导论                                    │
│                                                               │
│  ┌─ 配额状态 ─────────────────────────────────────────────┐ │
│  │  今日: ▓▓▓▓▓▓░░░ 65%  |  本周: ▓▓▓▓░░░░░ 40%         │ │
│  │  剩余 3,500 / 10,000 tokens                            │ │
│  └─────────────────────────────────────────────────────────┘ │
│                                                               │
│  ┌─ 可用模板 ─────────────────────────────────────────────┐ │
│  │                                                         │ │
│  │  🎓 智能辅导助手        📝 作业评分助手                 │ │
│  │  [开始使用]             [开始使用]                      │ │
│  │                                                         │ │
│  │  💻 代码调试助手                                        │ │
│  │  [开始使用]                                             │ │
│  │                                                         │ │
│  │  还未导入模板？ [去模板库选择 →]                        │ │
│  │                                                         │ │
│  └─────────────────────────────────────────────────────────┘ │
│                                                               │
│  ┌─ 对话工作区 ───────────────────────────────────────────┐ │
│  │                                                         │ │
│  │              🚧 功能开发中                              │ │
│  │                                                         │ │
│  │     AI工作区即将上线，敬请期待！                        │ │
│  │                                                         │ │
│  │     当前阶段：                                          │ │
│  │     ✅ 课程管理        ⏳ 工作区对接                    │ │
│  │     ✅ 模板库          ⏳ 实时对话                      │ │
│  │     ✅ 配额管理        ⏳ 知识检索                      │ │
│  │                                                         │ │
│  └─────────────────────────────────────────────────────────┘ │
│                                                               │
│  ┌─ 最近使用记录 ─────────────────────────────────────────┐ │
│  │                                                         │ │
│  │  暂无使用记录                                           │ │
│  │                                                         │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

### E-2. 课程选择器组件

**功能描述**：
- 快速切换当前课程上下文
- 显示在工作区、模板库等页面
- 支持搜索过滤

**UI设计**：
```
┌─ 课程选择器（展开状态）─────────────────────────────────────┐
│                                                               │
│  [🔽 人工智能导论 ▼]                                         │
│                                                               │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  🔍 搜索课程...                                        │  │
│  │                                                         │  │
│  │  👨‍🏫 我教的课                                          │  │
│  │  ─────────────────────────────────────────────────────  │  │
│  │  📚 人工智能导论                              ← 当前    │  │
│  │  📚 机器学习实践                                        │  │
│  │                                                         │  │
│  │  🎓 我学的课                                           │  │
│  │  ─────────────────────────────────────────────────────  │  │
│  │  📚 深度学习基础                                        │  │
│  │  📚 计算机视觉                                          │  │
│  │                                                         │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

---

### E-3. 通用组件库

#### 配额显示组件 QuotaBar
```vue
<template>
  <div class="quota-bar">
    <div class="quota-label">
      <span>{{ label }}</span>
      <span class="quota-numbers">{{ used.toLocaleString() }} / {{ limit.toLocaleString() }}</span>
    </div>
    <div class="quota-progress">
      <div 
        class="quota-fill" 
        :style="{ width: percent + '%' }"
        :class="{ warning: percent >= 70, danger: percent >= 90 }"
      ></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
  label: string;
  used: number;
  limit: number;
}>();

const percent = computed(() => Math.min(100, (props.used / props.limit) * 100));
</script>

<style scoped>
.quota-bar { margin-bottom: 12px; }
.quota-label {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: rgba(232, 237, 247, 0.8);
  margin-bottom: 6px;
}
.quota-numbers { color: rgba(232, 237, 247, 0.5); }
.quota-progress {
  height: 6px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
  overflow: hidden;
}
.quota-fill {
  height: 100%;
  background: #22c55e;
  border-radius: 3px;
  transition: width 0.3s;
}
.quota-fill.warning { background: #eab308; }
.quota-fill.danger { background: #ef4444; }
</style>
```

#### 状态标签组件 StatusBadge
```vue
<template>
  <span class="status-badge" :class="status">
    {{ getStatusText(status) }}
  </span>
</template>

<script setup lang="ts">
const props = defineProps<{
  status: 'active' | 'processing' | 'error' | 'pending' | 'completed';
}>();

const getStatusText = (status: string) => {
  const texts = {
    active: '✅ 可用',
    processing: '⏳ 处理中',
    error: '❌ 错误',
    pending: '⏸️ 等待中',
    completed: '✓ 完成'
  };
  return texts[status] || status;
};
</script>

<style scoped>
.status-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}
.status-badge.active { background: rgba(34, 197, 94, 0.2); color: #22c55e; }
.status-badge.processing { background: rgba(59, 130, 246, 0.2); color: #3b82f6; }
.status-badge.error { background: rgba(239, 68, 68, 0.2); color: #ef4444; }
.status-badge.pending { background: rgba(156, 163, 175, 0.2); color: #9ca3af; }
.status-badge.completed { background: rgba(34, 197, 94, 0.2); color: #22c55e; }
</style>
```

#### 空状态组件 EmptyState
```vue
<template>
  <div class="empty-state">
    <div class="empty-icon">{{ icon }}</div>
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
    <slot name="action"></slot>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  icon: string;
  title: string;
  description: string;
}>();
</script>

<style scoped>
.empty-state {
  text-align: center;
  padding: 60px 20px;
}
.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}
.empty-state h3 {
  color: #e8edf7;
  margin: 0 0 8px 0;
}
.empty-state p {
  color: rgba(232, 237, 247, 0.5);
  margin: 0 0 20px 0;
}
</style>
```

#### 确认对话框组件 ConfirmDialog
```vue
<template>
  <Teleport to="body">
    <div v-if="visible" class="dialog-overlay" @click="handleCancel">
      <div class="dialog-content" @click.stop>
        <div class="dialog-icon" :class="type">{{ getIcon(type) }}</div>
        <h3>{{ title }}</h3>
        <p>{{ message }}</p>
        <div class="dialog-actions">
          <button @click="handleCancel" class="btn-cancel">取消</button>
          <button @click="handleConfirm" :class="'btn-' + type">
            {{ confirmText }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
const props = defineProps<{
  visible: boolean;
  type: 'info' | 'warning' | 'danger';
  title: string;
  message: string;
  confirmText?: string;
}>();

const emit = defineEmits(['confirm', 'cancel']);

const getIcon = (type: string) => {
  const icons = { info: 'ℹ️', warning: '⚠️', danger: '🗑️' };
  return icons[type] || 'ℹ️';
};

const handleConfirm = () => emit('confirm');
const handleCancel = () => emit('cancel');
</script>
```

#### 加载状态组件 LoadingSpinner
```vue
<template>
  <div class="loading-wrapper" v-if="loading">
    <div class="spinner"></div>
    <span v-if="text">{{ text }}</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  loading: boolean;
  text?: string;
}>();
</script>

<style scoped>
.loading-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: rgba(232, 237, 247, 0.6);
}
.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255, 255, 255, 0.1);
  border-top-color: #6de6ff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 12px;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
</style>
```

---

### E-4. 导航栏增强

**功能描述**：
- 响应式导航栏
- 移动端汉堡菜单
- 用户下拉菜单
- 课程快速切换

**导航栏组件 NavBar**：
```vue
<template>
  <nav class="main-nav">
    <!-- Logo -->
    <div class="nav-brand">
      <router-link to="/">
        <span class="logo-icon">🤖</span>
        <span class="logo-text">EduAI Studio</span>
      </router-link>
    </div>
    
    <!-- 移动端菜单按钮 -->
    <button class="menu-toggle" @click="mobileMenuOpen = !mobileMenuOpen">
      ☰
    </button>
    
    <!-- 导航链接 -->
    <div class="nav-links" :class="{ open: mobileMenuOpen }">
      <template v-if="isAuthenticated">
        <router-link to="/" @click="mobileMenuOpen = false">首页</router-link>
        <router-link to="/courses" @click="mobileMenuOpen = false">课程</router-link>
        <router-link to="/templates" @click="mobileMenuOpen = false">模板</router-link>
      </template>
    </div>
    
    <!-- 用户区域 -->
    <div class="nav-user">
      <template v-if="isAuthenticated">
        <!-- 课程快速切换 -->
        <CourseSelector v-if="showCourseSelector" />
        
        <!-- 用户菜单 -->
        <div class="user-menu" @click="userMenuOpen = !userMenuOpen">
          <span class="user-avatar">{{ getUserInitial() }}</span>
          <span class="user-name">{{ currentUser?.name }}</span>
          <span class="user-role">({{ getRoleLabel() }})</span>
          <span class="dropdown-arrow">▼</span>
          
          <div class="user-dropdown" v-if="userMenuOpen">
            <router-link to="/profile">个人设置</router-link>
            <button @click="handleLogout">登出</button>
          </div>
        </div>
      </template>
      <template v-else>
        <router-link to="/login" class="nav-btn">登录</router-link>
        <router-link to="/register" class="nav-btn primary">注册</router-link>
      </template>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuth } from '../composables/useAuth';
import CourseSelector from './CourseSelector.vue';

const router = useRouter();
const route = useRoute();
const { currentUser, isAuthenticated, logout, initAuth } = useAuth();

const mobileMenuOpen = ref(false);
const userMenuOpen = ref(false);

const showCourseSelector = computed(() => {
  const paths = ['/workspace', '/templates', '/knowledge-base'];
  return paths.some(p => route.path.startsWith(p));
});

const getUserInitial = () => currentUser.value?.name?.charAt(0) || '?';
const getRoleLabel = () => currentUser.value?.role === 'teacher' ? '教师' : '学生';

const handleLogout = () => {
  logout();
  localStorage.removeItem('token');
  router.push('/login');
};

// 点击外部关闭菜单
const handleClickOutside = (e: MouseEvent) => {
  const target = e.target as HTMLElement;
  if (!target.closest('.user-menu')) {
    userMenuOpen.value = false;
  }
};

onMounted(() => {
  initAuth();
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
.main-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px;
  background: rgba(11, 18, 32, 0.95);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-brand a {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  color: #6de6ff;
  font-size: 18px;
  font-weight: 600;
}

.logo-icon { font-size: 24px; }

.menu-toggle {
  display: none;
  background: none;
  border: none;
  color: #e8edf7;
  font-size: 24px;
  cursor: pointer;
}

.nav-links {
  display: flex;
  gap: 24px;
}

.nav-links a {
  color: rgba(232, 237, 247, 0.7);
  text-decoration: none;
  font-size: 14px;
  transition: color 0.2s;
}

.nav-links a:hover,
.nav-links a.router-link-active {
  color: #6de6ff;
}

.nav-user {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  position: relative;
  padding: 8px;
  border-radius: 8px;
}

.user-menu:hover {
  background: rgba(255, 255, 255, 0.05);
}

.user-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #6de6ff, #a855f7);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #0b1220;
  font-weight: 600;
}

.user-name { color: #e8edf7; font-size: 14px; }
.user-role { color: rgba(232, 237, 247, 0.5); font-size: 12px; }
.dropdown-arrow { color: rgba(232, 237, 247, 0.5); font-size: 10px; }

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background: #1a2332;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 8px 0;
  min-width: 150px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.user-dropdown a,
.user-dropdown button {
  display: block;
  width: 100%;
  padding: 10px 16px;
  text-align: left;
  color: #e8edf7;
  text-decoration: none;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
}

.user-dropdown a:hover,
.user-dropdown button:hover {
  background: rgba(255, 255, 255, 0.05);
}

.nav-btn {
  padding: 8px 16px;
  border-radius: 6px;
  text-decoration: none;
  font-size: 14px;
  color: #e8edf7;
}

.nav-btn.primary {
  background: #6de6ff;
  color: #0b1220;
}

/* 响应式 */
@media (max-width: 768px) {
  .menu-toggle { display: block; }
  
  .nav-links {
    position: absolute;
    top: 60px;
    left: 0;
    right: 0;
    background: #0b1220;
    flex-direction: column;
    padding: 16px;
    gap: 16px;
    display: none;
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  }
  
  .nav-links.open { display: flex; }
  
  .user-name, .user-role, .dropdown-arrow { display: none; }
}
</style>
```

---

### E-5. 全局样式系统

**CSS变量定义**：
```css
/* styles/global.css */

:root {
  /* 主色调 */
  --color-primary: #6de6ff;
  --color-primary-hover: #5dd5ef;
  --color-secondary: #a855f7;
  
  /* 背景色 */
  --color-bg-primary: #0b1220;
  --color-bg-secondary: #111827;
  --color-bg-card: rgba(255, 255, 255, 0.04);
  --color-bg-hover: rgba(255, 255, 255, 0.08);
  
  /* 文字色 */
  --color-text-primary: #e8edf7;
  --color-text-secondary: rgba(232, 237, 247, 0.7);
  --color-text-muted: rgba(232, 237, 247, 0.5);
  
  /* 边框色 */
  --color-border: rgba(255, 255, 255, 0.1);
  --color-border-focus: #6de6ff;
  
  /* 状态色 */
  --color-success: #22c55e;
  --color-warning: #eab308;
  --color-danger: #ef4444;
  --color-info: #3b82f6;
  
  /* 阴影 */
  --shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.15);
  --shadow-md: 0 8px 24px rgba(0, 0, 0, 0.2);
  --shadow-lg: 0 16px 48px rgba(0, 0, 0, 0.3);
  
  /* 圆角 */
  --radius-sm: 4px;
  --radius-md: 8px;
  --radius-lg: 12px;
  --radius-xl: 16px;
  
  /* 间距 */
  --space-xs: 4px;
  --space-sm: 8px;
  --space-md: 16px;
  --space-lg: 24px;
  --space-xl: 32px;
  
  /* 动画 */
  --transition-fast: 0.15s ease;
  --transition-normal: 0.2s ease;
  --transition-slow: 0.3s ease;
}

/* 基础重置 */
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background: var(--color-bg-primary);
  color: var(--color-text-primary);
  line-height: 1.5;
}

/* 通用按钮样式 */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-sm);
  padding: 10px 20px;
  border-radius: var(--radius-md);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition-normal);
  border: none;
}

.btn-primary {
  background: var(--color-primary);
  color: var(--color-bg-primary);
}

.btn-primary:hover {
  background: var(--color-primary-hover);
}

.btn-secondary {
  background: transparent;
  border: 1px solid var(--color-border);
  color: var(--color-text-primary);
}

.btn-secondary:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.btn-danger {
  background: var(--color-danger);
  color: white;
}

.btn-danger:hover {
  background: #dc2626;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 通用输入框样式 */
.input {
  width: 100%;
  padding: 12px 16px;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  background: rgba(0, 0, 0, 0.2);
  color: var(--color-text-primary);
  font-size: 14px;
  transition: border-color var(--transition-normal);
}

.input:focus {
  outline: none;
  border-color: var(--color-border-focus);
}

.input::placeholder {
  color: var(--color-text-muted);
}

/* 卡片样式 */
.card {
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
}

.card:hover {
  border-color: var(--color-primary);
}

/* 表格样式 */
.table {
  width: 100%;
  border-collapse: collapse;
}

.table th,
.table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid var(--color-border);
}

.table th {
  color: var(--color-text-secondary);
  font-weight: 500;
  font-size: 13px;
}

.table tr:hover {
  background: var(--color-bg-hover);
}

/* 动画类 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-normal);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all var(--transition-normal);
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
```

---

## 📱 响应式断点

```css
/* 移动端 */
@media (max-width: 640px) {
  .page-header h1 { font-size: 24px; }
  .card { padding: var(--space-md); }
}

/* 平板 */
@media (max-width: 768px) {
  .nav-links { display: none; }
  .menu-toggle { display: block; }
}

/* 桌面端 */
@media (min-width: 1024px) {
  .container { max-width: 1200px; margin: 0 auto; }
}
```

---

## ✅ 测试用例

| 编号 | 场景 | 预期结果 |
|------|------|----------|
| E-01 | 导航栏响应式 | 移动端显示汉堡菜单 |
| E-02 | 用户下拉菜单 | 点击显示/隐藏 |
| E-03 | 课程选择器 | 正确切换课程 |
| E-04 | 配额显示组件 | 正确显示进度条颜色 |
| E-05 | 空状态组件 | 正确显示图标和文字 |
| E-06 | 确认对话框 | 正确触发回调 |
| E-07 | 加载状态 | 正确显示/隐藏 |
| E-08 | 深色主题一致性 | 所有页面颜色统一 |
| E-09 | 移动端布局 | 各页面适配良好 |
| E-10 | 动画过渡 | 流畅无卡顿 |
