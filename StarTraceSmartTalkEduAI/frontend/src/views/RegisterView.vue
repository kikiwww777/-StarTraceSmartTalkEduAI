<template>
  <div class="register-page fade-in">
    <div class="register-container">
      <!-- Left Brand Side (Consistent with Login) -->
      <div class="register-brand-side">
        <div class="brand-float">
          <div class="brand-icon-large">E</div>
          <h1 class="brand-title-large">加入 EduAI</h1>
          <p class="brand-slogan">构建新一代 Agentic AI 教学环境</p>
        </div>
        <div class="brand-footer-info">
          <span>&copy; 2024 StarTrace Advanced Agentic AI.</span>
        </div>
      </div>

      <!-- Right Form Side -->
      <div class="register-form-side">
        <div class="register-card">
          <header class="form-header">
            <h2 class="welcome-text">创建您的学术账号</h2>
            <p class="subtitle-text">填写以下信息，开启为您定制的智能体验</p>
          </header>

          <form @submit.prevent="handleRegister" class="auth-form">
            <div class="form-row">
              <div class="form-item">
                <label>学号 / 工号</label>
                <div class="input-wrapper">
                  <input type="text" v-model="form.studentId" placeholder="ID" required />
                </div>
              </div>
              <div class="form-item">
                <label>您的姓名</label>
                <div class="input-wrapper">
                  <input type="text" v-model="form.name" placeholder="真实姓名" required />
                </div>
              </div>
            </div>

            <div class="form-item">
              <label>身份角色</label>
              <div class="role-cards">
                <div 
                  class="role-card" 
                  :class="{ active: form.role === 'student' }"
                  @click="form.role = 'student'"
                >
                  <div class="role-icon">👨‍🎓</div>
                  <div class="role-info">
                    <span class="role-name">学生</span>
                    <span class="role-desc">学习与互动机拟</span>
                  </div>
                </div>
                <div 
                  class="role-card" 
                  :class="{ active: form.role === 'teacher' }"
                  @click="form.role = 'teacher'"
                >
                  <div class="role-icon">🧑‍🏫</div>
                  <div class="role-info">
                    <span class="role-name">教师</span>
                    <span class="role-desc">课程管理与报表</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="form-row">
              <div class="form-item">
                <label>设置密码</label>
                <div class="input-wrapper">
                  <input type="password" v-model="form.password" placeholder="至少 6 位" required />
                </div>
              </div>
              <div class="form-item">
                <label>确认密码</label>
                <div class="input-wrapper">
                  <input type="password" v-model="form.confirmPassword" placeholder="再次输入" required />
                </div>
              </div>
            </div>

            <button type="submit" class="btn-primary-auth" :disabled="loading">
              <span v-if="!loading">立即注册</span>
              <span v-else class="loader-mini"></span>
            </button>

            <div class="login-hint">
              <span>已有账号？</span>
              <router-link to="/login" class="link-highlight">直接登录</router-link>
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
import { http } from "../api/http";

const router = useRouter();
const loading = ref(false);

const form = ref({
  studentId: "",
  name: "",
  role: "student" as "student" | "teacher",
  password: "",
  confirmPassword: ""
});

const handleRegister = async () => {
  if (form.value.password.length < 6) {
    alert("密码至少6位");
    return;
  }
  if (form.value.password !== form.value.confirmPassword) {
    alert("两次输入的密码不一致");
    return;
  }

  loading.value = true;
  try {
    const payload = {
      // RegisterDTO 仅接受 studentId / password / role
      studentId: form.value.studentId.trim(),
      password: form.value.password,
      role: form.value.role
    };

    // 对应后端接口：POST /api/user/register
    const { data } = await http.post("/user/register", payload);

    if (!data || typeof data.code !== "number") {
      alert("注册失败：后端响应格式异常");
      return;
    }

    if (data.code !== 200) {
      alert(data.message ?? "注册失败");
      return;
    }

    alert("注册成功，请登录");
    router.push("/login");
  } catch (error: any) {
    const message =
      error?.response?.data?.message ??
      error?.response?.data?.error ??
      error?.message ??
      "注册失败";
    alert(message);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.register-page {
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

.register-container {
  display: flex;
  width: 100%;
  max-width: 1100px;
  height: 720px;
  background: white;
  border-radius: 32px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  border: 1px solid #f1f5f9;
}

/* Brand Side */
.register-brand-side {
  flex: 1;
  background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
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
  background: white;
  color: #2563eb;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.5rem;
  font-weight: 800;
  margin: 0 auto 32px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.brand-title-large {
  font-size: 2.5rem;
  font-weight: 800;
  letter-spacing: -0.02em;
  margin-bottom: 16px;
}

.brand-slogan {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
}

.brand-footer-info {
  position: absolute;
  bottom: 40px;
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.6);
  letter-spacing: 0.02em;
}

/* Form Side */
.register-form-side {
  flex: 1.2;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: white;
  overflow-y: auto;
}

.register-card {
  width: 100%;
  max-width: 480px;
}

.form-header {
  margin-bottom: 32px;
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
  gap: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
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

.input-wrapper input {
  width: 100%;
  padding: 12px 16px;
  border-radius: 12px;
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

/* Role Cards */
.role-cards {
  display: flex;
  gap: 12px;
}

.role-card {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.role-card:hover {
  background: white;
  border-color: #3b82f6;
}

.role-card.active {
  background: #eff6ff;
  border-color: #3b82f6;
  box-shadow: 0 0 0 1px #3b82f6;
}

.role-icon {
  font-size: 1.5rem;
}

.role-info {
  display: flex;
  flex-direction: column;
}

.role-name {
  font-size: 0.95rem;
  font-weight: 800;
  color: #1e293b;
}

.role-desc {
  font-size: 0.75rem;
  color: #94a3b8;
  white-space: nowrap;
}

.btn-primary-auth {
  width: 100%;
  padding: 16px;
  border-radius: 14px;
  border: none;
  background: #111827;
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
}

.btn-primary-auth:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.login-hint {
  text-align: center;
  margin-top: 16px;
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
  .register-container {
    max-width: 450px;
    height: auto;
    flex-direction: column;
  }
  .register-brand-side {
    display: none;
  }
}
</style>
