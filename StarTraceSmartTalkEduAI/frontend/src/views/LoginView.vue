<template>
  <div class="login-page fade-in">
    <div class="login-container">
      <!-- Left Decoration/Context Side -->
      <div class="login-brand-side">
        <div class="brand-float">
          <div class="brand-icon-large">E</div>
          <h1 class="brand-title-large">EduAI Studio</h1>
          <p class="brand-slogan">开启学术驱动的智能教学新旅程</p>
        </div>
        <div class="brand-footer-info">
          <span>&copy; 2024 StarTrace Advanced Agentic AI.</span>
        </div>
      </div>

      <!-- Right Form Side -->
      <div class="login-form-side">
        <div class="login-card">
          <header class="form-header">
            <h2 class="welcome-text">欢迎回来</h2>
            <p class="subtitle-text">请输入您的凭据以访问控制面板</p>
          </header>

          <form @submit.prevent="handleLogin" class="auth-form">
            <div class="form-item">
              <label>学号 / 工号</label>
              <div class="input-wrapper">
                <div class="input-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                    <circle cx="12" cy="7" r="4" />
                  </svg>
                </div>
                <input 
                  type="text" 
                  v-model="form.studentId" 
                  placeholder="ID" 
                  required 
                />
              </div>
            </div>

            <div class="form-item">
              <label>访问密码</label>
              <div class="input-wrapper">
                <div class="input-icon">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                  </svg>
                </div>
                <input 
                  type="password" 
                  v-model="form.password" 
                  placeholder="••••••" 
                  required 
                />
              </div>
            </div>

            <div class="form-utils">
              <label class="checkbox-container">
                <input type="checkbox" checked />
                <span class="checkmark"></span>
                记住我
              </label>
              <a href="#" class="forgot-link">忘记密码？</a>
            </div>

            <button type="submit" class="btn-primary-auth" :disabled="loading">
              <span v-if="!loading">登录系统</span>
              <span v-else class="loader-mini"></span>
            </button>

            <div class="register-hint">
              <span>还没有账号？</span>
              <router-link to="/register" class="link-highlight">立即注册</router-link>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuth, type UserRole } from "../composables/useAuth";
import { http } from "../api/http";

const router = useRouter();
const { login } = useAuth();
const loading = ref(false);

const form = ref({
  studentId: "",
  password: ""
});

const handleLogin = async () => {
  loading.value = true;
  try {
    const payload = {
      // 后端 LoginDTO 使用字段名 username，这里用学号/工号填充
      username: form.value.studentId.trim(),
      password: form.value.password
    };

    // 后端实际登录接口为 /api/user/login，返回通用 Result<T> 结构
    const { data } = await http.post("/user/login", payload);

    if (data?.code !== 200 || !data?.data) {
      alert(data?.message ?? "登录失败");
      return;
    }

    // 当前后端未返回 token，仅返回 User 实体，这里直接使用用户信息
    const userFromServer = data.data as {
      id?: string;
      studentId: string;
      name: string;
      role?: UserRole;
    };

    const user = {
      id: userFromServer.id ?? userFromServer.studentId,
      studentId: userFromServer.studentId,
      role: (userFromServer.role as UserRole) ?? "student",
      name: userFromServer.name,
      username: userFromServer.studentId,
    };

    login(user);
    router.push("/");
  } catch (error: any) {
    const message = error?.response?.data?.error ?? error?.message ?? "登录失败";
    alert(message);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8fafc;
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
}

.login-container {
  display: flex;
  width: 100%;
  max-width: 1100px;
  height: 640px;
  background: white;
  border-radius: 32px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  border: 1px solid #f1f5f9;
}

/* Brand Side */
.login-brand-side {
  flex: 1;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 60px;
  position: relative;
  color: white;
}

.brand-float {
  text-align: center;
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}

.brand-icon-large {
  width: 80px;
  height: 80px;
  background: linear-gradient(to top right, #3b82f6, #6366f1);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.5rem;
  font-weight: 800;
  margin: 0 auto 32px;
  box-shadow: 0 20px 40px rgba(59, 130, 246, 0.3);
}

.brand-title-large {
  font-size: 2.5rem;
  font-weight: 800;
  letter-spacing: -0.02em;
  margin-bottom: 16px;
}

.brand-slogan {
  font-size: 1.1rem;
  color: #94a3b8;
  font-weight: 500;
}

.brand-footer-info {
  position: absolute;
  bottom: 40px;
  font-size: 0.75rem;
  color: #475569;
  letter-spacing: 0.02em;
}

/* Form Side */
.login-form-side {
  flex: 1.1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: white;
}

.login-card {
  width: 100%;
  max-width: 400px;
}

.form-header {
  margin-bottom: 40px;
}

.welcome-text {
  font-size: 1.85rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 10px;
}

.subtitle-text {
  color: #64748b;
  font-size: 0.95rem;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  font-size: 0.85rem;
  font-weight: 700;
  color: #475569;
  margin-left: 4px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  color: #94a3b8;
  display: flex;
}

.input-wrapper input {
  width: 100%;
  padding: 14px 16px 14px 50px;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  font-size: 0.95rem;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  outline: none;
}

.input-wrapper input:focus {
  background: white;
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.08);
}

.form-utils {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.85rem;
}

.checkbox-container {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #64748b;
  font-weight: 600;
}

.forgot-link {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 700;
}

.btn-primary-auth {
  width: 100%;
  padding: 16px;
  border-radius: 14px;
  border: none;
  background: #1e293b;
  color: white;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 10px;
}

.btn-primary-auth:hover {
  background: #000;
  transform: translateY(-2px);
  box-shadow: 0 10px 20px -5px rgba(0, 0, 0, 0.2);
}

.btn-primary-auth:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.register-hint {
  text-align: center;
  margin-top: 24px;
  font-size: 0.9rem;
  color: #64748b;
  font-weight: 500;
}

.link-highlight {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 800;
  margin-left: 6px;
}

.link-highlight:hover {
  text-decoration: underline;
}

.loader-mini {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255,255,255,0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 900px) {
  .login-container {
    max-width: 450px;
    height: auto;
    flex-direction: column;
  }
  .login-brand-side {
    display: none;
  }
}
</style>

