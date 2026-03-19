# PowerShell 脚本：启动 Nginx 并验证配置
# 使用方法：.\start-nginx.ps1

Write-Host "=== Nginx 反向代理启动脚本 ===" -ForegroundColor Cyan

# 检查 Nginx 是否已安装
$nginxPath = "C:\nginx\nginx.exe"
if (-not (Test-Path $nginxPath)) {
    Write-Host "错误: 未找到 Nginx，请先安装 Nginx" -ForegroundColor Red
    Write-Host "安装方法:" -ForegroundColor Yellow
    Write-Host "  1. 下载: https://nginx.org/en/download.html" -ForegroundColor Yellow
    Write-Host "  2. 解压到 C:\nginx\" -ForegroundColor Yellow
    Write-Host "  3. 将项目根目录的 nginx.conf 复制到 C:\nginx\conf\nginx.conf" -ForegroundColor Yellow
    exit 1
}

# 检查配置文件语法
Write-Host "`n检查 Nginx 配置语法..." -ForegroundColor Yellow
$testResult = & $nginxPath -t 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "配置语法错误:" -ForegroundColor Red
    Write-Host $testResult
    exit 1
}
Write-Host "配置语法正确" -ForegroundColor Green

# 检查端口是否被占用
Write-Host "`n检查端口 80 是否被占用..." -ForegroundColor Yellow
$port80 = Get-NetTCPConnection -LocalPort 80 -ErrorAction SilentlyContinue
if ($port80) {
    Write-Host "警告: 端口 80 已被占用" -ForegroundColor Yellow
    Write-Host "如果 Nginx 已运行，可以跳过启动步骤" -ForegroundColor Yellow
} else {
    Write-Host "端口 80 可用" -ForegroundColor Green
}

# 检查后端服务
Write-Host "`n检查后端服务 (端口 8080)..." -ForegroundColor Yellow
$port8080 = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if (-not $port8080) {
    Write-Host "警告: 后端服务 (8080) 未运行" -ForegroundColor Yellow
} else {
    Write-Host "后端服务运行中" -ForegroundColor Green
}

# 检查 Sim Studio (端口 3000)
Write-Host "`n检查 Sim Studio (端口 3000)..." -ForegroundColor Yellow
$port3000 = Get-NetTCPConnection -LocalPort 3000 -ErrorAction SilentlyContinue
if (-not $port3000) {
    Write-Host "警告: Sim Studio (3000) 未运行" -ForegroundColor Yellow
} else {
    Write-Host "Sim Studio 运行中" -ForegroundColor Green
}

# 检查前端服务 (端口 5173)
Write-Host "`n检查前端服务 (端口 5173)..." -ForegroundColor Yellow
$port5173 = Get-NetTCPConnection -LocalPort 5173 -ErrorAction SilentlyContinue
if (-not $port5173) {
    Write-Host "警告: 前端服务 (5173) 未运行" -ForegroundColor Yellow
} else {
    Write-Host "前端服务运行中" -ForegroundColor Green
}

# 启动 Nginx
Write-Host "`n启动 Nginx..." -ForegroundColor Yellow
$nginxProcess = Get-Process -Name nginx -ErrorAction SilentlyContinue
if ($nginxProcess) {
    Write-Host "Nginx 已在运行，重新加载配置..." -ForegroundColor Yellow
    & $nginxPath -s reload
    if ($LASTEXITCODE -eq 0) {
        Write-Host "配置已重新加载" -ForegroundColor Green
    } else {
        Write-Host "重新加载失败，尝试重启..." -ForegroundColor Yellow
        & $nginxPath -s stop
        Start-Sleep -Seconds 2
        & $nginxPath
    }
} else {
    & $nginxPath
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Nginx 启动成功" -ForegroundColor Green
    } else {
        Write-Host "Nginx 启动失败" -ForegroundColor Red
        exit 1
    }
}

Write-Host "`n=== 启动完成 ===" -ForegroundColor Cyan
Write-Host "访问地址:" -ForegroundColor Yellow
Write-Host "  前端: http://localhost/" -ForegroundColor Green
Write-Host "  Sim Studio: http://localhost/sim/" -ForegroundColor Green
Write-Host "  嵌入页面: http://localhost/sim/embed/workbench" -ForegroundColor Green
Write-Host "`n提示: 确保前端 .env.local 中 VITE_SIM_BASE_URL 为空或设置为 /sim" -ForegroundColor Yellow





































