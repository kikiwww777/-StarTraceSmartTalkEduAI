# 直接访问 localhost:3000 配置指南

## 说明

如果不想使用 Nginx 反向代理，可以直接让 iframe 嵌入 `http://localhost:3000`。

## 配置步骤

### 1. 确保前端代码使用 localhost:3000

前端代码已经更新，默认使用 `http://localhost:3000`。如果设置了环境变量 `VITE_SIM_BASE_URL`，会使用环境变量的值。

**可选配置**（如果需要覆盖默认值）：
在 `frontend/.env.local` 中：
```bash
VITE_SIM_BASE_URL=http://localhost:3000
```

### 2. 配置 Sim Studio 允许跨域嵌入

Sim Studio 需要配置允许 EduAI 前端（localhost:5173）通过 iframe 嵌入。

#### 方式 1: 使用 Docker Compose（推荐）

在 `sim/docker-compose.local.yml` 中已经配置了：
```yaml
environment:
  - EDUAI_EMBED_ALLOWED_ORIGINS=http://localhost:5173 http://127.0.0.1:5173
  - NEXT_PUBLIC_EDUAI_EMBED_ALLOWED_ORIGINS=http://localhost:5173 http://127.0.0.1:5173
```

确保这些环境变量已设置，然后重启 Sim Studio：
```bash
cd sim
docker-compose -f docker-compose.local.yml down
docker-compose -f docker-compose.local.yml up -d
```

#### 方式 2: 直接运行 Next.js

在 `sim/apps/sim/.env.local` 中设置：
```bash
EDUAI_EMBED_ALLOWED_ORIGINS=http://localhost:5173 http://127.0.0.1:5173
NEXT_PUBLIC_EDUAI_EMBED_ALLOWED_ORIGINS=http://localhost:5173 http://127.0.0.1:5173
```

然后重启 Next.js 开发服务器。

### 3. 验证配置

1. **检查 Sim Studio 是否运行**：
   ```bash
   curl http://localhost:3000
   ```

2. **检查 CSP 响应头**：
   访问 `http://localhost:3000/embed/workbench`，在浏览器开发者工具的 Network 标签中查看响应头：
   ```
   Content-Security-Policy: frame-ancestors 'self' http://localhost:5173 http://127.0.0.1:5173 ...
   ```
   
   应该包含 `http://localhost:5173`。

3. **测试 iframe 嵌入**：
   - 访问 EduAI 前端：`http://localhost:5173`
   - 在班级详情页面点击"立即实验"按钮
   - iframe 中应该显示 Sim Studio 的 `/embed/workbench` 页面

## 注意事项

### 跨域限制

直接使用 `localhost:3000` 会有跨域限制：
- ✅ **CSP frame-ancestors**：可以通过配置解决（如上）
- ⚠️ **Cookie 共享**：不同端口无法共享 Cookie，如果需要单点登录，需要使用 Nginx 反向代理

### 如果仍然出现 CSP 错误

1. **确认环境变量已设置**：
   ```bash
   # Docker 方式
   docker-compose -f sim/docker-compose.local.yml exec simstudio env | grep EDUAI_EMBED
   
   # 直接运行方式
   cat sim/apps/sim/.env.local | grep EDUAI_EMBED
   ```

2. **确认 Sim Studio 已重启**：
   环境变量更改后，必须重启服务才能生效。

3. **检查浏览器控制台**：
   查看是否有其他错误信息。

### 使用 Nginx 反向代理的优势

如果遇到跨域或 Cookie 问题，建议使用 Nginx 反向代理：
- ✅ 完全避免跨域问题（同一域名）
- ✅ 可以共享 Cookie，实现单点登录
- ✅ 更符合生产环境部署方式

参考：[NGINX_SETUP.md](./NGINX_SETUP.md)

## 故障排查

### 问题 1: 仍然出现 CSP 错误

**检查清单：**
- [ ] Sim Studio 环境变量 `EDUAI_EMBED_ALLOWED_ORIGINS` 是否包含 `http://localhost:5173`
- [ ] Sim Studio 服务是否已重启
- [ ] 浏览器控制台是否有其他错误

### 问题 2: iframe 显示空白

**检查清单：**
- [ ] Sim Studio 是否正在运行（端口 3000）
- [ ] 浏览器控制台是否有错误信息
- [ ] Network 标签中 `/embed/workbench` 请求是否成功（状态码 200）

### 问题 3: Cookie 无法共享

这是正常现象，因为不同端口被视为不同源。如果需要 Cookie 共享，请使用 Nginx 反向代理。





































