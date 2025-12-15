package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.annotation.RequireRole;
import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.enums.UserRoleEnum;
import com.yourschool.campussystem.service.AdminService;
import com.yourschool.campussystem.util.UserContextUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员功能接口
 * 系统全局配置、用户管理、内容审核等
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员功能", description = "系统全局配置与用户管理，保障平台稳定、安全运行")
@RequireRole({UserRoleEnum.ADMIN})  // 整个Controller都需要管理员权限
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ==================== 用户管理 ====================

    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持筛选条件")
    @GetMapping("/users")
    public ApiResponse<Map<String, Object>> getUserList(
            HttpServletRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size,
            
            @Parameter(description = "角色筛选")
            @RequestParam(required = false) String role,
            
            @Parameter(description = "学校ID筛选")
            @RequestParam(required = false) Long schoolId,
            
            @Parameter(description = "关键词搜索（用户名/学号/工号）")
            @RequestParam(required = false) String keyword) {
        // 权限验证已在 @RequireRole 注解和拦截器中处理
        UserContextUtils.getUserIdRequired(request);
        
        Map<String, Object> response = adminService.getUserList(page, size, role, schoolId, keyword);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "禁用/启用用户", description = "管理员禁用或启用用户账号")
    @PutMapping("/users/{userId}/status")
    public ApiResponse<String> updateUserStatus(
            HttpServletRequest request,
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            
            @Parameter(description = "状态（ENABLED/DISABLED）", required = true)
            @RequestParam String status) {
        UserContextUtils.getUserIdRequired(request);
        // TODO: 添加管理员角色验证
        
        adminService.updateUserStatus(userId, status);
        return ApiResponse.success("用户状态已更新");
    }

    @Operation(summary = "重置用户密码", description = "管理员重置用户密码")
    @PostMapping("/users/{userId}/reset-password")
    public ApiResponse<Map<String, Object>> resetUserPassword(
            HttpServletRequest request,
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        // 权限验证已在 @RequireRole 注解和拦截器中处理
        UserContextUtils.getUserIdRequired(request);
        
        String newPassword = adminService.resetUserPassword(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("newPassword", newPassword);
        response.put("message", "密码已重置，新密码：" + newPassword);
        // 实际应该发送邮件，这里返回密码供测试使用
        return ApiResponse.success("密码已重置", response);
    }

    // ==================== 认证审核 ====================

    @Operation(summary = "获取认证申请列表", description = "获取待审核的身份认证申请列表")
    @GetMapping("/auth-applies")
    public ApiResponse<Map<String, Object>> getAuthApplies(
            HttpServletRequest request,
            @Parameter(description = "审核状态筛选（PENDING/APPROVED/REJECTED）")
            @RequestParam(required = false) String status,
            
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        // 权限验证已在 @RequireRole 注解和拦截器中处理
        UserContextUtils.getUserIdRequired(request);
        
        Map<String, Object> response = adminService.getAuthApplies(status, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "审核认证申请", description = "管理员审核用户身份认证申请")
    @PutMapping("/auth-applies/{applyId}/review")
    public ApiResponse<String> reviewAuthApply(
            HttpServletRequest request,
            @Parameter(description = "申请ID", required = true)
            @PathVariable Long applyId,
            
            @Parameter(description = "审核结果（APPROVED/REJECTED）", required = true)
            @RequestParam String result,
            
            @Parameter(description = "审核备注")
            @RequestParam(required = false) String note) {
        Long adminId = UserContextUtils.getUserIdRequired(request);
        // TODO: 添加管理员角色验证
        
        adminService.reviewAuthApply(applyId, result, note, adminId);
        return ApiResponse.success("认证申请审核完成");
    }

    // ==================== 内容审核 ====================

    @Operation(summary = "获取待审核内容列表", description = "获取待审核的二手商品、兼职等信息")
    @GetMapping("/content/review-list")
    public ApiResponse<Map<String, Object>> getReviewContentList(
            HttpServletRequest request,
            @Parameter(description = "内容类型（SECONDHAND/PARTTIME）")
            @RequestParam(required = false) String contentType,
            
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        // 权限验证已在 @RequireRole 注解和拦截器中处理
        UserContextUtils.getUserIdRequired(request);
        
        Map<String, Object> response = adminService.getReviewContentList(contentType, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "审核内容", description = "管理员审核用户发布的内容")
    @PutMapping("/content/{contentId}/review")
    public ApiResponse<String> reviewContent(
            HttpServletRequest request,
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            
            @Parameter(description = "内容类型（SECONDHAND/PARTTIME）", required = true)
            @RequestParam String contentType,
            
            @Parameter(description = "审核结果（APPROVED/REJECTED）", required = true)
            @RequestParam String result,
            
            @Parameter(description = "审核备注")
            @RequestParam(required = false) String note) {
        Long adminId = UserContextUtils.getUserIdRequired(request);
        // TODO: 添加管理员角色验证
        
        adminService.reviewContent(contentId, contentType, result, note, adminId);
        return ApiResponse.success("内容审核完成");
    }

    // ==================== 高校管理 ====================

    @Operation(summary = "获取高校列表", description = "获取已接入平台的高校列表")
    @GetMapping("/universities")
    public ApiResponse<Map<String, Object>> getUniversityList(
            HttpServletRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        // 权限验证已在 @RequireRole 注解和拦截器中处理
        UserContextUtils.getUserIdRequired(request);
        
        Map<String, Object> response = adminService.getUniversityList(page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "审核高校接入申请", description = "管理员审核高校官方接入申请")
    @PutMapping("/universities/{applyId}/review")
    public ApiResponse<String> reviewUniversityApply(
            HttpServletRequest request,
            @Parameter(description = "申请ID", required = true)
            @PathVariable Long applyId,
            
            @Parameter(description = "审核结果（APPROVED/REJECTED）", required = true)
            @RequestParam String result,
            
            @Parameter(description = "审核备注")
            @RequestParam(required = false) String note) {
        Long adminId = UserContextUtils.getUserIdRequired(request);
        // TODO: 添加管理员角色验证
        
        adminService.reviewUniversityApply(applyId, result, note, adminId);
        return ApiResponse.success("高校接入申请审核完成");
    }

    // ==================== 系统配置 ====================

    @Operation(summary = "获取系统配置", description = "获取系统全局配置信息")
    @GetMapping("/config")
    public ApiResponse<Map<String, Object>> getSystemConfig(HttpServletRequest request) {
        UserContextUtils.getUserIdRequired(request);
        // TODO: 添加管理员角色验证
        
        Map<String, Object> config = adminService.getSystemConfig();
        return ApiResponse.success("查询成功", config);
    }

    @Operation(summary = "更新系统配置", description = "更新系统全局配置")
    @PutMapping("/config")
    public ApiResponse<String> updateSystemConfig(
            HttpServletRequest request,
            @Parameter(description = "配置信息", required = true)
            @RequestBody Map<String, Object> config) {
        // 权限验证已在 @RequireRole 注解和拦截器中处理
        UserContextUtils.getUserIdRequired(request);
        
        adminService.updateSystemConfig(config);
        return ApiResponse.success("系统配置已更新");
    }

    // ==================== 数据统计 ====================

    @Operation(summary = "获取平台数据统计", description = "获取平台用户数、内容数等统计数据")
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics(HttpServletRequest request) {
        UserContextUtils.getUserIdRequired(request);
        // TODO: 添加管理员角色验证
        
        Map<String, Object> stats = adminService.getStatistics();
        return ApiResponse.success("查询成功", stats);
    }
}

