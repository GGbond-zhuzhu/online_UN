# 测试后端服务是否启动
Write-Host "正在检查后端服务..." -ForegroundColor Cyan

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/doc.html" -Method GET -TimeoutSec 5 -UseBasicParsing
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ 后端服务运行正常 (http://localhost:8080)" -ForegroundColor Green
        Write-Host "   API文档: http://localhost:8080/doc.html" -ForegroundColor Green
        exit 0
    }
} catch {
    Write-Host "❌ 后端服务未启动或无法访问" -ForegroundColor Red
    Write-Host "   请先启动后端服务: cd packages\backend && mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}
