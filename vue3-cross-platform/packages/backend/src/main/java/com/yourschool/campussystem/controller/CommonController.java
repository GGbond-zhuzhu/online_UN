package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.service.CommonService;
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
 * 通用功能接口
 * 包括帮助中心、公告管理、文件上传等
 */
@RestController
@RequestMapping("/api/common")
@Tag(name = "通用功能", description = "帮助中心、公告管理、文件上传等通用功能")
@RequiredArgsConstructor
public class CommonController {

    private final CommonService commonService;

    // ==================== 帮助中心 ====================

    @Operation(summary = "获取帮助文档列表", description = "获取帮助中心的文档分类和列表")
    @GetMapping("/help/categories")
    public ApiResponse<Map<String, Object>> getHelpCategories() {
        Map<String, Object> response = commonService.getHelpCategories();
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "获取帮助文档详情", description = "获取指定帮助文档的详细内容")
    @GetMapping("/help/article/{id}")
    public ApiResponse<Map<String, Object>> getHelpArticle(
            @Parameter(description = "文档ID", required = true)
            @PathVariable Long id) {
        Map<String, Object> article = commonService.getHelpArticle(id);
        return ApiResponse.success("查询成功", article);
    }

    @Operation(summary = "搜索帮助文档", description = "根据关键词搜索帮助文档")
    @GetMapping("/help/search")
    public ApiResponse<Map<String, Object>> searchHelp(
            @Parameter(description = "搜索关键词", required = true)
            @RequestParam String keyword,
            
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> response = commonService.searchHelp(keyword, page, size);
        return ApiResponse.success("查询成功", response);
    }

    // ==================== 公告管理 ====================

    @Operation(summary = "获取公告列表", description = "获取平台公告列表，按时间倒序")
    @GetMapping("/announcements")
    public ApiResponse<Map<String, Object>> getAnnouncements(
            @Parameter(description = "公告类型（ALL/NEWS/UPDATE/NOTICE）")
            @RequestParam(required = false, defaultValue = "ALL") String type,
            
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> response = commonService.getAnnouncements(type, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "获取公告详情", description = "获取指定公告的详细内容")
    @GetMapping("/announcements/{id}")
    public ApiResponse<Map<String, Object>> getAnnouncementDetail(
            @Parameter(description = "公告ID", required = true)
            @PathVariable Long id) {
        Map<String, Object> announcement = commonService.getAnnouncementDetail(id);
        return ApiResponse.success("查询成功", announcement);
    }

    // ==================== 文件上传 ====================

    @Operation(summary = "上传图片", description = "上传图片文件，返回访问URL")
    @PostMapping("/upload/image")
    public ApiResponse<Map<String, Object>> uploadImage(
            @Parameter(description = "图片文件", required = true)
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "上传类型（AVATAR/GOODS/PARTTIME/OTHER）")
            @RequestParam(required = false, defaultValue = "OTHER") String uploadType) {
        Map<String, Object> response = commonService.uploadImage(file, uploadType);
        return ApiResponse.success("图片上传成功", response);
    }

    @Operation(summary = "上传文件", description = "上传通用文件（如Excel、PDF等）")
    @PostMapping("/upload/file")
    public ApiResponse<Map<String, Object>> uploadFile(
            @Parameter(description = "文件", required = true)
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> response = commonService.uploadFile(file);
        return ApiResponse.success("文件上传成功", response);
    }

    // ==================== 安全保障 ====================

    @Operation(summary = "获取安全保障说明", description = "获取平台安全保障机制说明")
    @GetMapping("/security/info")
    public ApiResponse<Map<String, Object>> getSecurityInfo() {
        Map<String, Object> securityInfo = commonService.getSecurityInfo();
        return ApiResponse.success("查询成功", securityInfo);
    }

    @Operation(summary = "获取隐私政策", description = "获取平台隐私政策文档内容")
    @GetMapping("/privacy-policy")
    public ApiResponse<Map<String, Object>> getPrivacyPolicy() {
        Map<String, Object> policy = commonService.getPrivacyPolicy();
        return ApiResponse.success("查询成功", policy);
    }

    @Operation(summary = "获取服务协议", description = "获取平台服务协议文档内容")
    @GetMapping("/service-agreement")
    public ApiResponse<Map<String, Object>> getServiceAgreement() {
        Map<String, Object> agreement = commonService.getServiceAgreement();
        return ApiResponse.success("查询成功", agreement);
    }

