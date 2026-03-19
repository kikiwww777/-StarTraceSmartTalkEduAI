<script setup lang="ts">
import { RouterLink, RouterView, useRouter, useRoute } from "vue-router";
import { useAuth } from "./composables/useAuth";
import { computed, ref } from "vue";

const router = useRouter();
const route = useRoute();
const { currentUser, isAuthenticated, logout, userRole, switchRole } = useAuth();

// Menu Items with the updated SVG paths from the React snippet
const navItems = [
  { 
    id: 'dashboard', 
    label: '控制面板', 
    path: '/',
    icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <rect x="3" y="3" width="7" height="9" />
      <rect x="14" y="3" width="7" height="5" />
      <rect x="14" y="12" width="7" height="9" />
      <rect x="3" y="16" width="7" height="5" />
    </svg>`
  },
  { 
    id: 'workspace', 
    label: '互动工作区', 
    path: '/workspace',
    icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <polyline points="4 17 10 11 4 5" />
      <line x1="12" y1="19" x2="20" y2="19" />
    </svg>`
  },
  { 
    id: 'courses', 
    label: '我的课程', 
    path: '/courses',
    icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <path d="M22 10v6M2 10l10-5 10 5-10 5zM6 12v5c0 2 2 3 6 3s6-1 6-3v-5" />
    </svg>`
  },
  { 
    id: 'templates', 
    label: '模板库', 
    path: '/templates',
    icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <path d="m12 3 8 4.5v9L12 21l-8-4.5v-9L12 3zM12 12l8-4.5M12 12v9M12 12 4 7.5" />
    </svg>`
  },
  { 
    id: 'knowledge', 
    label: '知识库', 
    path: '/knowledge',
    icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20M4 4.5A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1-2.5-2.5v-15z" />
    </svg>`
  },
  { 
    id: 'reports', 
    label: '教学报告', 
    path: '/reports',
    icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <path d="M21.21 15.89A10 10 0 1 1 8 2.83M22 12A10 10 0 0 0 12 2v10z" />
    </svg>`
  },
  { 
    id: 'settings', 
    label: '系统设置', 
    path: '/settings',
    icon: `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
      <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z" />
      <circle cx="12" cy="12" r="3" />
    </svg>`
  }
];

// Global Quota Data
const tokenUsage = ref({
  current: 34500,
  max: 100000,
  percent: 34.5
});

const formatNumber = (num: number) => {
  return num.toLocaleString();
};

const handleLogout = () => {
  logout();
  router.push("/login");
};

const getRoleText = (role: string | null) => {
  if (role === "admin") return "ADMIN";
  if (role === "teacher") return "TEACHER";
  if (role === "student") return "STUDENT";
  return "GUEST";
};
</script>

<template>
  <div class="app-layout">
    <!-- Sidebar -->
    <aside v-if="isAuthenticated" class="sidebar">
      <div class="sidebar-top">
        <div class="brand-section">
          <div class="brand-logo shadow-lg">E</div>
          <div class="brand-text">
            <h1 class="brand-title">EduAI</h1>
            <p class="brand-subtitle">星溯智语 EduAI Studio</p>
          </div>
        </div>
      </div>

      <nav class="sidebar-nav">
        <RouterLink 
          v-for="item in navItems" 
          :key="item.id" 
          :to="item.path"
          class="nav-item group"
          :class="{ active: route.path === item.path }"
        >
          <div class="nav-icon" v-html="item.icon"></div>
          <span class="nav-label">{{ item.label }}</span>
          <div v-if="route.path === item.path" class="nav-active-indicator"></div>
        </RouterLink>
      </nav>

      <div class="sidebar-footer">
        <!-- Role Switcher (Dev Mode) -->
        <div class="dev-role-switcher" @click="switchRole">
          <div class="switch-icon">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M7 11V7a5 5 0 0 1 10 0v4" />
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
              <path d="M12 18.5V14.5" />
            </svg>
          </div>
          <span>切换为 {{ userRole === 'teacher' ? '学生' : '老师' }}</span>
        </div>

        <div class="user-profile-card">
          <div class="avatar-container">
            <img v-if="currentUser?.avatar" :src="currentUser.avatar" alt="User" class="avatar-image shadow-sm" />
            <div v-else class="avatar-image avatar-placeholder shadow-sm">{{ currentUser?.name?.[0] || 'U' }}</div>
          </div>
          <div class="user-info">
            <p class="user-name">{{ currentUser?.name || 'User' }}</p>
            <div class="user-status">
              <span class="status-pulse"></span>
              <p class="user-role-label">{{ getRoleText(userRole) }}</p>
            </div>
          </div>
        </div>
      </div>
    </aside>

    <!-- Main Content -->
    <main class="main-wrapper">
      <!-- Global Quota Bar -->
      <div v-if="isAuthenticated" class="global-quota-header">
        <div class="quota-bar-container">
          <div class="quota-left">
            <div class="quota-icon-circle">
              <span class="quota-percent-mini">{{ Math.round(tokenUsage.percent) }}%</span>
            </div>
            <span class="quota-label-text">当前配额</span>
          </div>
          <div class="quota-center">
            <div class="quota-progress-track">
              <div class="quota-progress-fill" :style="{ width: tokenUsage.percent + '%' }"></div>
            </div>
          </div>
          <div class="quota-right">
            <span class="quota-numbers-text">{{ formatNumber(tokenUsage.current) }} / {{ formatNumber(tokenUsage.max) }}</span>
          </div>
        </div>
      </div>

      <div class="content-area">
        <RouterView v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </RouterView>
      </div>
    </main>
  </div>
</template>

