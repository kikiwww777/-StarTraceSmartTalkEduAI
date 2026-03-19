# 修复 iframe 嵌入问题 - 完整步骤

## 问题分析

当前使用的是预构建镜像 `ghcr.io/simstudioai/simstudio:latest`，代码修改不会生效。

## 解决方案

### 方案 1: 使用本地构建（推荐）

1. **停止当前服务**
   ```bash
   cd sim
   docker compose -f docker-compose.prod.yml down
   ```

2. **使用构建版本启动**
   ```bash
   docker compose -f docker-compose.prod.build.yml up -d --build
   ```

   这会：
   - 重新构建包含最新代码的镜像
   - 启动服务
   - 应用所有代码修改

3. **等待构建完成**
   构建可能需要几分钟时间，请耐心等待。

4. **检查服务状态**
   ```bash
   docker compose -f docker-compose.prod.build.yml ps
   ```

### 方案 2: 使用开发模式（最快）

1. **停止 Docker 服务**
   ```bash
   cd sim
   docker compose -f docker-compose.prod.yml down
   ```

2. **直接运行 Next.js 开发服务器**
   ```bash
   cd sim/apps/sim
   npm install  # 或 bun install
   npm run dev  # 或 bun dev
   ```

   这会在 `http://localhost:3000` 启动开发服务器，代码修改会立即生效。

### 方案 3: 检查预构建镜像的路由

如果 `/embed/workbench` 路由在预构建镜像中不存在，我们需要：

1. **检查路由是否存在**
   访问 `http://localhost:3000/embed/workbench` 看是否返回 404

2. **如果路由不存在**
   必须使用方案 1 或方案 2 重新构建

## 验证修复

### 1. 检查路由是否存在
在浏览器中直接访问：
```
http://localhost:3000/embed/workbench
```

应该看到页面内容，而不是 404。

### 2. 检查响应头
打开浏览器开发者工具 → Network 标签：
- 找到 `/embed/workbench` 请求
- 查看 Response Headers
- 应该看到：
  ```
  Content-Security-Policy: ... frame-ancestors 'self' http://localhost:5173 ...
  ```

### 3. 测试 iframe 嵌入
在前端页面点击"立即实验"按钮：
- ✅ 不再出现 CSP 错误
- ✅ iframe 正常加载
- ✅ 不再出现 404 错误

## 如果仍然有问题

### 检查清单

1. **服务是否使用最新代码？**
   - 如果使用预构建镜像，代码修改不会生效
   - 必须重新构建或使用开发模式

2. **路由文件是否存在？**
   ```bash
   ls sim/apps/sim/app/embed/workbench/page.tsx
   ```
   文件应该存在

3. **中间件是否正确配置？**
   检查 `sim/apps/sim/proxy.ts` 是否包含：
   ```typescript
   if (url.pathname.startsWith('/embed/')) {
     return NextResponse.next()
   }
   ```

4. **CSP 配置是否正确？**
   检查 `sim/apps/sim/lib/core/security/csp.ts` 中的 `getEmbedFrameAncestors()` 函数

5. **环境变量是否正确？**
   ```bash
   docker exec sim-simstudio-1 env | grep EDUAI
   ```
   应该看到 `EDUAI_EMBED_ALLOWED_ORIGINS`（可选，有默认值）

## 快速命令参考

```bash
# 停止服务
docker compose -f docker-compose.prod.yml down

# 使用本地构建启动
docker compose -f docker-compose.prod.build.yml up -d --build

# 查看日志
docker compose -f docker-compose.prod.build.yml logs -f simstudio

# 检查服务状态
docker compose -f docker-compose.prod.build.yml ps
```






































