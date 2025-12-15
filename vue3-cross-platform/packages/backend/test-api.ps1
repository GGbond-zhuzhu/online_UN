# API测试脚本
Write-Host "=== API Test Script ===" -ForegroundColor Green
Write-Host ""

# 测试1: 用户注册
Write-Host "Test 1: User Registration" -ForegroundColor Yellow
try {
    $body = @{
        username = "testuser001"
        password = "123456"
        confirmPassword = "123456"
        nickname = "Test User"
        role = "TOURIST"
        schoolId = 1
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/user/register" `
        -Method POST `
        -Body $body `
        -ContentType "application/json"
    
    Write-Host "  SUCCESS: User registered, ID: $($response.data)" -ForegroundColor Green
    $script:userId = $response.data
} catch {
    Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# 测试2: 用户登录
Write-Host "Test 2: User Login" -ForegroundColor Yellow
try {
    $body = @{
        username = "testuser001"
        password = "123456"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/user/login" `
        -Method POST `
        -Body $body `
        -ContentType "application/json"
    
    Write-Host "  SUCCESS: Login successful" -ForegroundColor Green
    $script:token = $response.data.token
    Write-Host "  Token: $($script:token.Substring(0, [Math]::Min(50, $script:token.Length)))..." -ForegroundColor Cyan
} catch {
    Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# 测试3: 获取用户信息
if ($script:token) {
    Write-Host "Test 3: Get User Info" -ForegroundColor Yellow
    try {
        $headers = @{
            Authorization = "Bearer $script:token"
        }
        
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/user/info" `
            -Method GET `
            -Headers $headers
        
        Write-Host "  SUCCESS: User info retrieved" -ForegroundColor Green
        Write-Host "  Username: $($response.data.username)" -ForegroundColor Cyan
        Write-Host "  Nickname: $($response.data.nickname)" -ForegroundColor Cyan
    } catch {
        Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
}

# 测试4: 发送邮箱验证码
Write-Host "Test 4: Send Email Code" -ForegroundColor Yellow
try {
    $body = @{
        email = "test@example.com"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/email/send-code" `
        -Method POST `
        -Body $body `
        -ContentType "application/json"
    
    Write-Host "  SUCCESS: Email code sent" -ForegroundColor Green
    Write-Host "  CodeId: $($response.data.codeId)" -ForegroundColor Cyan
    if ($response.data.code) {
        Write-Host "  Code (Mock Mode): $($response.data.code)" -ForegroundColor Cyan
        $script:emailCode = $response.data.code
        $script:emailCodeId = $response.data.codeId
    }
} catch {
    Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# 测试5: 邮箱登录
if ($script:emailCode -and $script:emailCodeId) {
    Write-Host "Test 5: Email Login" -ForegroundColor Yellow
    try {
        $body = "email=test@example.com&code=$($script:emailCode)&codeId=$($script:emailCodeId)"
        
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/email/login" `
            -Method POST `
            -Body $body `
            -ContentType "application/x-www-form-urlencoded"
        
        Write-Host "  SUCCESS: Email login successful" -ForegroundColor Green
        Write-Host "  Token: $($response.data.token.Substring(0, [Math]::Min(50, $response.data.token.Length)))..." -ForegroundColor Cyan
    } catch {
        Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
}

# 测试6: 获取未读消息数量
if ($script:token) {
    Write-Host "Test 6: Get Unread Message Count" -ForegroundColor Yellow
    try {
        $headers = @{
            Authorization = "Bearer $script:token"
        }
        
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/messages/unread-count" `
            -Method GET `
            -Headers $headers
        
        Write-Host "  SUCCESS: Unread count retrieved" -ForegroundColor Green
        Write-Host "  Unread Count: $($response.data.unreadCount)" -ForegroundColor Cyan
    } catch {
        Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
}

# 测试7: 获取聊天会话列表
if ($script:token) {
    Write-Host "Test 7: Get Chat Conversations" -ForegroundColor Yellow
    try {
        $headers = @{
            Authorization = "Bearer $script:token"
        }
        
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/chat/conversations" `
            -Method GET `
            -Headers $headers
        
        Write-Host "  SUCCESS: Conversations retrieved" -ForegroundColor Green
        Write-Host "  Conversation Count: $($response.data.Count)" -ForegroundColor Cyan
    } catch {
        Write-Host "  FAILED: $($_.Exception.Message)" -ForegroundColor Red
    }
    Write-Host ""
}

Write-Host "=== Test Complete ===" -ForegroundColor Green
Write-Host ""
Write-Host "For more detailed testing, please use Knife4j:" -ForegroundColor Cyan
Write-Host "  http://localhost:8080/doc.html" -ForegroundColor Yellow