    // ==================== 用户反馈 ====================

    @Operation(summary = "提交用户反馈", description = "用户提交问题反馈或建议")
    @PostMapping("/feedback")
    public ApiResponse<Map<String, Object>> submitFeedback(
            HttpServletRequest request,
            @Parameter(description = "反馈类型（BUG/SUGGESTION/COMPLAINT/OTHER）", required = true)
            @RequestParam String feedbackType,
            
            @Parameter(description = "反馈标题", required = true)
            @RequestParam String title,
            
            @Parameter(description = "反馈内容", required = true)
            @RequestParam String content,
            
            @Parameter(description = "联系方式（邮箱或手机）")
            @RequestParam(required = false) String contact,
            
            @Parameter(description = "相关截图（Base64）")
            @RequestParam(required = false) String screenshots) {
        Long userId = UserContextUtils.getUserId(request);
        Map<String, Object> response = commonService.submitFeedback(userId, feedbackType, title, content, contact, screenshots);
        return ApiResponse.success("反馈提交成功", response);
    }

    @Operation(summary = "获取我的反馈列表", description = "获取当前用户提交的反馈记录")
    @GetMapping("/feedback/my-feedbacks")
    public ApiResponse<Map<String, Object>> getMyFeedbacks(
            HttpServletRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = commonService.getMyFeedbacks(userId, page, size);
        return ApiResponse.success("查询成功", response);
    }

    // ==================== 联系我们 ====================

    @Operation(summary = "获取联系方式", description = "获取平台官方联系方式")
    @GetMapping("/contact")
    public ApiResponse<Map<String, Object>> getContactInfo() {
        Map<String, Object> contact = commonService.getContactInfo();
        return ApiResponse.success("查询成功", contact);
    }

    @Operation(summary = "提交联系表单", description = "用户提交联系表单，平台会尽快回复")
    @PostMapping("/contact/submit")
    public ApiResponse<Map<String, Object>> submitContactForm(
            @Parameter(description = "联系类型（TECH/BUSINESS/UNIVERSITY/OTHER）", required = true)
            @RequestParam String contactType,
            
            @Parameter(description = "姓名", required = true)
            @RequestParam String name,
            
            @Parameter(description = "邮箱", required = true)
            @RequestParam String email,
            
            @Parameter(description = "手机号", required = true)
            @RequestParam String phone,
            
            @Parameter(description = "主题", required = true)
            @RequestParam String subject,
            
            @Parameter(description = "内容", required = true)
            @RequestParam String content) {
        Map<String, Object> response = commonService.submitContactForm(contactType, name, email, phone, subject, content);
        return ApiResponse.success("提交成功", response);
    }

    // ==================== 用户调研 ====================

    @Operation(summary = "获取调研问卷列表", description = "获取可参与的调研问卷列表")
    @GetMapping("/surveys")
    public ApiResponse<Map<String, Object>> getSurveys(
            @Parameter(description = "是否仅显示未完成的", example = "true")
            @RequestParam(defaultValue = "true") Boolean onlyUnfinished,
            
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> response = commonService.getSurveys(onlyUnfinished, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "获取问卷详情", description = "获取指定问卷的详细内容和题目")
    @GetMapping("/surveys/{surveyId}")
    public ApiResponse<Map<String, Object>> getSurveyDetail(
            @Parameter(description = "问卷ID", required = true)
            @PathVariable Long surveyId) {
        Map<String, Object> survey = commonService.getSurveyDetail(surveyId);
        return ApiResponse.success("查询成功", survey);
    }

    @Operation(summary = "提交问卷答案", description = "用户提交问卷答案")
    @PostMapping("/surveys/{surveyId}/submit")
    public ApiResponse<Map<String, Object>> submitSurvey(
            HttpServletRequest request,
            @Parameter(description = "问卷ID", required = true)
            @PathVariable Long surveyId,
            
            @Parameter(description = "答案JSON（题目ID和答案的映射）", required = true)
            @RequestBody Map<String, Object> answers) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = commonService.submitSurvey(userId, surveyId, answers);
        return ApiResponse.success("问卷提交成功", response);
    }

    // ==================== 平台介绍 ====================

    @Operation(summary = "获取平台介绍", description = "获取平台介绍信息")
    @GetMapping("/about")
    public ApiResponse<Map<String, Object>> getAboutInfo() {
        Map<String, Object> about = commonService.getAboutInfo();
        return ApiResponse.success("查询成功", about);
    }
}

