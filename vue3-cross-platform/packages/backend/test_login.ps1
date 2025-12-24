# 测试登录接口
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "测试登录接口" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

$body = @{
    username = 'zhangsan'
    password = '123456'
} | ConvertTo-Json -Compress

Write-Host "请求URL: http://localhost:8080/api/user/login" -ForegroundColor Yellow
Write-Host "请求体: $body" -ForegroundColor Yellow
Write-Host ""

try {
    $response = Invoke-RestMethod -Uri http://localhost:8080/api/user/login `
        -Method POST `
        -Body $body `
        -ContentType 'application/json'
    
    Write-Host "✅ 登录成功！" -ForegroundColor Green
    Write-Host "状态码: 200" -ForegroundColor Green
    Write-Host ""
    Write-Host "响应数据:" -ForegroundColor Cyan
    $response | ConvertTo-Json -Depth 5
    
    if ($response.code -eq 200) {
        Write-Host ""
        Write-Host "✅ Token已获取: $($response.data.token.Substring(0, 20))..." -ForegroundColor Green
    }
} catch {
    Write-Host "❌ 登录失败！" -ForegroundColor Red
    Write-Host "错误信息: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $statusCode = [int]$_.Exception.Response.StatusCode
        Write-Host "HTTP状态码: $statusCode" -ForegroundColor Red
        
        try {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $responseBody = $reader.ReadToEnd()
            Write-Host "响应内容: $responseBody" -ForegroundColor Yellow
            $reader.Close()
        } catch {
            Write-Host "无法读取响应内容" -ForegroundColor Yellow
        }
    }
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan

