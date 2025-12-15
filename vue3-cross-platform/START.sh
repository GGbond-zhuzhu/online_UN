#!/bin/bash

# 校园集成系统 - 前端启动脚本 (Git Bash)

echo "========================================"
echo "  校园集成系统 - 前端启动脚本"
echo "========================================"
echo ""

# 检查 Node.js 是否安装
if ! command -v node &> /dev/null; then
    echo "[错误] 未检测到 Node.js，请先安装 Node.js"
    echo "下载地址: https://nodejs.org/"
    exit 1
fi

echo "[1/3] 检查依赖..."
if [ ! -d "node_modules" ]; then
    echo "[信息] 正在安装根目录依赖..."
    npm install --legacy-peer-deps
    if [ $? -ne 0 ]; then
        echo "[错误] 依赖安装失败"
        exit 1
    fi
fi

if [ ! -d "packages/web/node_modules" ]; then
    echo "[信息] 正在安装 web 包依赖..."
    cd packages/web
    npm install --legacy-peer-deps
    cd ../..
    if [ $? -ne 0 ]; then
        echo "[错误] 依赖安装失败"
        exit 1
    fi
fi

if [ ! -d "packages/common/node_modules" ]; then
    echo "[信息] 正在安装 common 包依赖..."
    cd packages/common
    npm install --legacy-peer-deps
    cd ../..
    if [ $? -ne 0 ]; then
        echo "[错误] 依赖安装失败"
        exit 1
    fi
fi

echo "[2/3] 检查环境变量配置..."
if [ ! -f "packages/web/.env.development" ]; then
    echo "[信息] 创建环境变量文件..."
    cat > packages/web/.env.development << 'EOF'
VITE_API_BASE_URL=http://localhost:8080
VITE_API_DOCS_URL=http://localhost:8080/doc.html
VITE_APP_TITLE=校园集成系统-开发环境
EOF
    echo "[成功] 环境变量文件已创建"
fi

echo "[3/3] 启动开发服务器..."
echo ""
echo "========================================"
echo "  前端地址: http://localhost:5173"
echo "  API文档:  http://localhost:8080/doc.html"
echo "========================================"
echo ""
echo "按 Ctrl+C 停止服务器"
echo ""

cd packages/web
npm run dev

