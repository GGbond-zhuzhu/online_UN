#!/bin/bash

echo "========================================"
echo "启动校园集成系统后端服务"
echo "========================================"
echo ""

# 检查Java环境
echo "检查Java环境..."
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请先安装JDK 17或更高版本"
    exit 1
fi
java -version

echo ""
# 检查Maven环境
echo "检查Maven环境..."
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven，请先安装Maven"
    exit 1
fi
mvn -version

echo ""
# 检查MySQL服务（Linux/Mac）
if command -v systemctl &> /dev/null; then
    echo "检查MySQL服务..."
    if systemctl is-active --quiet mysql || systemctl is-active --quiet mysqld; then
        echo "MySQL服务运行中"
    else
        echo "警告: MySQL服务可能未启动，请确保MySQL服务已启动"
    fi
fi

echo ""
echo "开始启动后端服务..."
echo "服务将在 http://localhost:8080 启动"
echo "API文档地址: http://localhost:8080/doc.html"
echo ""

# 启动服务
mvn spring-boot:run

