# 快速修复 404 错误：/embed/workbench 不存在

## 问题

访问 `http://localhost:3000/embed/workbench` 返回 404，说明路由不存在。

## 原因

可能使用了预构建的 Docker 镜像，代码没有包含 `/embed/workbench` 路由。

## 解决方案（选择其一）

### 方案 1: 使用本地构建的 Docker Compose（推荐）

```bash
cd sim

# 停止当前服务（如果正在运行）
docker-compose -f docker-compose.local.yml down
# 或者如果是其他 compose 文件
docker-compose down

# 使用本地构建启动
docker-compose -f docker-compose.local.yml up -d --build
```

这会重新构建镜像，包含最新的代码。

### 方案 2: 直接运行开发服务器（最快）

```bash
# 停止 Docker 服务
cd sim
docker-compose -f docker-compose.local.yml down

# 进入 Sim Studio 目录
cd apps/sim

# 安装依赖（如果还没安装）
npm install
# 或
bun install

# 启动开发服务器
npm run dev
# 或
bun dev
```

这会在 `http://localhost:3000` 启动开发服务器，代码修改会立即生效。

### 方案 3: 检查当前使用的 Docker Compose 文件

```bash
cd sim

# 查看所有 compose 文件
ls docker-compose*.yml

# 检查当前运行的服务
docker-compose ps
# 或
docker ps | grep sim
```

**如果使用的是 `docker-compose.prod.yml`（预构建镜像）：**
```bash
# 停止
docker-compose -f docker-compose.prod.yml down

# 切换到本地构建版本
docker-compose -f docker-compose.local.yml up -d --build
```

## 验证

### 1. 检查服务是否运行

```bash
# Docker 方式
docker-compose -f docker-compose.local.yml ps

# 或直接检查端口
curl http://localhost:3000
```

### 2. 检查路由是否存在

在浏览器中直接访问：
```
http://localhost:3000/embed/workbench
```

**应该看到页面内容，而不是 404。**

### 3. 检查响应头

打开浏览器开发者工具 → Network 标签：
- 找到 `/embed/workbench` 请求
- 查看 Response Headers
- 应该看到：
  ```
  Content-Security-Policy: ... frame-ancestors 'self' http://localhost:5173 ...
  ```

## 常见问题

### Q: 构建时间太长？

A: 使用方案 2（直接运行开发服务器），启动速度最快。

### Q: 如何确认使用的是哪个 Docker Compose 文件？

A: 查看 `sim/docker-compose.local.yml` 中的 `build` 配置：
```yaml
services:
  simstudio:
    build:  # 有 build 说明是本地构建
      context: .
      dockerfile: docker/app.Dockerfile
```

如果是 `image: ghcr.io/...`，说明使用的是预构建镜像。

### Q: 开发服务器启动后还是 404？

A: 检查：
1. 路由文件是否存在：`ls sim/apps/sim/app/embed/workbench/page.tsx`
2. Next.js 是否正常启动（查看控制台输出）
3. 访问其他路由（如 `http://localhost:3000/`）是否正常

## 推荐配置

**开发环境：** 使用方案 2（直接运行开发服务器）
- ✅ 启动快
- ✅ 代码修改立即生效
- ✅ 便于调试

**生产环境：** 使用方案 1（本地构建 Docker 镜像）
- ✅ 环境一致
- ✅ 便于部署





































