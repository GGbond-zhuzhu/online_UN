# 测试API接口
param(
    [string]$Email = "test@example.com"
)

Write-Host "正在测试API接口..." -ForegroundColor Cyan
Write-Host ""

# 测试1: 发送邮箱验证码
Write-Host "测试1: 发送邮箱验证码" -ForegroundColor Yellow
try {
    $body = @{
        email = $Email
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/email/send-code" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body

    Write-Host "✅ 验证码发送成功" -ForegroundColor Green
    Write-Host "   响应: $($response | ConvertTo-Json -Depth 3)" -ForegroundColor Gray
    
    if ($response.data.code) {
        Write-Host "   验证码(模拟模式): $($response.data.code)" -ForegroundColor Cyan
    }
    
    return $response
} catch {
    Write-Host "❌ 验证码发送失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "✅ API测试完成" -ForegroundColor Green
