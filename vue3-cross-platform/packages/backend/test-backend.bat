@echo off
chcp 65001 >nul
echo ========================================
echo   后端服务测试脚本
echo ========================================
echo.

echo [1/3] 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo [错误] 未检测到Java，请先安装Java 17+
    pause
    exit /b 1
)

echo [2/3] 检查Maven...
call mvnw.cmd -v
if %errorlevel% neq 0 (
    echo [错误] Maven检查失败
    pause
    exit /b 1
)

echo [3/3] 启动后端服务...
echo.
echo ========================================
echo   服务地址: http://localhost:8080
echo   API文档:  http://localhost:8080/doc.html
echo ========================================
echo.
echo 按 Ctrl+C 停止服务器
echo.

call mvnw.cmd spring-boot:run

pause
