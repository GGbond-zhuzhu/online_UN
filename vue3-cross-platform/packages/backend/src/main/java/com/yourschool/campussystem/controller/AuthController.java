package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.service.AuthService;
import com.yourschool.campussystem.util.UserContextUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 身份认证相关接口
 * 包括学生、教师、游客、高校、管理员等不同角色的认证流程
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "身份认证", description = "用户身份认证：学生双因素认证、教师认证、游客刷脸认证、高校接入等")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "学生身份认证申请", description = "学生使用学号+教务系统验证码进行双因素认证申请")
    @PostMapping("/student/apply")
    public ApiResponse<Map<String, Object>> applyStudentAuth(
            HttpServletRequest request,
            @Parameter(description = "学号", required = true)
            @RequestParam String studentId,
            
            @Parameter(description = "教务系统验证码", required = true)
            @RequestParam String verificationCode,
            
            @Parameter(description = "学生姓名", required = true)
            @RequestParam String name,
            
            @Parameter(description = "身份证号", required = true)
            @RequestParam String idCard,
            
            @Parameter(description = "学校ID", required = true)
            @RequestParam Long schoolId) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = authService.applyStudentAuth(userId, studentId, verificationCode, name, idCard, schoolId);
        return ApiResponse.success("认证申请已提交", response);
    }

    @Operation(summary = "教师身份认证申请", description = "教师身份认证申请，需要上传教师证明文件")
    @PostMapping("/teacher/apply")
    public ApiResponse<Map<String, Object>> applyTeacherAuth(
            HttpServletRequest request,
            @Parameter(description = "工号", required = true)
            @RequestParam String teacherId,
            
            @Parameter(description = "教师姓名", required = true)
            @RequestParam String name,
            
            @Parameter(description = "身份证号", required = true)
            @RequestParam String idCard,
            
            @Parameter(description = "学校ID", required = true)
            @RequestParam Long schoolId,
            
            @Parameter(description = "教师证明文件", required = true)
            @RequestParam MultipartFile certificateFile) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = authService.applyTeacherAuth(userId, teacherId, name, idCard, schoolId, certificateFile);
        return ApiResponse.success("认证申请已提交", response);
    }

    @Operation(summary = "游客刷脸活体检测", description = "游客进行刷脸活体检测，用于申请游客临时卡")
    @PostMapping("/visitor/face-detect")
    public ApiResponse<Map<String, Object>> visitorFaceDetect(
            @Parameter(description = "人脸照片（Base64或文件）", required = true)
            @RequestParam String faceImage,
            
            @Parameter(description = "姓名", required = true)
            @RequestParam String name,
            
            @Parameter(description = "手机号", required = true)
            @RequestParam String phone,
            
            @Parameter(description = "身份证号", required = true)
            @RequestParam String idCard,
            
            @Parameter(description = "进校事由", required = true)
            @RequestParam String reason) {
        
        Map<String, Object> response = authService.visitorFaceDetect(faceImage, name, phone, idCard, reason);
        return ApiResponse.success("活体检测通过", response);
    }

    @Operation(summary = "游客进校登记", description = "游客完成刷脸检测后，进行进校登记")
    @PostMapping("/visitor/register")
    public ApiResponse<Map<String, Object>> visitorRegister(
            @Parameter(description = "姓名", required = true)
            @RequestParam String name,
            
            @Parameter(description = "手机号", required = true)
            @RequestParam String phone,
            
            @Parameter(description = "身份证号", required = true)
            @RequestParam String idCard,
            
            @Parameter(description = "进校事由", required = true)
            @RequestParam String reason,
            
            @Parameter(description = "预计停留时间（小时）", required = true)
            @RequestParam Integer stayHours,
            
            @Parameter(description = "学校ID", required = true)
            @RequestParam Long schoolId) {
        
        Map<String, Object> response = authService.visitorRegister(name, phone, idCard, reason, stayHours, schoolId);
        return ApiResponse.success("进校登记成功", response);
    }

    @Operation(summary = "查询认证申请状态", description = "查询用户身份认证申请的审核状态")
    @GetMapping("/apply/status")
    public ApiResponse<Map<String, Object>> getAuthApplyStatus(
            @Parameter(description = "申请ID", required = true)
            @RequestParam Long applyId) {
        
        Map<String, Object> response = authService.getAuthApplyStatus(applyId);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "高校官方接入申请", description = "高校申请官方接入平台，需要上传高校资质文件")
    @PostMapping("/university/apply")
    public ApiResponse<Map<String, Object>> applyUniversityAccess(
            @Parameter(description = "高校名称", required = true)
            @RequestParam String universityName,
            
            @Parameter(description = "高校代码", required = true)
            @RequestParam String universityCode,
            
            @Parameter(description = "联系人姓名", required = true)
            @RequestParam String contactName,
            
            @Parameter(description = "联系人电话", required = true)
            @RequestParam String contactPhone,
            
            @Parameter(description = "联系人邮箱", required = true)
            @RequestParam String contactEmail,
            
            @Parameter(description = "高校资质证明文件", required = true)
            @RequestParam MultipartFile certificateFile,
            
            @Parameter(description = "申请说明")
            @RequestParam(required = false) String description) {
        
        Map<String, Object> response = authService.applyUniversityAccess(universityName, universityCode,
                contactName, contactPhone, contactEmail, certificateFile, description);
        return ApiResponse.success("高校接入申请已提交", response);
    }

    @Operation(summary = "获取认证申请记录", description = "获取当前用户的所有认证申请记录")
    @GetMapping("/apply/records")
    public ApiResponse<Map<String, Object>> getAuthApplyRecords(
            HttpServletRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = authService.getAuthApplyRecords(userId, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "发送邮箱验证码（模拟实现）", 
               description = "发送邮箱验证码用于邮箱登录。由于企业级邮箱服务需要企业级用户才能接入，此接口采用模拟实现。" +
                           "当邮件服务未配置时，验证码会在响应中返回（仅开发/测试环境），方便测试使用。" +
                           "当邮件服务已配置时，验证码会发送到邮箱，响应中不返回验证码。")
    @PostMapping("/email/send-code")
    public ApiResponse<Map<String, Object>> sendEmailCode(
            @Parameter(description = "邮箱地址", required = true)
            @RequestBody Map<String, String> params) {
        
        String email = params.get("email");
        if (email == null || email.isEmpty()) {
            return ApiResponse.error(com.yourschool.campussystem.common.ErrorCode.BAD_REQUEST, "邮箱地址不能为空");
        }
        
        Map<String, Object> response = authService.sendEmailCode(email);
        // 根据是否返回验证码来判断模式
        String message = response.containsKey("code") ? 
            "验证码已发送（模拟模式，验证码已返回在响应中）" : 
            "验证码已发送到邮箱";
        return ApiResponse.success(message, response);
    }

    @Operation(summary = "邮箱登录", 
               description = "使用邮箱和验证码登录。如果邮箱对应的用户不存在，系统会自动创建用户（默认为游客角色）。")
    @PostMapping("/email/login")
    public ApiResponse<Map<String, Object>> emailLogin(
            HttpServletRequest request,
            @Parameter(description = "登录参数", required = true)
            @RequestBody Map<String, String> params) {
        
        String email = params.get("email");
        String code = params.get("code");
        String codeId = params.get("codeId");
        
        if (email == null || email.isEmpty()) {
            return ApiResponse.error(com.yourschool.campussystem.common.ErrorCode.BAD_REQUEST, "邮箱地址不能为空");
        }
        if (code == null || code.isEmpty()) {
            return ApiResponse.error(com.yourschool.campussystem.common.ErrorCode.BAD_REQUEST, "验证码不能为空");
        }
        if (codeId == null || codeId.isEmpty()) {
            return ApiResponse.error(com.yourschool.campussystem.common.ErrorCode.BAD_REQUEST, "验证码ID不能为空");
        }
        
        Map<String, Object> response = authService.emailLogin(email, code, codeId, request);
        return ApiResponse.success("登录成功", response);
    }
}

