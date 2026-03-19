# 修复 CSP 错误：frame-ancestors 'self'

## 错误信息

```
Framing 'http://localhost:3000/' violates the following Content Security Policy directive: "frame-ancestors 'self'". The request has been blocked.
```

## 问题原因

1. **basePath 配置问题**：如果 Sim Studio 配置了 `NEXT_PUBLIC_BASE_PATH=/sim`，路径会变成 `/sim/embed/workbench`，但访问的是根路径
2. **CSP 头未正确应用**：Sim Studio 的 CSP 配置可能没有正确应用到响应头
3. **服务未重启**：环境变量更改后，Sim Studio 服务未重启

## 解决方案

### 方案 1: 直接访问 localhost:3000（推荐，最简单）

#### 步骤 1: 移除 basePath 配置

**如果使用 Docker Compose：**

编辑 `sim/docker-compose.local.yml`，将 `NEXT_PUBLIC_BASE_PATH` 设置为空：

```yaml
- NEXT_PUBLIC_BASE_PATH=${NEXT_PUBLIC_BASE_PATH:-}
```

或者直接删除这一行。

**如果直接运行：**

确保 `.env.local` 中没有设置 `NEXT_PUBLIC_BASE_PATH`，或者设置为空：
```bash
# sim/apps/sim/.env.local
NEXT_PUBLIC_BASE_PATH=
```

#### 步骤 2: 确保环境变量已设置

**Docker Compose 方式：**

`docker-compose.local.yml` 中已经配置了：
```yaml
- EDUAI_EMBED_ALLOWED_ORIGINS=${EDUAI_EMBED_ALLOWED_ORIGINS:-http://localhost:5173 http://127.0.0.1:5173}
```

**直接运行方式：**

在 `sim/apps/sim/.env.local` 中：
```bash
EDUAI_EMBED_ALLOWED_ORIGINS=http://localhost:5173 http://127.0.0.1:5173
NEXT_PUBLIC_EDUAI_EMBED_ALLOWED_ORIGINS=http://localhost:5173 http://127.0.0.1:5173
```

#### 步骤 3: 重启 Sim Studio 服务

**Docker Compose：**
```bash
cd sim
docker-compose -f docker-compose.local.yml down
docker-compose -f docker-compose.local.yml up -d
```

**直接运行：**
```bash
# 停止当前服务（Ctrl+C）
# 然后重新启动
cd sim/apps/sim
npm run dev
```

#### 步骤 4: 验证 CSP 响应头

访问 `http://localhost:3000/embed/workbench`，在浏览器开发者工具的 Network 标签中：

1. 找到 `/embed/workbench` 请求
2. 查看 Response Headers
3. 应该看到：
   ```
   Content-Security-Policy: ... frame-ancestors 'self' http://localhost:5173 http://127.0.0.1:5173 ...
   ```

**重要**：必须包含 `http://localhost:5173`，否则仍然会被阻止。

#### 步骤 5: 测试 iframe 嵌入

1. 访问 EduAI 前端：`http://localhost:5173`
2. 在班级详情页面点击"立即实验"按钮
3. iframe 应该显示 Sim Studio 的 `/embed/workbench` 页面

### 方案 2: 使用 Nginx 反向代理

如果方案 1 不行，可以使用 Nginx 反向代理（参考 [NGINX_SETUP.md](./NGINX_SETUP.md)）。

## 故障排查

### 问题 1: 仍然显示 `frame-ancestors 'self'`

**检查清单：**
- [ ] Sim Studio 服务是否已重启（环境变量更改后必须重启）
- [ ] 环境变量 `EDUAI_EMBED_ALLOWED_ORIGINS` 是否已设置
- [ ] 访问的路径是否正确：`http://localhost:3000/embed/workbench`（不是根路径 `/`）

**验证环境变量（Docker）：**
```bash
docker-compose -f sim/docker-compose.local.yml exec simstudio env | grep EDUAI_EMBED
```

应该看到：
```
EDUAI_EMBED_ALLOWED_ORIGINS=http://localhost:5173 http://127.0.0.1:5173
```

### 问题 2: 404 错误

**检查：**
- [ ] 访问的路径是否正确：`/embed/workbench`（不是 `/`）
- [ ] Sim Studio 是否正在运行
- [ ] 如果设置了 `NEXT_PUBLIC_BASE_PATH`，路径会变成 `/sim/embed/workbench`

### 问题 3: basePath 冲突

如果之前配置了 `NEXT_PUBLIC_BASE_PATH=/sim`，现在想直接使用 `localhost:3000`：

1. **移除 basePath 配置**（设置为空或删除）
2. **重启 Sim Studio 服务**
3. **确保前端代码使用 `http://localhost:3000`**（不是 `/sim`）

## 快速检查脚本

### 检查 Sim Studio CSP 配置

```bash
# 检查响应头
curl -I http://localhost:3000/embed/workbench | grep -i "content-security-policy"
```

应该看到包含 `http://localhost:5173` 的 CSP 头。

### 检查环境变量（Docker）

```bash
cd sim
docker-compose -f docker-compose.local.yml exec simstudio env | grep -E "EDUAI_EMBED|BASE_PATH"
```

## 注意事项

1. **环境变量更改后必须重启服务**：Next.js 在启动时读取环境变量
2. **开发环境默认值**：即使不设置环境变量，Sim Studio 的代码中也包含了 `http://localhost:5173` 作为默认值，但为了确保，最好显式设置
3. **路径必须正确**：确保访问的是 `/embed/workbench`，不是根路径 `/`

## 如果仍然不行

1. **检查浏览器控制台**：查看完整的错误信息
2. **检查 Network 标签**：查看实际请求的 URL 和响应头
3. **检查 Sim Studio 日志**：查看是否有错误信息
4. **尝试直接访问**：在浏览器中直接访问 `http://localhost:3000/embed/workbench`，看是否能正常显示





































