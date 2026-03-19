# Nginx 反向代理配置指南

本文档说明如何通过 Nginx 反向代理将 EduAI Studio 和 Sim Studio 统一到同一域名下，解决跨域和 iframe 限制问题。

## 问题描述

直接使用 `http://localhost:3000` 嵌入 Sim Studio 会遇到以下问题：
- CSP (Content Security Policy) 错误：`frame-ancestors 'self'` 限制
- 跨域 Cookie 问题
- iframe 安全限制

## 解决方案

通过 Nginx 反向代理，将两个服务统一到同一域名下：
- EduAI Studio: `http://localhost/` (或 `http://localhost:5173` 开发环境)
- Sim Studio: `http://localhost/sim/` (代理到 `http://localhost:3000/`)

这样两个服务在同一域名下，完全避免了跨域问题。

## 配置步骤

### 1. 安装 Nginx

**Windows:**
```powershell
# 使用 Chocolatey
choco install nginx

# 或下载安装包
# https://nginx.org/en/download.html
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install nginx
```

**macOS:**
```bash
brew install nginx
```

### 2. 配置 Nginx

将项目根目录的 `nginx.conf` 复制到 Nginx 配置目录：

**Windows:**
```powershell
# Nginx 默认配置目录通常在：
# C:\nginx\conf\nginx.conf
# 或安装目录下的 conf\nginx.conf

# 备份原配置
copy nginx.conf C:\nginx\conf\nginx.conf.backup
copy nginx.conf C:\nginx\conf\nginx.conf
```

**Linux:**
```bash
# 备份原配置
sudo cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.backup

# 将项目配置合并到主配置文件，或创建站点配置
sudo cp nginx.conf /etc/nginx/sites-available/eduai
sudo ln -s /etc/nginx/sites-available/eduai /etc/nginx/sites-enabled/

# 或直接替换主配置（不推荐，除非是专用服务器）
# sudo cp nginx.conf /etc/nginx/nginx.conf
```

**macOS:**
```bash
# 使用 Homebrew 安装的 Nginx 配置目录
sudo cp nginx.conf /usr/local/etc/nginx/nginx.conf
```

### 3. 修改配置（如需要）

根据实际情况修改 `nginx.conf` 中的端口：

- **开发环境**：前端 Vite dev server 在 5173，Sim Studio 在 3000
- **生产环境**：前端构建后可能部署在 3001 或其他端口

编辑 `nginx.conf`，确认以下配置：
```nginx
# 开发环境
location / {
    proxy_pass http://127.0.0.1:5173;  # Vite dev server
}

# 生产环境（如果前端构建后部署在 3001）
# location / {
#     proxy_pass http://127.0.0.1:3001;
# }
```

### 4. 配置前端环境变量

在 `frontend/.env.local` 中设置（如果文件不存在则创建）：

```bash
# 使用 Nginx 反向代理时，设置为空或相对路径
VITE_SIM_BASE_URL=
# 或
VITE_SIM_BASE_URL=/sim
```

**重要**：如果不设置 `VITE_SIM_BASE_URL` 或设置为空，前端代码会自动使用相对路径 `/sim`。

### 5. 启动服务

#### 启动 Nginx

**Windows:**
```powershell
# 启动 Nginx
C:\nginx\nginx.exe

# 或使用服务（如果已安装为服务）
net start nginx

# 重新加载配置（修改配置后）
C:\nginx\nginx.exe -s reload
```

**Linux:**
```bash
# 启动 Nginx
sudo systemctl start nginx

# 设置开机自启
sudo systemctl enable nginx

# 重新加载配置
sudo nginx -s reload
# 或
sudo systemctl reload nginx
```

**macOS:**
```bash
# 启动 Nginx
sudo nginx

# 重新加载配置
sudo nginx -s reload
```

#### 启动应用服务

1. **启动后端 API** (端口 8080)
2. **启动 Sim Studio** (端口 3000)
3. **启动前端开发服务器** (端口 5173，如果使用开发模式)

### 6. 访问应用

通过 Nginx 访问：
- **前端**: `http://localhost/` (通过 Nginx 代理到 5173)
- **Sim Studio**: `http://localhost/sim/` (通过 Nginx 代理到 3000)

**注意**：如果直接访问 `http://localhost:5173`，仍然会遇到跨域问题。必须通过 Nginx 访问。

## 验证配置

### 1. 检查 Nginx 配置语法

```bash
# Windows
C:\nginx\nginx.exe -t

# Linux/macOS
sudo nginx -t
```

### 2. 测试访问

1. 访问 `http://localhost/` 应该看到 EduAI Studio 前端
2. 访问 `http://localhost/sim/` 应该看到 Sim Studio
3. 访问 `http://localhost/sim/embed/workbench` 应该看到嵌入页面

### 3. 检查响应头

在浏览器开发者工具的 Network 标签中，查看 `/sim/embed/workbench` 请求的响应头：

应该看到：
```
Content-Security-Policy: frame-ancestors 'self'
```

不应该看到：
```
X-Frame-Options: DENY
```

### 4. 测试 iframe 嵌入

在班级详情页面点击"立即实验"按钮，应该能够正常嵌入 Sim Studio，不再出现 CSP 错误。

## 生产环境配置

### HTTPS 配置

1. 获取 SSL 证书（Let's Encrypt 或其他 CA）
2. 取消注释 `nginx.conf` 中的 HTTPS server 块
3. 修改 SSL 证书路径：
   ```nginx
   ssl_certificate /path/to/cert.pem;
   ssl_certificate_key /path/to/key.pem;
   ```
4. 启用 HTTP 到 HTTPS 重定向（取消注释 `return 301`）

### 前端构建

生产环境通常需要构建前端：

```bash
cd frontend
npm run build
```

然后将构建产物部署到静态文件服务器，或使用 Nginx 直接服务静态文件。

## 故障排查

### 问题 1: 502 Bad Gateway

**原因**：后端服务未启动或端口不正确

**解决**：
1. 检查后端服务是否运行
2. 检查 `nginx.conf` 中的 `proxy_pass` 端口是否正确

### 问题 2: 404 Not Found

**原因**：路径配置不正确

**解决**：
1. 检查 `location /sim/` 配置是否正确
2. 确认 `proxy_pass` 末尾的 `/` 是否正确（`http://127.0.0.1:3000/`）

### 问题 3: WebSocket 连接失败

**原因**：WebSocket 代理配置不正确

**解决**：
1. 确认已配置 `proxy_http_version 1.1`
2. 确认已配置 `Upgrade` 和 `Connection` 头

### 问题 4: 仍然出现 CSP 错误

**原因**：CSP 头未正确替换

**解决**：
1. 检查 `proxy_hide_header Content-Security-Policy` 是否生效
2. 检查 `add_header Content-Security-Policy` 是否正确添加
3. 使用 `always` 参数确保在所有响应中添加头

## 注意事项

1. **开发环境**：如果使用 Vite dev server，确保 Nginx 代理到正确的端口（5173）
2. **生产环境**：如果前端构建后部署，确保 Nginx 代理到正确的端口或静态文件目录
3. **路径匹配**：`location /sim/` 必须放在 `location /` 之前，因为 Nginx 按最长匹配优先
4. **CSP 头**：使用 `'self'` 允许同域嵌入，这是最安全的配置

## 参考

- [Nginx 官方文档](https://nginx.org/en/docs/)
- [Nginx 反向代理配置](https://nginx.org/en/docs/http/ngx_http_proxy_module.html)
- [Content Security Policy](https://developer.mozilla.org/en-US/docs/Web/HTTP/CSP)





