<style scoped>

/* Global Quota Bar - Sleek horizontal design */
.global-quota-header {
  position: sticky;
  top: 0;
  z-index: 20;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(12px);
  padding: 10px 32px;
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  display: flex;
  justify-content: center;
}

.quota-bar-container {
  width: 100%;
  max-width: 1200px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.quota-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.quota-icon-circle {
  width: 42px;
  height: 42px;
  background: #111827;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.quota-percent-mini {
  color: #3b82f6;
  font-size: 0.75rem;
  font-weight: 800;
}

.quota-label-text {
  font-size: 0.85rem;
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.quota-center {
  flex: 1;
}

.quota-progress-track {
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
}

.quota-progress-fill {
  height: 100%;
  background: linear-gradient(to right, #3b82f6, #6366f1);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.quota-right {
  flex-shrink: 0;
}

.quota-numbers-text {
  font-family: var(--font-mono);
  font-size: 0.95rem;
  font-weight: 800;
  color: #1e293b;
}

.app-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background-color: #f8fafc;
}

/* Sidebar Styling - Following user request */
.sidebar {
  width: 280px;
  height: 100vh;
  background: #fcfdfe; /* Slightly off-white for contrast */
  border-right: 1px solid #f1f5f9;
  display: flex;
  flex-direction: column;
  z-index: 50;
  flex-shrink: 0;
}

.sidebar-top {
  padding: 32px; /* p-8 */
}

.brand-section {
  display: flex;
  align-items: center;
  gap: 12px; /* gap-3 */
}

.brand-logo {
  width: 40px; /* w-10 */
  height: 40px; /* h-10 */
  background: linear-gradient(to top right, #2563eb, #6366f1); /* bg-gradient-to-tr from-blue-600 to-indigo-500 */
  border-radius: 12px; /* rounded-xl */
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.25rem; /* text-xl */
  font-weight: 700; /* font-bold */
  box-shadow: 0 10px 15px -3px rgba(191, 219, 254, 0.5); /* shadow-lg shadow-blue-200 */
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-title {
  font-size: 1.125rem; /* text-lg */
  font-weight: 700; /* font-bold */
  color: #1e293b; /* text-slate-800 */
  line-height: 1;
  letter-spacing: -0.025em; /* tracking-tight */
}

.brand-subtitle {
  font-size: 10px;
  color: #94a3b8; /* text-slate-400 */
  margin-top: 4px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.1em; /* tracking-widest */
}

/* Nav Section */
.sidebar-nav {
  flex: 1;
  padding: 0 16px; /* px-4 */
  display: flex;
  flex-direction: column;
  gap: 6px; /* space-y-1.5 approx */
}

.nav-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 14px; /* gap-3.5 */
  padding: 12px 16px;
  border-radius: 12px; /* rounded-xl */
  font-size: 0.875rem; /* text-sm */
  font-weight: 600; /* font-semibold */
  text-decoration: none;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  color: #64748b; /* text-slate-500 */
  position: relative;
}

.nav-item:hover {
  background: #f8fafc; /* hover:bg-slate-50 */
  color: #334155; /* hover:text-slate-700 */
}

.nav-item.active {
  background: #eff6ff; /* bg-blue-50 */
  color: #2563eb; /* text-blue-600 */
  box-shadow: 0 1px 2px 0 rgba(239, 246, 255, 0.5); /* shadow-sm shadow-blue-50 */
}

.nav-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s;
  color: #94a3b8; /* text-slate-400 */
}

.nav-item:hover .nav-icon {
  transform: scale(1.1);
  color: #475569; /* group-hover:text-slate-600 */
}

.nav-item.active .nav-icon {
  color: #2563eb; /* active state icon color */
}

.nav-label {
  flex: 1;
}

.nav-active-indicator {
  width: 6px; /* w-1.5 */
  height: 6px; /* h-1.5 */
  border-radius: 9999px;
  background-color: #2563eb; /* bg-blue-600 */
}

/* Footer Section */
/* Role Switcher */
.dev-role-switcher {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  margin-bottom: 12px;
  background: transparent;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 700;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.dev-role-switcher:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  color: #1e293b;
}

.sidebar-footer {
  padding: 20px;
  border-top: 1px solid #f1f5f9;
}

.user-profile-card {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: white;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
  transition: all 0.2s;
  cursor: pointer;
}

.user-profile-card:hover {
  border-color: #dbeafe; /* hover:border-blue-100 */
}

.avatar-container {
  flex-shrink: 0;
}

.avatar-image {
  width: 40px;
  height: 40px;
  border-radius: 12px; /* rounded-xl */
  object-fit: cover;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.05); /* shadow-sm */
  box-shadow: 0 0 0 2px white; /* ring-2 ring-white */
}

.avatar-placeholder {
  background: #334155;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 0.875rem; /* text-sm */
  font-weight: 700; /* font-bold */
  color: #1e293b; /* text-slate-800 */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis; /* truncate */
}

.user-status {
  display: flex;
  align-items: center;
  gap: 6px; /* gap-1.5 */
}

.status-pulse {
  width: 6px; /* w-1.5 */
  height: 6px; /* h-1.5 */
  border-radius: 9999px;
  background-color: #10b981; /* bg-emerald-500 */
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: .5; }
}

.user-role-label {
  font-size: 10px;
  color: #94a3b8; /* text-slate-400 */
  font-weight: 700; /* font-bold */
  text-transform: uppercase;
  letter-spacing: -0.025em; /* tracking-tight */
}

/* Main Wrapper */
.main-wrapper {
  flex: 1;
  overflow-y: auto;
  background-color: #f8fafc;
}

.content-area {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
