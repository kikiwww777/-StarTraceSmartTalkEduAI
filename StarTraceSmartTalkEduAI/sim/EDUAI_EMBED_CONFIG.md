# EDUAI iframe 嵌入配置指南

本文档说明如何配置 Sim Studio 以允许 EDUAI 前端通过 iframe 嵌入实验工作台。

## 环境变量配置

### 必需的环境变量

#### 1. `EDUAI_EMBED_ALLOWED_ORIGINS`
**用途**: 允许通过 iframe 嵌入 Sim Studio `/embed/*` 页面的前端域名列表

**格式**: 使用空格或逗号分隔的域名列表

**示例**:
```bash
# 开发环境
EDUAI_EMBED_ALLOWED_ORIGINS=http://localhost:5173 http://127.0.0.1:5173

# 生产环境
EDUAI_EMBED_ALLOWED_ORIGINS=https://eduai.example.com https://www.eduai.example.com
```

**说明**:
- 此变量用于配置 CSP (Content Security Policy) 的 `frame-ancestors` 指令
- 必须包含完整的前端域名（包括协议和端口）
- 开发环境默认包含 `http://localhost:5173` 等本地地址

#### 2. `NEXT_PUBLIC_EDUAI_EMBED_ALLOWED_ORIGINS`
**用途**: 客户端代码中用于验证消息来源的允许域名列表

**格式**: 与 `EDUAI_EMBED_ALLOWED_ORIGINS` 相同

**说明**:
- 此变量用于 `embed/workbench/page.tsx` 中的消息来源验证
- 通常与 `EDUAI_EMBED_ALLOWED_ORIGINS` 设置为相同值

#### 3. `NEXT_PUBLIC_EDUAI_BACKEND_URL`
**用途**: EDUAI 后端 API 地址，用于实验事件上报

**示例**:
```bash
# 开发环境
NEXT_PUBLIC_EDUAI_BACKEND_URL=http://localhost:8080

# 生产环境
NEXT_PUBLIC_EDUAI_BACKEND_URL=https://api.eduai.example.com
```

## 配置方式

### 方式一：使用 Docker Compose（推荐）

在 `docker-compose.local.yml` 或 `docker-compose.prod.yml` 中配置：

```yaml
services:
  simstudio:
    environment:
      - EDUAI_EMBED_ALLOWED_ORIGINS=${EDUAI_EMBED_ALLOWED_ORIGINS:-http://localhost:5173 http://127.0.0.1:5173}
      - NEXT_PUBLIC_EDUAI_EMBED_ALLOWED_ORIGINS=${EDUAI_EMBED_ALLOWED_ORIGINS:-http://localhost:5173 http://127.0.0.1:5173}
      - NEXT_PUBLIC_EDUAI_BACKEND_URL=${NEXT_PUBLIC_EDUAI_BACKEND_URL:-http://localhost:8080}
```

然后在项目根目录创建 `.env` 文件：

```bash
EDUAI_EMBED_ALLOWED_ORIGINS=http://localhost:5173 http://127.0.0.1:5173
NEXT_PUBLIC_EDUAI_BACKEND_URL=http://localhost:8080
```

### 方式二：直接设置环境变量

```bash
export EDUAI_EMBED_ALLOWED_ORIGINS="http://localhost:5173 http://127.0.0.1:5173"
export NEXT_PUBLIC_EDUAI_EMBED_ALLOWED_ORIGINS="http://localhost:5173 http://127.0.0.1:5173"
export NEXT_PUBLIC_EDUAI_BACKEND_URL="http://localhost:8080"
```

### 方式三：在 `.env.local` 文件中配置（Next.js 开发环境）

在 `sim/apps/sim/.env.local` 文件中：

```bash
EDUAI_EMBED_ALLOWED_ORIGINS=http://localhost:5173 http://127.0.0.1:5173
NEXT_PUBLIC_EDUAI_EMBED_ALLOWED_ORIGINS=http://localhost:5173 http://127.0.0.1:5173
NEXT_PUBLIC_EDUAI_BACKEND_URL=http://localhost:8080
```

## 工作原理

1. **CSP 配置**: `EDUAI_EMBED_ALLOWED_ORIGINS` 被用于生成 CSP `frame-ancestors` 指令，允许指定的前端域名通过 iframe 嵌入 `/embed/*` 路由。

2. **消息验证**: `NEXT_PUBLIC_EDUAI_EMBED_ALLOWED_ORIGINS` 用于验证来自前端页面的 postMessage 消息来源。

3. **事件上报**: `NEXT_PUBLIC_EDUAI_BACKEND_URL` 用于将实验事件上报到 EDUAI 后端。

## 验证配置

配置完成后，可以通过以下方式验证：

1. **检查 HTTP 响应头**: 访问 `http://localhost:3000/embed/workbench`，检查响应头中的 `Content-Security-Policy`，应该包含 `frame-ancestors` 指令。

2. **测试 iframe 嵌入**: 在前端页面中尝试嵌入 iframe：
   ```html
   <iframe src="http://localhost:3000/embed/workbench"></iframe>
   ```

3. **检查浏览器控制台**: 如果配置正确，不应该看到 CSP 相关的错误。

## 常见问题

### Q: 仍然看到 "拒绝连接" 错误？
A: 检查以下几点：
- 确保 `EDUAI_EMBED_ALLOWED_ORIGINS` 包含完整的前端域名（包括协议和端口）
- 确保环境变量已正确加载（重启服务）
- 检查浏览器控制台的 CSP 错误信息

### Q: 开发环境需要配置吗？
A: 开发环境默认包含 `http://localhost:5173` 等本地地址，但如果使用其他端口，需要手动配置。

### Q: 生产环境如何配置？
A: 生产环境必须明确配置前端域名，例如：
```bash
EDUAI_EMBED_ALLOWED_ORIGINS=https://eduai.example.com https://www.eduai.example.com
```

## 安全注意事项

- 不要将 `EDUAI_EMBED_ALLOWED_ORIGINS` 设置为 `*`，这会允许任何网站嵌入
- 只添加信任的前端域名
- 生产环境使用 HTTPS
- 定期审查允许的域名列表






































