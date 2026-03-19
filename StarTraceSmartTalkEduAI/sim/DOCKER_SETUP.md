# Docker Compose 配置说明

## 已修复的问题

### 1. volumes 配置错误
已修复 `docker-compose.prod.yml` 文件中 `volumes` 部分的格式错误。

## 环境变量警告说明

### 警告信息
```
The "COPILOT_API_KEY" variable is not set. Defaulting to a blank string.
The "SIM_AGENT_API_URL" variable is not set. Defaulting to a blank string.
```

这些警告是**正常的**，不会影响服务启动。这些变量是可选的：
- `COPILOT_API_KEY`: 用于 Copilot 功能的 API 密钥（可选）
- `SIM_AGENT_API_URL`: Sim Agent API 的 URL（可选）

### 如何消除警告（可选）

如果你想消除这些警告，可以：

#### 方法 1: 在 `.env` 文件中设置
在 `sim` 目录下创建 `.env` 文件：

```bash
# 可选：Copilot API 密钥
COPILOT_API_KEY=

# 可选：Sim Agent API URL
SIM_AGENT_API_URL=
```

#### 方法 2: 在 docker-compose 文件中设置默认值
修改 `docker-compose.prod.yml`：

```yaml
- COPILOT_API_KEY=${COPILOT_API_KEY:-}
- SIM_AGENT_API_URL=${SIM_AGENT_API_URL:-}
```

**注意**: 这些警告不影响功能，可以安全忽略。

## 必需的环境变量

以下环境变量有默认值，但建议在生产环境中明确设置：

### 核心配置
- `BETTER_AUTH_SECRET`: 认证密钥（至少 32 字符）
- `ENCRYPTION_KEY`: 加密密钥（至少 32 字符）
- `NEXT_PUBLIC_APP_URL`: 应用基础 URL
- `POSTGRES_USER`: 数据库用户名（默认: postgres）
- `POSTGRES_PASSWORD`: 数据库密码（默认: postgres）
- `POSTGRES_DB`: 数据库名称（默认: simstudio）

### EDUAI 集成配置
- `EDUAI_EMBED_ALLOWED_ORIGINS`: 允许 iframe 嵌入的前端域名
- `NEXT_PUBLIC_EDUAI_BACKEND_URL`: EDUAI 后端 API 地址

## 启动服务

### 使用预构建镜像（推荐）
```bash
docker compose -f docker-compose.prod.yml up -d
```

### 本地构建
```bash
docker compose -f docker-compose.prod.build.yml up -d
```

### 开发环境
```bash
docker compose -f docker-compose.local.yml up -d
```

## 验证服务

启动后，检查服务状态：
```bash
docker compose -f docker-compose.prod.yml ps
```

检查日志：
```bash
docker compose -f docker-compose.prod.yml logs -f simstudio
```

访问服务：
- Sim Studio: http://localhost:3000
- Realtime: http://localhost:3002
- PostgreSQL: localhost:5432

## 常见问题

### Q: 服务启动失败？
A: 检查：
1. 端口是否被占用（3000, 3002, 5432）
2. 环境变量是否正确设置
3. Docker 是否有足够的内存（至少 8GB）

### Q: 数据库连接失败？
A: 确保：
1. `db` 服务已启动并健康
2. `DATABASE_URL` 环境变量正确
3. 数据库迁移已完成

### Q: 如何重置数据库？
A: 
```bash
# 停止服务
docker compose -f docker-compose.prod.yml down

# 删除数据卷
docker volume rm sim_postgres_data

# 重新启动
docker compose -f docker-compose.prod.yml up -d
```

## 生产环境建议

1. **设置强密码**: 修改 `POSTGRES_PASSWORD` 和认证密钥
2. **使用 HTTPS**: 设置 `NEXT_PUBLIC_APP_URL` 为 HTTPS URL
3. **配置域名**: 在 `EDUAI_EMBED_ALLOWED_ORIGINS` 中设置实际的前端域名
4. **备份数据**: 定期备份 `postgres_data` 卷
5. **监控资源**: 监控内存和 CPU 使用情况






































