package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.annotation.RequireRole;
import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.enums.UserRoleEnum;
import com.yourschool.campussystem.service.MerchantService;
import com.yourschool.campussystem.util.UserContextUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 商户管理接口
 * 兼职商家入驻、资质审核、保证金管理等
 */
@RestController
@RequestMapping("/api/merchant")
@Tag(name = "商户管理", description = "兼职商家入驻、资质核验、保证金保障等功能")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @Operation(summary = "商户入驻申请", description = "商家申请入驻平台，需要上传资质文件和缴纳保证金")
    @PostMapping("/apply")
    public ApiResponse<Map<String, Object>> applyMerchant(
            HttpServletRequest request,
            @Parameter(description = "商户名称", required = true)
            @RequestParam String merchantName,
            
            @Parameter(description = "联系人姓名", required = true)
            @RequestParam String contactName,
            
            @Parameter(description = "联系人电话", required = true)
            @RequestParam String contactPhone,
            
            @Parameter(description = "联系人邮箱", required = true)
            @RequestParam String contactEmail,
            
            @Parameter(description = "营业执照文件", required = true)
            @RequestParam MultipartFile businessLicense,
            
            @Parameter(description = "法人身份证", required = true)
            @RequestParam MultipartFile legalIdCard,
            
            @Parameter(description = "保证金金额（元）", required = true)
            @RequestParam BigDecimal depositAmount,
            
            @Parameter(description = "商户类型（COMPANY/INDIVIDUAL）", required = true)
            @RequestParam String merchantType,
            
            @Parameter(description = "经营范围")
            @RequestParam(required = false) String businessScope) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = merchantService.applyMerchant(
                userId, merchantName, contactName, contactPhone, contactEmail,
                businessLicense, legalIdCard, depositAmount, merchantType, businessScope);
        return ApiResponse.success("商户入驻申请已提交", response);
    }

    @Operation(summary = "公司资格智能校验", description = "系统自动校验公司营业执照等信息")
    @PostMapping("/verify-company")
    public ApiResponse<Map<String, Object>> verifyCompany(
            @Parameter(description = "统一社会信用代码", required = true)
            @RequestParam String creditCode,
            
            @Parameter(description = "公司名称", required = true)
            @RequestParam String companyName) {
        
        Map<String, Object> response = merchantService.verifyCompany(creditCode, companyName);
        return ApiResponse.success("公司资格校验完成", response);
    }

    @Operation(summary = "获取商户信息", description = "获取当前商户的详细信息")
    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getMerchantInfo(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> merchant = merchantService.getMerchantInfo(userId);
        return ApiResponse.success("查询成功", merchant);
    }

    @Operation(summary = "缴纳保证金", description = "商户缴纳或补缴保证金，返回支付二维码")
    @PostMapping("/deposit/pay")
    public ApiResponse<Map<String, Object>> payDeposit(
            HttpServletRequest request,
            @Parameter(description = "保证金金额（元）", required = true)
            @RequestParam BigDecimal amount,
            
            @Parameter(description = "支付方式（ALIPAY/WECHAT）", required = true)
            @RequestParam String payMethod) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = merchantService.payDeposit(userId, amount, payMethod);
        return ApiResponse.success("支付订单创建成功，请扫描二维码完成支付", response);
    }

    @Operation(summary = "刷新支付二维码", description = "刷新支付订单的二维码（如果二维码过期或需要重新生成）")
    @PostMapping("/deposit/pay/{orderId}/refresh")
    public ApiResponse<Map<String, Object>> refreshPaymentQrCode(
            @Parameter(description = "订单号", required = true)
            @PathVariable String orderId) {
        
        Map<String, Object> response = merchantService.refreshPaymentQrCode(orderId);
        return ApiResponse.success("二维码刷新成功", response);
    }

    @Operation(summary = "查询支付状态", description = "查询支付订单的支付状态")
    @GetMapping("/deposit/pay/{orderId}/status")
    public ApiResponse<Map<String, Object>> queryPaymentStatus(
            @Parameter(description = "订单号", required = true)
            @PathVariable String orderId) {
        
        Map<String, Object> response = merchantService.queryPaymentStatus(orderId);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "查询保证金记录", description = "查询商户的保证金缴纳和退还记录")
    @GetMapping("/deposit/records")
    public ApiResponse<Map<String, Object>> getDepositRecords(
            HttpServletRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = merchantService.getDepositRecords(userId, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "获取商户发布的兼职列表", description = "获取商户发布的所有兼职信息")
    @GetMapping("/jobs")
    public ApiResponse<Map<String, Object>> getMerchantJobs(
            HttpServletRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = merchantService.getMerchantJobs(userId, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "获取商户统计数据", description = "获取商户的发布数、报名数等统计数据")
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getMerchantStatistics(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> stats = merchantService.getMerchantStatistics(userId);
        return ApiResponse.success("查询成功", stats);
    }
}

