# Simple API Test Script
$baseUrl = "http://localhost:8080"

Write-Host "Starting API Tests..." -ForegroundColor Cyan
Write-Host ""

# 1. Test Login
Write-Host "1. Testing Login..." -ForegroundColor Yellow
try {
    $loginBody = '{"username":"zhangsan","password":"123456"}'
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/user/login" -Method POST -ContentType "application/json" -Body $loginBody
    Write-Host "   Login Success! Token: $($loginResponse.data.token.Substring(0, 30))..." -ForegroundColor Green
    $token = $loginResponse.data.token
    Write-Host ""
    
    # 2. Test Get User Info
    Write-Host "2. Testing Get User Info..." -ForegroundColor Yellow
    $headers = @{ Authorization = "Bearer $token" }
    $userInfo = Invoke-RestMethod -Uri "$baseUrl/api/user/info" -Method GET -Headers $headers
    Write-Host "   User Info Retrieved! Username: $($userInfo.data.username)" -ForegroundColor Green
    Write-Host ""
    
    # 3. Test Ecard Info
    Write-Host "3. Testing Ecard Info..." -ForegroundColor Yellow
    $ecardInfo = Invoke-RestMethod -Uri "$baseUrl/api/ecard/info" -Method GET -Headers $headers
    Write-Host "   Ecard Info Retrieved! Balance: $($ecardInfo.data.balance)" -ForegroundColor Green
    Write-Host ""
    
    # 4. Test Common APIs (No Auth Required)
    Write-Host "4. Testing Common APIs..." -ForegroundColor Yellow
    $helpCategories = Invoke-RestMethod -Uri "$baseUrl/api/common/help/categories" -Method GET
    Write-Host "   Help Categories Retrieved! Count: $($helpCategories.data.total)" -ForegroundColor Green
    Write-Host ""
    
    Write-Host "All Tests Passed!" -ForegroundColor Green
} catch {
    Write-Host "Test Failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Please ensure backend service is running at http://localhost:8080" -ForegroundColor Yellow
}
