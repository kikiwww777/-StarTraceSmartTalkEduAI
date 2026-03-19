# Nginx 反向代理配置修复说明

## 问题

使用 Nginx 反向代理时，iframe 中显示的是 EduAI 前端界面，而不是 Sim Studio。

## 原因

Sim Studio 是 Next.js 应用，当部署在子路径（如 `/sim/`）时，需要配置 `basePath`，否则静态资源路径会不正确。

## 解决方案

### 1. 配置 Sim Studio 的 basePath

在 Sim Studio 的环境变量中设置 `NEXT_PUBLIC_BASE_PATH=/sim`：

**Docker Compose 方式：**
```yaml
environment:
  - NEXT_PUBLIC_BASE_PATH=/sim
```

**或者通过 .env 文件：**
```bash
NEXT_PUBLIC_BASE_PATH=/sim
```

**直接运行 Next.js 开发服务器：**
```bash
export NEXT_PUBLIC_BASE_PATH=/sim
npm run dev
```

### 2. 更新 Nginx 配置

Nginx 配置已经更新，现在 `/sim/` 路径会直接代理到 Sim Studio，不需要移除前缀（因为 Next.js 的 basePath 会自动处理）。

### 3. 重启服务

配置更改后，需要重启 Sim Studio 服务：

**Docker Compose：**
```bash
cd sim
docker-compose -f docker-compose.local.yml down
docker-compose -f docker-compose.local.yml up -d
```

**直接运行：**
```bash
# 停止当前服务，然后重新启动
npm run dev
```

### 4. 验证

1. 访问 `http://localhost/sim/` 应该看到 Sim Studio 主页
2. 访问 `http://localhost/sim/embed/workbench` 应该看到嵌入页面
3. 在 EduAI 前端点击"立即实验"按钮，iframe 中应该显示 Sim Studio，而不是 EduAI 前端

## 配置说明

### Next.js basePath 的作用

当配置 `basePath: '/sim'` 时：
- Next.js 会在所有路由前添加 `/sim` 前缀
- 静态资源路径会变成 `/sim/_next/static/...`
- 所有内部链接会自动包含 `/sim` 前缀

### Nginx 配置要点

```nginx
location /sim/ {
    # 直接代理，不移除前缀
    # 因为 Next.js 配置了 basePath，它会自动处理路径
    proxy_pass http://127.0.0.1:3000;
    # ... 其他配置
}
```

## 故障排查

### 问题 1: 仍然显示 EduAI 前端

**检查：**
1. Sim Studio 是否配置了 `NEXT_PUBLIC_BASE_PATH=/sim`
2. Sim Studio 服务是否已重启
3. Nginx 配置是否正确加载（使用 `nginx -t` 检查）

### 问题 2: 404 错误

**检查：**
1. Sim Studio 是否正在运行（端口 3000）
2. Nginx 代理配置是否正确
3. 浏览器控制台是否有错误信息

### 问题 3: 静态资源加载失败

**检查：**
1. Next.js 的 basePath 是否正确配置
2. 浏览器 Network 标签中，静态资源请求的路径是否正确（应该是 `/sim/_next/...`）

## 注意事项

- 如果**不使用** Nginx 反向代理，不要设置 `NEXT_PUBLIC_BASE_PATH`，或者设置为空
- 如果**使用** Nginx 反向代理，必须设置 `NEXT_PUBLIC_BASE_PATH=/sim`
- 修改环境变量后，必须重启 Sim Studio 服务才能生效





































