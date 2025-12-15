@echo off
chcp 65001 >nul
echo ========================================
echo   校园集成系统 - 前端启动脚本
echo ========================================
echo.

REM 检查 Node.js 是否安装
where node >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未检测到 Node.js，请先安装 Node.js
    echo 下载地址: https://nodejs.org/
    pause
    exit /b 1
)

echo [1/3] 检查依赖...
if not exist "node_modules" (
    echo [信息] 正在安装根目录依赖...
    call npm install --legacy-peer-deps
    if %errorlevel% neq 0 (
        echo [错误] 依赖安装失败
        pause
        exit /b 1
    )
)

if not exist "packages\web\node_modules" (
    echo [信息] 正在安装 web 包依赖...
    cd packages\web
    call npm install --legacy-peer-deps
    cd ..\..
    if %errorlevel% neq 0 (
        echo [错误] 依赖安装失败
        pause
        exit /b 1
    )
)

if not exist "packages\common\node_modules" (
    echo [信息] 正在安装 common 包依赖...
    cd packages\common
    call npm install --legacy-peer-deps
    cd ..\..
    if %errorlevel% neq 0 (
        echo [错误] 依赖安装失败
        pause
        exit /b 1
    )
)

echo [2/3] 检查环境变量配置...
if not exist "packages\web\.env.development" (
    echo [信息] 创建环境变量文件...
    (
        echo VITE_API_BASE_URL=http://localhost:8080
        echo VITE_API_DOCS_URL=http://localhost:8080/doc.html
        echo VITE_APP_TITLE=校园集成系统-开发环境
    ) > packages\web\.env.development
    echo [成功] 环境变量文件已创建
)

echo [3/3] 启动开发服务器...
echo.
echo ========================================
echo   前端地址: http://localhost:5173
echo   API文档:  http://localhost:8080/doc.html
echo ========================================
echo.
echo 按 Ctrl+C 停止服务器
echo.

cd packages\web
call npm run dev

pause

