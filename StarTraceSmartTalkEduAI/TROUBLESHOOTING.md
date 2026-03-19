# 问题排查指南

## 问题 1: CSP 错误 - "frame-ancestors 'self'"

### 错误信息
```
Framing 'http://localhost:3000/' violates the following Content Security Policy directive: "frame-ancestors 'self'". The request has been blocked.
```

### 原因分析
这个错误表示 Sim Studio 的 CSP 配置没有正确允许前端域名嵌入。可能的原因：
1. 环境变量 `EDUAI_EMBED_ALLOWED_ORIGINS` 没有正确设置
2. Next.js 服务需要重启才能读取新的环境变量
3. 环境变量格式不正确

### 解决方案

#### 步骤 1: 检查环境变量
确保 Sim Studio 的环境变量已正确设置：

**开发环境（docker-compose.local.yml）**:
```yaml
environment:
  - EDUAI_EMBED_ALLOWED_ORIGINS=http://localhost:5173 http://127.0.0.1:5173
  - NEXT_PUBLIC_EDUAI_EMBED_ALLOWED_ORIGINS=http://localhost:5173 http://127.0.0.1:5173
```

**或者通过 .env 文件**:
```bash
EDUAI_EMBED_ALLOWED_ORIGINS=http://localhost:5173 http://127.0.0.1:5173
NEXT_PUBLIC_EDUAI_EMBED_ALLOWED_ORIGINS=http://localhost:5173 http://127.0.0.1:5173
```

#### 步骤 2: 重启 Sim Studio 服务
环境变量更改后，必须重启服务：

```bash
# 如果使用 Docker Compose
docker-compose -f sim/docker-compose.local.yml down
docker-compose -f sim/docker-compose.local.yml up -d

# 如果直接运行 Next.js
# 停止当前服务，然后重新启动
```

#### 步骤 3: 验证 CSP 配置
访问 `http://localhost:3000/embed/workbench`，在浏览器开发者工具中检查响应头：

```
Content-Security-Policy: frame-ancestors 'self' http://localhost:5173 http://127.0.0.1:5173 ...
```

如果响应头中没有包含前端域名，说明环境变量没有正确传递。

#### 步骤 4: 检查前端 URL
确保前端访问的是正确的 URL：
- ✅ 正确: `http://localhost:3000/embed/workbench`
- ❌ 错误: `http://localhost:3000/` (根路径)

### 临时解决方案
如果问题仍然存在，可以临时修改 `sim/apps/sim/lib/core/security/csp.ts`，在开发环境中强制添加前端域名：

```typescript
function getEmbedFrameAncestors(): string[] {
  const configured = parseSpaceOrCommaSeparatedList(getEnv('EDUAI_EMBED_ALLOWED_ORIGINS'))
  
  const devDefaults = isDev
    ? [
        'http://localhost:5173',
        'http://127.0.0.1:5173',
        'http://localhost:5174',
        'http://127.0.0.1:5174',
        'http://localhost:8080',
        'http://127.0.0.1:8080',
        // 强制添加所有可能的本地地址
        'http://localhost:*',
        'http://127.0.0.1:*',
      ]
    : []
  
  return Array.from(new Set(["'self'", ...configured, ...devDefaults]))
}
```

**注意**: 生产环境不要使用 `*` 通配符，这是不安全的。

---

## 问题 2: 数据库错误 - "Column 'token_total' cannot be null"

### 错误信息
```
Column 'token_total' cannot be null
```

### 原因分析
在创建实验尝试记录时，`token_total` 等字段没有设置默认值，但数据库约束要求这些字段不能为 null。

### 解决方案

#### 已修复
已在 `SimExperimentServiceImpl.java` 的 `startAttempt` 方法中添加了默认值设置：

```java
// 设置 token 相关字段的默认值，避免数据库约束错误
if (attempt.getTokenTotal() == null) {
    attempt.setTokenTotal(0L);
}
if (attempt.getTokenPrompt() == null) {
    attempt.setTokenPrompt(0L);
}
if (attempt.getTokenCompletion() == null) {
    attempt.setTokenCompletion(0L);
}
if (attempt.getTokenEmbedding() == null) {
    attempt.setTokenEmbedding(0L);
}
```

#### 重新编译后端
修复后需要重新编译后端服务：

```bash
cd backend
mvn clean compile
# 或者
mvn clean package
```

然后重启后端服务。

---

## 问题 3: "localhost 拒绝连接"

### 错误信息
```
Failed to load resource: the server responded with a status of 404
localhost 拒绝连接
```

### 原因分析
1. Sim Studio 服务没有运行
2. 端口配置不正确
3. 前端访问的 URL 不正确

### 解决方案

#### 步骤 1: 检查 Sim Studio 是否运行
```bash
# 检查端口 3000 是否被占用
netstat -ano | findstr :3000  # Windows
lsof -i :3000  # Linux/Mac

# 或者访问
curl http://localhost:3000
```

#### 步骤 2: 检查前端配置
确保 `frontend/.env.local` 或环境变量中设置了正确的 Sim Studio URL：

```bash
VITE_SIM_BASE_URL=http://localhost:3000
```

#### 步骤 3: 检查 URL 路径
确保前端访问的是 `/embed/workbench` 而不是根路径 `/`。

---

## 完整排查流程

1. **检查环境变量**
   - Sim Studio: `EDUAI_EMBED_ALLOWED_ORIGINS`
   - 前端: `VITE_SIM_BASE_URL`

2. **重启所有服务**
   - Sim Studio (端口 3000)
   - 后端服务 (端口 8080)
   - 前端服务 (端口 5173)

3. **检查浏览器控制台**
   - 查看 CSP 错误
   - 查看网络请求状态
   - 查看响应头

4. **验证配置**
   - 访问 `http://localhost:3000/embed/workbench` 直接测试
   - 检查响应头中的 CSP 配置

---

## 常见问题

### Q: 为什么修改环境变量后还是不行？
A: Next.js 在启动时读取环境变量，修改后必须重启服务。

### Q: 开发环境需要配置环境变量吗？
A: 开发环境有默认值，但如果使用非标准端口（不是 5173），需要手动配置。

### Q: 生产环境如何配置？
A: 生产环境必须明确配置前端域名，例如：
```bash
EDUAI_EMBED_ALLOWED_ORIGINS=https://eduai.example.com https://www.eduai.example.com
```

### Q: 如何验证 CSP 配置是否正确？
A: 在浏览器开发者工具的 Network 标签中，查看 `/embed/workbench` 请求的响应头，应该包含 `Content-Security-Policy` 头，其中 `frame-ancestors` 应该包含前端域名。






































