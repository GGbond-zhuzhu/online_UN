package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.annotation.RequireRole;
import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.entity.User;
import com.yourschool.campussystem.enums.UserRoleEnum;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.service.UniversityService;
import com.yourschool.campussystem.util.UserContextUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 高校管理接口
 * 高校角色专用功能：官方接入、用户管理、功能配置等
 */
@RestController
@RequestMapping("/api/university")
@Tag(name = "高校管理", description = "高校官方接入与管理，定制化功能配置，实现数字化校园治理")
@RequireRole({UserRoleEnum.UNIVERSITY})  // 需要高校角色权限
@RequiredArgsConstructor
public class UniversityController {

    private final UniversityService universityService;
    private final com.yourschool.campussystem.mapper.UserMapper userMapper;

    // ==================== 高校信息管理 ====================

    @Operation(summary = "获取高校信息", description = "获取当前高校的详细信息")
    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> getUniversityInfo(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> university = universityService.getUniversityInfo(universityId);
        return ApiResponse.success("查询成功", university);
    }
    
    /**
     * 从用户ID获取高校ID（schoolId）
     */
    private Long getUniversityIdFromUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        if (user.getSchoolId() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "用户未关联高校");
        }
        return user.getSchoolId();
    }

    @Operation(summary = "更新高校信息", description = "更新高校基本信息")
    @PutMapping("/info")
    public ApiResponse<Map<String, Object>> updateUniversityInfo(
            HttpServletRequest request,
            @Parameter(description = "高校名称")
            @RequestParam(required = false) String universityName,
            
            @Parameter(description = "联系人姓名")
            @RequestParam(required = false) String contactName,
            
            @Parameter(description = "联系人电话")
            @RequestParam(required = false) String contactPhone,
            
            @Parameter(description = "联系人邮箱")
            @RequestParam(required = false) String contactEmail,
            
            @Parameter(description = "高校简介")
            @RequestParam(required = false) String description) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> university = universityService.updateUniversityInfo(
                universityId, universityName, contactName, contactPhone, contactEmail, description);
        return ApiResponse.success("高校信息更新成功", university);
    }

    // ==================== 用户管理 ====================

    @Operation(summary = "获取本校用户列表", description = "获取本校所有用户列表，支持筛选和搜索")
    @GetMapping("/users")
    public ApiResponse<Map<String, Object>> getUniversityUsers(
            HttpServletRequest request,
            @Parameter(description = "角色筛选（STUDENT/TEACHER/VISITOR）")
            @RequestParam(required = false) String role,
            
            @Parameter(description = "认证状态筛选（PENDING/APPROVED/REJECTED）")
            @RequestParam(required = false) String authStatus,
            
            @Parameter(description = "关键词搜索（学号/工号/姓名）")
            @RequestParam(required = false) String keyword,
            
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> response = universityService.getUniversityUsers(
                universityId, role, authStatus, keyword, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "审核本校用户认证申请", description = "高校审核本校用户的身份认证申请")
    @PutMapping("/users/auth-applies/{applyId}/review")
    public ApiResponse<String> reviewUserAuthApply(
            HttpServletRequest request,
            @Parameter(description = "申请ID", required = true)
            @PathVariable Long applyId,
            
            @Parameter(description = "审核结果（APPROVED/REJECTED）", required = true)
            @RequestParam String result,
            
            @Parameter(description = "审核备注")
            @RequestParam(required = false) String note) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        universityService.reviewUserAuthApply(universityId, applyId, result, note, userId);
        return ApiResponse.success("认证申请审核完成");
    }

    @Operation(summary = "批量导入用户", description = "通过Excel批量导入本校用户信息")
    @PostMapping("/users/import")
    public ApiResponse<Map<String, Object>> importUsers(
            HttpServletRequest request,
            @Parameter(description = "Excel文件", required = true)
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "用户类型（STUDENT/TEACHER）", required = true)
            @RequestParam String userType) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> response = universityService.importUsers(universityId, file, userType);
        return ApiResponse.success("用户导入完成", response);
    }

    @Operation(summary = "导出用户列表", description = "导出本校用户列表为Excel文件")
    @GetMapping("/users/export")
    public ResponseEntity<Resource> exportUsers(
            HttpServletRequest request,
            @Parameter(description = "角色筛选")
            @RequestParam(required = false) String role) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Resource resource = universityService.exportUsers(universityId, role);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "users.xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    // ==================== 学生信息管理 ====================

    @Operation(summary = "获取学生信息列表", description = "获取本校学生信息列表，支持筛选和搜索")
    @GetMapping("/students")
    public ApiResponse<Map<String, Object>> getStudentInfoList(
            HttpServletRequest request,
            @Parameter(description = "关键词搜索（学号/姓名）")
            @RequestParam(required = false) String keyword,
            
            @Parameter(description = "年级筛选")
            @RequestParam(required = false) String grade,
            
            @Parameter(description = "专业筛选")
            @RequestParam(required = false) String major,
            
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> response = universityService.getStudentInfoList(universityId, keyword, grade, major, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "添加学生信息", description = "手动添加单个学生信息")
    @PostMapping("/students")
    public ApiResponse<Map<String, Object>> addStudentInfo(
            HttpServletRequest request,
            @Parameter(description = "学号", required = true)
            @RequestParam String studentId,
            
            @Parameter(description = "姓名", required = true)
            @RequestParam String name,
            
            @Parameter(description = "身份证号", required = true)
            @RequestParam String idCard,
            
            @Parameter(description = "专业")
            @RequestParam(required = false) String major,
            
            @Parameter(description = "年级")
            @RequestParam(required = false) String grade,
            
            @Parameter(description = "班级")
            @RequestParam(required = false) String className) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> response = universityService.addStudentInfo(universityId, studentId, name, idCard, major, grade, className);
        return ApiResponse.success("学生信息添加成功", response);
    }

    @Operation(summary = "批量导入学生信息", description = "通过Excel批量导入学生信息")
    @PostMapping("/students/import")
    public ApiResponse<Map<String, Object>> importStudentInfo(
            HttpServletRequest request,
            @Parameter(description = "Excel文件", required = true)
            @RequestParam("file") MultipartFile file) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> response = universityService.importStudentInfo(universityId, file);
        return ApiResponse.success("学生信息导入完成", response);
    }

    @Operation(summary = "导出学生信息", description = "导出本校学生信息为Excel文件")
    @GetMapping("/students/export")
    public ResponseEntity<Resource> exportStudentInfo(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Resource resource = universityService.exportStudentInfo(universityId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Operation(summary = "更新学生信息", description = "更新学生信息（专业、年级、班级、状态等）")
    @PutMapping("/students/{studentInfoId}")
    public ApiResponse<Map<String, Object>> updateStudentInfo(
            HttpServletRequest request,
            @Parameter(description = "学生信息ID", required = true)
            @PathVariable Long studentInfoId,
            
            @Parameter(description = "专业")
            @RequestParam(required = false) String major,
            
            @Parameter(description = "年级")
            @RequestParam(required = false) String grade,
            
            @Parameter(description = "班级")
            @RequestParam(required = false) String className,
            
            @Parameter(description = "状态（ACTIVE/GRADUATED/SUSPENDED）")
            @RequestParam(required = false) String status) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> response = universityService.updateStudentInfo(universityId, studentInfoId, major, grade, className, status);
        return ApiResponse.success("学生信息更新成功", response);
    }

    @Operation(summary = "删除学生信息", description = "删除学生信息（逻辑删除）")
    @DeleteMapping("/students/{studentInfoId}")
    public ApiResponse<String> deleteStudentInfo(
            HttpServletRequest request,
            @Parameter(description = "学生信息ID", required = true)
            @PathVariable Long studentInfoId) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        universityService.deleteStudentInfo(universityId, studentInfoId);
        return ApiResponse.success("学生信息删除成功");
    }

    // ==================== 教师信息管理 ====================

    @Operation(summary = "获取教师信息列表", description = "获取本校教师信息列表，支持筛选和搜索")
    @GetMapping("/teachers")
    public ApiResponse<Map<String, Object>> getTeacherInfoList(
            HttpServletRequest request,
            @Parameter(description = "关键词搜索（工号/姓名）")
            @RequestParam(required = false) String keyword,
            
            @Parameter(description = "部门筛选")
            @RequestParam(required = false) String department,
            
            @Parameter(description = "职称筛选")
            @RequestParam(required = false) String title,
            
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> response = universityService.getTeacherInfoList(universityId, keyword, department, title, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "添加教师信息", description = "手动添加单个教师信息")
    @PostMapping("/teachers")
    public ApiResponse<Map<String, Object>> addTeacherInfo(
            HttpServletRequest request,
            @Parameter(description = "工号", required = true)
            @RequestParam String teacherId,
            
            @Parameter(description = "姓名", required = true)
            @RequestParam String name,
            
            @Parameter(description = "身份证号", required = true)
            @RequestParam String idCard,
            
            @Parameter(description = "部门")
            @RequestParam(required = false) String department,
            
            @Parameter(description = "职称")
            @RequestParam(required = false) String title,
            
            @Parameter(description = "联系电话")
            @RequestParam(required = false) String phone,
            
            @Parameter(description = "邮箱")
            @RequestParam(required = false) String email) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> response = universityService.addTeacherInfo(universityId, teacherId, name, idCard, department, title, phone, email);
        return ApiResponse.success("教师信息添加成功", response);
    }

    @Operation(summary = "批量导入教师信息", description = "通过Excel批量导入教师信息")
    @PostMapping("/teachers/import")
    public ApiResponse<Map<String, Object>> importTeacherInfo(
            HttpServletRequest request,
            @Parameter(description = "Excel文件", required = true)
            @RequestParam("file") MultipartFile file) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> response = universityService.importTeacherInfo(universityId, file);
        return ApiResponse.success("教师信息导入完成", response);
    }

    @Operation(summary = "导出教师信息", description = "导出本校教师信息为Excel文件")
    @GetMapping("/teachers/export")
    public ResponseEntity<Resource> exportTeacherInfo(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Resource resource = universityService.exportTeacherInfo(universityId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=teachers.xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Operation(summary = "更新教师信息", description = "更新教师信息（部门、职称、联系方式、状态等）")
    @PutMapping("/teachers/{teacherInfoId}")
    public ApiResponse<Map<String, Object>> updateTeacherInfo(
            HttpServletRequest request,
            @Parameter(description = "教师信息ID", required = true)
            @PathVariable Long teacherInfoId,
            
            @Parameter(description = "部门")
            @RequestParam(required = false) String department,
            
            @Parameter(description = "职称")
            @RequestParam(required = false) String title,
            
            @Parameter(description = "联系电话")
            @RequestParam(required = false) String phone,
            
            @Parameter(description = "邮箱")
            @RequestParam(required = false) String email,
            
            @Parameter(description = "状态（ACTIVE/RESIGNED/SUSPENDED）")
            @RequestParam(required = false) String status) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> response = universityService.updateTeacherInfo(universityId, teacherInfoId, department, title, phone, email, status);
        return ApiResponse.success("教师信息更新成功", response);
    }

    @Operation(summary = "删除教师信息", description = "删除教师信息（逻辑删除）")
    @DeleteMapping("/teachers/{teacherInfoId}")
    public ApiResponse<String> deleteTeacherInfo(
            HttpServletRequest request,
            @Parameter(description = "教师信息ID", required = true)
            @PathVariable Long teacherInfoId) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        universityService.deleteTeacherInfo(universityId, teacherInfoId);
        return ApiResponse.success("教师信息删除成功");
    }

    // ==================== 功能配置 ====================

    @Operation(summary = "获取功能配置", description = "获取高校定制化功能配置")
    @GetMapping("/config")
    public ApiResponse<Map<String, Object>> getUniversityConfig(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> config = universityService.getUniversityConfig(universityId);
        return ApiResponse.success("查询成功", config);
    }

    @Operation(summary = "更新功能配置", description = "更新高校定制化功能配置")
    @PutMapping("/config")
    public ApiResponse<String> updateUniversityConfig(
            HttpServletRequest request,
            @Parameter(description = "配置信息（JSON格式）", required = true)
            @RequestBody Map<String, Object> config) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        universityService.updateUniversityConfig(universityId, config);
        return ApiResponse.success("功能配置更新成功");
    }

    // ==================== 数据统计 ====================

    @Operation(summary = "获取高校数据统计", description = "获取本校用户数、内容数等统计数据")
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getUniversityStatistics(
            HttpServletRequest request,
            @Parameter(description = "统计时间范围（WEEK/MONTH/YEAR）", example = "MONTH")
            @RequestParam(defaultValue = "MONTH") String timeRange) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> stats = universityService.getUniversityStatistics(universityId, timeRange);
        return ApiResponse.success("查询成功", stats);
    }

    @Operation(summary = "获取用户活跃度统计", description = "获取本校用户活跃度统计数据")
    @GetMapping("/statistics/activity")
    public ApiResponse<Map<String, Object>> getUserActivityStatistics(
            HttpServletRequest request,
            @Parameter(description = "开始日期（yyyy-MM-dd）", example = "2024-01-01")
            @RequestParam(required = false) String startDate,
            
            @Parameter(description = "结束日期（yyyy-MM-dd）", example = "2024-12-31")
            @RequestParam(required = false) String endDate) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> stats = universityService.getUserActivityStatistics(universityId, startDate, endDate);
        return ApiResponse.success("查询成功", stats);
    }

    // ==================== 内容管理 ====================

    @Operation(summary = "获取本校内容统计", description = "获取本校二手商品、兼职等内容的统计数据")
    @GetMapping("/content/statistics")
    public ApiResponse<Map<String, Object>> getContentStatistics(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> stats = universityService.getContentStatistics(universityId);
        return ApiResponse.success("查询成功", stats);
    }

    @Operation(summary = "审核本校内容", description = "高校审核本校用户发布的内容")
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
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        universityService.reviewContent(universityId, contentId, contentType, result, note, userId);
        return ApiResponse.success("内容审核完成");
    }

    // ==================== 通知推送 ====================

    @Operation(summary = "发送通知", description = "高校向本校用户发送通知")
    @PostMapping("/notifications/send")
    public ApiResponse<Map<String, Object>> sendNotification(
            HttpServletRequest request,
            @Parameter(description = "通知标题", required = true)
            @RequestParam String title,
            
            @Parameter(description = "通知内容", required = true)
            @RequestParam String content,
            
            @Parameter(description = "接收用户类型（ALL/STUDENT/TEACHER）", example = "ALL")
            @RequestParam(defaultValue = "ALL") String targetType,
            
            @Parameter(description = "是否紧急", example = "false")
            @RequestParam(defaultValue = "false") Boolean isUrgent) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> response = universityService.sendNotification(
                universityId, title, content, targetType, isUrgent);
        return ApiResponse.success("通知发送成功", response);
    }

    @Operation(summary = "获取通知发送记录", description = "获取高校发送的通知记录列表")
    @GetMapping("/notifications/history")
    public ApiResponse<Map<String, Object>> getNotificationHistory(
            HttpServletRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long universityId = getUniversityIdFromUser(userId);
        Map<String, Object> response = universityService.getNotificationHistory(universityId, page, size);
        return ApiResponse.success("查询成功", response);
    }
}

