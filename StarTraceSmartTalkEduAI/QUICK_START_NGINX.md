# Nginx 反向代理快速开始指南

## 问题解决

通过 Nginx 反向代理，解决了以下问题：
- ✅ CSP 错误：`frame-ancestors 'self'` 限制
- ✅ 跨域 Cookie 问题
- ✅ iframe 安全限制

## 快速开始（4 步）

### 1. 配置 Sim Studio 的 basePath

**重要**：Sim Studio 需要配置 `basePath` 才能在子路径下正常运行。

**Docker Compose 方式：**
在 `sim/docker-compose.local.yml` 中已经配置了 `NEXT_PUBLIC_BASE_PATH=/sim`，如果使用 Docker，直接启动即可。

**直接运行方式：**
```bash
export NEXT_PUBLIC_BASE_PATH=/sim
cd sim/apps/sim
npm run dev
```

**或者创建 `.env` 文件：**
在 `sim/apps/sim/.env.local` 中：
```bash
NEXT_PUBLIC_BASE_PATH=/sim
```

### 2. 配置前端环境变量

在 `frontend/.env.local` 中设置（如果文件不存在则创建）：

```bash
# 使用 Nginx 反向代理时，设置为空或相对路径
VITE_SIM_BASE_URL=
```

或者直接不创建这个文件，前端代码会自动使用相对路径 `/sim`。

### 3. 配置 Nginx

**Windows:**
```powershell
# 将 nginx.conf 复制到 Nginx 配置目录
copy nginx.conf C:\nginx\conf\nginx.conf

# 或使用启动脚本（会自动检查）
.\start-nginx.ps1
```

**Linux/macOS:**
```bash
# 将 nginx.conf 复制到 Nginx 配置目录
sudo cp nginx.conf /etc/nginx/sites-available/eduai
sudo ln -s /etc/nginx/sites-available/eduai /etc/nginx/sites-enabled/

# 或使用启动脚本
chmod +x start-nginx.sh
./start-nginx.sh
```

### 4. 启动服务

确保以下服务都在运行：
1. ✅ 后端 API (端口 8080)
2. ✅ Sim Studio (端口 3000) - **必须配置了 NEXT_PUBLIC_BASE_PATH=/sim**
3. ✅ 前端开发服务器 (端口 5173)
4. ✅ Nginx (端口 80)

## 访问地址

**重要**：必须通过 Nginx 访问，不要直接访问 `http://localhost:5173`

- 前端: `http://localhost/`
- Sim Studio: `http://localhost/sim/`
- 嵌入页面: `http://localhost/sim/embed/workbench`

## 验证

1. 访问 `http://localhost/` 应该看到 EduAI Studio 前端
2. 在班级详情页面点击"立即实验"按钮
3. 应该能够正常嵌入 Sim Studio，不再出现 CSP 错误

## 常见问题

### Q: iframe 中显示的是 EduAI 前端，而不是 Sim Studio？

A: 这是最常见的问题！确保：
1. **Sim Studio 配置了 `NEXT_PUBLIC_BASE_PATH=/sim`**（最重要！）
2. Sim Studio 服务已重启（环境变量更改后必须重启）
3. 通过 `http://localhost/` 访问，而不是 `http://localhost:5173`
4. 前端 `.env.local` 中 `VITE_SIM_BASE_URL` 为空或设置为 `/sim`

### Q: 仍然出现 CSP 错误？

A: 确保：
1. 通过 `http://localhost/` 访问，而不是 `http://localhost:5173`
2. 前端 `.env.local` 中 `VITE_SIM_BASE_URL` 为空或设置为 `/sim`
3. Nginx 配置已正确加载（使用 `nginx -s reload` 重新加载）

### Q: 502 Bad Gateway？

A: 检查后端服务是否运行：
- 后端 API: 端口 8080
- Sim Studio: 端口 3000
- 前端: 端口 5173

### Q: 如何查看 Nginx 日志？

**Windows:**
```
C:\nginx\logs\error.log
```

**Linux:**
```
/var/log/nginx/error.log
```

**macOS:**
```
/usr/local/var/log/nginx/error.log
```

## 详细文档

- [NGINX_SETUP.md](./NGINX_SETUP.md) - 详细配置指南
- [NGINX_FIX.md](./NGINX_FIX.md) - 问题修复说明（特别是 iframe 显示错误的问题）

