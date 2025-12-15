@echo off
echo ========================================
echo 启动校园集成系统后端服务
echo ========================================
echo.

echo 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请先安装JDK 17或更高版本
    pause
    exit /b 1
)

echo.
echo 检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven，请先安装Maven
    pause
    exit /b 1
)

echo.
echo 检查MySQL服务...
net start | findstr /i "MySQL" >nul
if %errorlevel% neq 0 (
    echo 警告: MySQL服务可能未启动，请确保MySQL服务已启动
)

echo.
echo 开始启动后端服务...
echo 服务将在 http://localhost:8080 启动
echo API文档地址: http://localhost:8080/doc.html
echo.

mvn spring-boot:run

pause

