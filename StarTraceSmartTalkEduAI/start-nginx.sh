#!/bin/bash
# Bash 脚本：启动 Nginx 并验证配置
# 使用方法：chmod +x start-nginx.sh && ./start-nginx.sh

echo "=== Nginx 反向代理启动脚本 ==="

# 检查 Nginx 是否已安装
if ! command -v nginx &> /dev/null; then
    echo "错误: 未找到 Nginx，请先安装 Nginx"
    echo "安装方法:"
    echo "  Ubuntu/Debian: sudo apt install nginx"
    echo "  macOS: brew install nginx"
    echo "  或访问: https://nginx.org/en/download.html"
    exit 1
fi

# 检查配置文件语法
echo ""
echo "检查 Nginx 配置语法..."
if sudo nginx -t 2>&1 | grep -q "syntax is ok"; then
    echo "配置语法正确"
else
    echo "配置语法错误，请检查 nginx.conf"
    sudo nginx -t
    exit 1
fi

# 检查端口是否被占用
echo ""
echo "检查端口 80 是否被占用..."
if sudo lsof -i :80 &> /dev/null; then
    echo "警告: 端口 80 已被占用"
    echo "如果 Nginx 已运行，可以跳过启动步骤"
else
    echo "端口 80 可用"
fi

# 检查后端服务
echo ""
echo "检查后端服务 (端口 8080)..."
if lsof -i :8080 &> /dev/null || netstat -an | grep -q ":8080.*LISTEN"; then
    echo "后端服务运行中"
else
    echo "警告: 后端服务 (8080) 未运行"
fi

# 检查 Sim Studio (端口 3000)
echo ""
echo "检查 Sim Studio (端口 3000)..."
if lsof -i :3000 &> /dev/null || netstat -an | grep -q ":3000.*LISTEN"; then
    echo "Sim Studio 运行中"
else
    echo "警告: Sim Studio (3000) 未运行"
fi

# 检查前端服务 (端口 5173)
echo ""
echo "检查前端服务 (端口 5173)..."
if lsof -i :5173 &> /dev/null || netstat -an | grep -q ":5173.*LISTEN"; then
    echo "前端服务运行中"
else
    echo "警告: 前端服务 (5173) 未运行"
fi

# 启动 Nginx
echo ""
echo "启动 Nginx..."
if pgrep -x nginx > /dev/null; then
    echo "Nginx 已在运行，重新加载配置..."
    sudo nginx -s reload
    if [ $? -eq 0 ]; then
        echo "配置已重新加载"
    else
        echo "重新加载失败，尝试重启..."
        sudo nginx -s stop
        sleep 2
        sudo nginx
    fi
else
    sudo nginx
    if [ $? -eq 0 ]; then
        echo "Nginx 启动成功"
    else
        echo "Nginx 启动失败"
        exit 1
    fi
fi

echo ""
echo "=== 启动完成 ==="
echo "访问地址:"
echo "  前端: http://localhost/"
echo "  Sim Studio: http://localhost/sim/"
echo "  嵌入页面: http://localhost/sim/embed/workbench"
echo ""
echo "提示: 确保前端 .env.local 中 VITE_SIM_BASE_URL 为空或设置为 /sim"





































