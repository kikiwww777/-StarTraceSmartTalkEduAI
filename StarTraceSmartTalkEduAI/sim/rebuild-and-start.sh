#!/bin/bash
# Bash 脚本：重新构建并启动 Sim Studio

echo "正在停止当前服务..."
docker compose -f docker-compose.prod.yml down

echo ""
echo "正在使用本地构建启动服务..."
echo "这可能需要几分钟时间，请耐心等待..."

docker compose -f docker-compose.prod.build.yml up -d --build

echo ""
echo "等待服务启动..."
sleep 10

echo ""
echo "检查服务状态..."
docker compose -f docker-compose.prod.build.yml ps

echo ""
echo "查看日志（按 Ctrl+C 退出）..."
docker compose -f docker-compose.prod.build.yml logs -f simstudio






































