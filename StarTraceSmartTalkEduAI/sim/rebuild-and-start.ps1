# PowerShell 脚本：重新构建并启动 Sim Studio

Write-Host "正在停止当前服务..." -ForegroundColor Yellow
docker compose -f docker-compose.prod.yml down

Write-Host "`n正在使用本地构建启动服务..." -ForegroundColor Yellow
Write-Host "这可能需要几分钟时间，请耐心等待..." -ForegroundColor Cyan

docker compose -f docker-compose.prod.build.yml up -d --build

Write-Host "`n等待服务启动..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host "`n检查服务状态..." -ForegroundColor Yellow
docker compose -f docker-compose.prod.build.yml ps

Write-Host "`n查看日志（按 Ctrl+C 退出）..." -ForegroundColor Yellow
docker compose -f docker-compose.prod.build.yml logs -f simstudio






































