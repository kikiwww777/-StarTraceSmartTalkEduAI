import { ref, computed } from "vue";

export type UserRole = "student" | "teacher" | "admin";

export interface User {
  id: number | string;
  studentId: string;
  role: UserRole;
  name?: string;
  username?: string;
  email?: string;
  avatar?: string;
  createdAt?: string;
  updatedAt?: string;
}

const currentUser = ref<User | null>(null);

function getSimBaseUrl(): string {
  // Vite only exposes env vars prefixed with VITE_
  const envUrl = import.meta.env.VITE_SIM_BASE_URL;
  // 如果环境变量为空或未设置，且使用 Nginx 代理，则使用相对路径
  if (!envUrl || envUrl === '') {
    return '/sim';
  }
  // 如果环境变量是相对路径（以 / 开头），直接使用
  if (envUrl.startsWith('/')) {
    return envUrl.replace(/\/+$/, '');
  }
  // 否则使用绝对路径
  return String(envUrl).replace(/\/+$/, '');
}

function bestEffortSimSignOut(): void {
  try {
    const base = getSimBaseUrl();
    const ts = Date.now();
    const iframe = document.createElement("iframe");
    iframe.style.display = "none";
    iframe.src = `${base}/api/auth/sign-out?ts=${ts}`;
    document.body.appendChild(iframe);
    window.setTimeout(() => iframe.remove(), 1500);
  } catch {
    // ignore
  }
}

export function useAuth() {
  // 从 localStorage 恢复用户信息
  const initAuth = () => {
    const saved = localStorage.getItem("currentUser");
    if (saved) {
      try {
        currentUser.value = JSON.parse(saved);
      } catch {
        currentUser.value = null;
      }
    }
  };

  // 登录
  const login = (user: User) => {
    currentUser.value = user;
    localStorage.setItem("currentUser", JSON.stringify(user));
  };

  // 登出
  const logout = () => {
    // 同步登出 SIM（best-effort，不阻塞本系统退出）
    bestEffortSimSignOut();
    currentUser.value = null;
    localStorage.removeItem("currentUser");
  };

  // 当前用户角色
  const userRole = computed(() => currentUser.value?.role || null);

  // 是否为管理员
  const isAdmin = computed(() => currentUser.value?.role === "admin");

  // 是否为老师
  const isTeacher = computed(() => currentUser.value?.role === "teacher");

  // 是否为学生
  const isStudent = computed(() => currentUser.value?.role === "student");

  // 是否已登录
  const isAuthenticated = computed(() => currentUser.value !== null);

  // 切换角色（用于测试）
  const switchRole = () => {
    if (!currentUser.value) return;
    const nextRole: UserRole = currentUser.value.role === "teacher" ? "student" : "teacher";
    currentUser.value = { ...currentUser.value, role: nextRole };
    localStorage.setItem("currentUser", JSON.stringify(currentUser.value));
    // 强制刷新页面以确保所有组件和路由根据新角色重新渲染
    window.location.reload();
  };

  return {
    currentUser,
    userRole,
    isAdmin,
    isTeacher,
    isStudent,
    isAuthenticated,
    login,
    logout,
    initAuth,
    switchRole
  };
}


