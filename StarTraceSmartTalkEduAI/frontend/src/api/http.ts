import axios, { type AxiosInstance } from "axios";

/**
 * 全局 axios 实例：
 * - 统一 baseURL / 超时 / 请求头
 * - 统一处理 token（如需）
 * - 统一错误提示（可按项目替换为 UI toast）
 *
 * 说明：
 * - 默认超时时间从 30 秒提升到 5 分钟（300_000ms），与后端 server.connection-timeout 保持一致
 * - 避免大文件上传、AI 推理等长耗时操作在前端过早超时
 */
export const http: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "/api",
  timeout: 300_000,
  withCredentials: true, // 允许跨域携带 cookie（用于 Sim 登录等场景）
  headers: {
    "Content-Type": "application/json"
  }
});

http.interceptors.request.use((config) => {
  // 从 localStorage 取 token
  const token = localStorage.getItem("token");
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }

  // 添加 X-User-Id 头（后端需要）
  const userStr = localStorage.getItem("currentUser");
  if (userStr) {
    try {
      const user = JSON.parse(userStr);
      if (user?.id) {
        config.headers = config.headers ?? {};
        config.headers["X-User-Id"] = user.id;
      }
    } catch (e) {
      console.error("解析用户信息失败", e);
    }
  }

  // 如果是 FormData，不设置 Content-Type，让浏览器自动设置（包含 boundary）
  if (config.data instanceof FormData) {
    delete config.headers["Content-Type"];
  }

  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error) => {
    // 这里先用 console 输出；实际项目可接入 message/toast
    const message =
      error?.response?.data?.message ?? error?.message ?? "Network error";
    console.error("[HTTP ERROR]", message, error);
    return Promise.reject(error);
  }
);



