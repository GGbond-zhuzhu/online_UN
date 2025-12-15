# 测试前端服务是否启动
Write-Host "正在检查前端服务..." -ForegroundColor Cyan

try {
    $response = Invoke-WebRequest -Uri "http://localhost:5173" -Method GET -TimeoutSec 5 -UseBasicParsing
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ 前端服务运行正常 (http://localhost:5173)" -ForegroundColor Green
        exit 0
    }
} catch {
    Write-Host "❌ 前端服务未启动或无法访问" -ForegroundColor Red
    Write-Host "   请先启动前端服务: cd packages\web && npm run dev" -ForegroundColor Yellow
    exit 1
}
