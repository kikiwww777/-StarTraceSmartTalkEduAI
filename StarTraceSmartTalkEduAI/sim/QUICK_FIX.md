# 快速修复指南 - iframe 嵌入问题

## 已修复的问题

### 1. 中间件未处理 `/embed/*` 路由
- ✅ 在 `proxy.ts` 中添加了对 `/embed/*` 路由的明确处理
- ✅ 在中间件 matcher 中添加了 `/embed/:path*` 路由

### 2. CSP 配置优化
- ✅ 优化了 `csp.ts`，确保开发环境默认包含所有常见的本地地址
- ✅ 移除了对 `isDev` 的依赖，确保在所有环境下都能正常工作

## 必须重启服务

**重要**: 修改代码后必须重启 Sim Studio 服务才能生效！

### 如果使用 Docker Compose:
```bash
cd sim
docker compose -f docker-compose.prod.yml down
docker compose -f docker-compose.prod.yml up -d
```

### 如果直接运行 Next.js:
```bash
# 停止当前服务 (Ctrl+C)
# 然后重新启动
npm run dev
# 或
bun dev
```

## 验证修复

### 1. 检查服务是否运行
```bash
# 检查端口 3000 是否被占用
netstat -ano | findstr :3000  # Windows
lsof -i :3000  # Linux/Mac

# 或直接访问
curl http://localhost:3000/embed/workbench
```

### 2. 检查响应头
在浏览器中访问 `http://localhost:3000/embed/workbench`，打开开发者工具：
- 进入 Network 标签
- 刷新页面
- 点击 `/embed/workbench` 请求
- 查看 Response Headers

应该看到：
```
Content-Security-Policy: ... frame-ancestors 'self' http://localhost:5173 http://127.0.0.1:5173 ...
```

### 3. 测试 iframe 嵌入
在前端页面中点击"立即实验"按钮，应该：
- ✅ 不再出现 CSP 错误
- ✅ iframe 正常加载
- ✅ 不再出现 404 错误

## 如果问题仍然存在

### 检查清单

1. **服务是否已重启？**
   - 修改代码后必须重启服务

2. **环境变量是否正确？**
   - 检查 `EDUAI_EMBED_ALLOWED_ORIGINS` 是否设置（可选，有默认值）
   - 检查 `NEXT_PUBLIC_APP_URL` 是否正确

3. **前端 URL 是否正确？**
   - 确保前端访问的是 `http://localhost:3000/embed/workbench`
   - 不是根路径 `http://localhost:3000/`

4. **浏览器缓存？**
   - 尝试硬刷新 (Ctrl+Shift+R 或 Cmd+Shift+R)
   - 或使用无痕模式测试

5. **检查日志**
   ```bash
   # Docker Compose
   docker compose -f docker-compose.prod.yml logs -f simstudio
   
   # 直接运行
   # 查看终端输出
   ```

### 调试步骤

1. **直接访问测试**
   在浏览器中直接访问：
   ```
   http://localhost:3000/embed/workbench
   ```
   如果这个 URL 可以访问，说明路由配置正确。

2. **检查中间件日志**
   如果设置了 `NODE_ENV=development`，中间件会输出日志：
   ```
   Proxy request { pathname: '/embed/workbench', hasActiveSession: ... }
   ```

3. **检查 CSP 配置**
   在浏览器控制台中运行：
   ```javascript
   // 检查当前页面的 CSP
   document.querySelector('meta[http-equiv="Content-Security-Policy"]')
   ```

## 常见错误

### 错误 1: "frame-ancestors 'self'"
**原因**: CSP 配置没有正确应用或环境变量未传递

**解决**:
1. 重启服务
2. 检查环境变量
3. 查看响应头中的 CSP 值

### 错误 2: "404 Not Found"
**原因**: 路由未正确注册或中间件拦截

**解决**:
1. 确认 `/embed/workbench` 路由存在
2. 检查中间件是否允许该路由
3. 重启服务

### 错误 3: "ERR_BLOCKED_BY_RESPONSE"
**原因**: CSP 或 X-Frame-Options 阻止嵌入

**解决**:
1. 检查响应头中的 CSP
2. 确认 `X-Frame-Options` 没有设置为 `DENY` 或 `SAMEORIGIN`
3. 确认 `frame-ancestors` 包含前端域名

## 联系支持

如果以上步骤都无法解决问题，请提供：
1. 浏览器控制台的完整错误信息
2. Network 标签中 `/embed/workbench` 请求的响应头
3. Sim Studio 服务的日志输出
4. 使用的启动方式（Docker Compose 或直接运行）






































