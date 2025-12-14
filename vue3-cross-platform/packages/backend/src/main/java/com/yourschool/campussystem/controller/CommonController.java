package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 通用功能接口
 * 包括帮助中心、公告管理、文件上传等
 */
@RestController
@RequestMapping("/api/common")
@Tag(name = "通用功能", description = "帮助中心、公告管理、文件上传等通用功能")
public class CommonController {

    // ==================== 帮助中心 ====================

    @Operation(summary = "获取帮助文档列表", description = "获取帮助中心的文档分类和列表")
    @GetMapping("/help/categories")
    public ApiResponse<Map<String, Object>> getHelpCategories() {
        List<Map<String, Object>> categories = Arrays.asList(
                createHelpCategory("注册登录", "用户注册、登录、密码找回等相关问题", 5),
                createHelpCategory("校园E卡通", "校园卡使用、充值、挂失等问题", 8),
                createHelpCategory("二手交易", "商品发布、交易流程、担保交易等问题", 6),
                createHelpCategory("兼职服务", "兼职发布、报名、审核等问题", 7),
                createHelpCategory("行程管理", "课程表导入、团队行程、同步等问题", 4),
                createHelpCategory("身份认证", "学生、教师、游客认证流程", 5)
        );

        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        response.put("total", categories.size());

        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "获取帮助文档详情", description = "获取指定帮助文档的详细内容")
    @GetMapping("/help/article/{id}")
    public ApiResponse<Map<String, Object>> getHelpArticle(
            @Parameter(description = "文档ID", required = true)
            @PathVariable Long id) {
        
        Map<String, Object> article = new HashMap<>();
        article.put("id", id);
        article.put("title", "如何申请学生身份认证？");
        article.put("content", "学生身份认证需要以下步骤：\n1. 准备学号和教务系统验证码\n2. 填写个人信息\n3. 提交认证申请\n4. 等待审核（3个工作日内）");
        article.put("category", "身份认证");
        article.put("viewCount", 1250);
        article.put("updateTime", LocalDateTime.now());

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
        
        Map<String, Object> response = new HashMap<>();
        response.put("list", Arrays.asList());
        response.put("total", 0L);
        response.put("keyword", keyword);

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
        
        List<Map<String, Object>> announcements = Arrays.asList(
                createAnnouncement(1L, "新功能上线", "校园E卡通人脸支付功能正式推出！", "NEWS", true),
                createAnnouncement(2L, "高校合作", "恭喜北京大学、清华大学成为首批官方接入院校。", "NEWS", false),
                createAnnouncement(3L, "安全提示", "二手交易防诈骗指南，请各位用户仔细阅读。", "NOTICE", false),
                createAnnouncement(4L, "商户招募", "校园商家入驻通道限时开放，享专属扶持计划。", "NEWS", false)
        );

        Map<String, Object> response = new HashMap<>();
        response.put("list", announcements);
        response.put("total", announcements.size());
        response.put("page", page);
        response.put("size", size);

        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "获取公告详情", description = "获取指定公告的详细内容")
    @GetMapping("/announcements/{id}")
    public ApiResponse<Map<String, Object>> getAnnouncementDetail(
            @Parameter(description = "公告ID", required = true)
            @PathVariable Long id) {
        
        Map<String, Object> announcement = new HashMap<>();
        announcement.put("id", id);
        announcement.put("title", "新功能上线");
        announcement.put("content", "校园E卡通人脸支付功能正式推出！现在您可以使用人脸识别进行校园卡支付，更加便捷安全。");
        announcement.put("type", "NEWS");
        announcement.put("isTop", true);
        announcement.put("publishTime", LocalDateTime.now().minusDays(2));
        announcement.put("viewCount", 3560);

        return ApiResponse.success("查询成功", announcement);
    }

    // ==================== 文件上传 ====================

    @Operation(summary = "上传图片", description = "上传图片文件，返回访问URL")
    @PostMapping("/upload/image")
    public ApiResponse<Map<String, Object>> uploadImage(
            @Parameter(description = "图片文件", required = true)
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            
            @Parameter(description = "上传类型（AVATAR/GOODS/PARTTIME/OTHER）")
            @RequestParam(required = false, defaultValue = "OTHER") String uploadType) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("url", "https://example.com/uploads/" + System.currentTimeMillis() + ".jpg");
        response.put("fileName", file.getOriginalFilename());
        response.put("fileSize", file.getSize());
        response.put("uploadTime", LocalDateTime.now());

        return ApiResponse.success("图片上传成功", response);
    }

    @Operation(summary = "上传文件", description = "上传通用文件（如Excel、PDF等）")
    @PostMapping("/upload/file")
    public ApiResponse<Map<String, Object>> uploadFile(
            @Parameter(description = "文件", required = true)
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("url", "https://example.com/files/" + System.currentTimeMillis() + "_" + file.getOriginalFilename());
        response.put("fileName", file.getOriginalFilename());
        response.put("fileSize", file.getSize());
        response.put("fileType", file.getContentType());
        response.put("uploadTime", LocalDateTime.now());

        return ApiResponse.success("文件上传成功", response);
    }

    // ==================== 安全保障 ====================

    @Operation(summary = "获取安全保障说明", description = "获取平台安全保障机制说明")
    @GetMapping("/security/info")
    public ApiResponse<Map<String, Object>> getSecurityInfo() {
        Map<String, Object> securityInfo = new HashMap<>();
        securityInfo.put("encryption", "采用加密传输技术存储个人信息");
        securityInfo.put("locationPermission", "定位权限分级调用");
        securityInfo.put("transactionEncryption", "交易记录AES加密存储");
        securityInfo.put("dataProtection", "确保用户信息安全");
        
        return ApiResponse.success("查询成功", securityInfo);
    }

    @Operation(summary = "获取隐私政策", description = "获取平台隐私政策文档内容")
    @GetMapping("/privacy-policy")
    public ApiResponse<Map<String, Object>> getPrivacyPolicy() {
        Map<String, Object> policy = new HashMap<>();
        policy.put("title", "隐私政策");
        policy.put("content", "平台严格遵守相关法律法规，保护用户隐私信息...");
        policy.put("version", "1.0");
        policy.put("updateTime", LocalDateTime.now().minusMonths(1));
        
        return ApiResponse.success("查询成功", policy);
    }

    @Operation(summary = "获取服务协议", description = "获取平台服务协议文档内容")
    @GetMapping("/service-agreement")
    public ApiResponse<Map<String, Object>> getServiceAgreement() {
        Map<String, Object> agreement = new HashMap<>();
        agreement.put("title", "服务协议");
        agreement.put("content", "用户在使用平台服务前，需同意本服务协议...");
        agreement.put("version", "1.0");
        agreement.put("updateTime", LocalDateTime.now().minusMonths(1));
        
        return ApiResponse.success("查询成功", agreement);
    }

    // ==================== 用户反馈 ====================

    @Operation(summary = "提交用户反馈", description = "用户提交问题反馈或建议")
    @PostMapping("/feedback")
    public ApiResponse<Map<String, Object>> submitFeedback(
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
        
        Map<String, Object> response = new HashMap<>();
        response.put("feedbackId", System.currentTimeMillis());
        response.put("status", "SUBMITTED");
        response.put("message", "反馈已提交，我们会在3个工作日内处理");
        response.put("submitTime", LocalDateTime.now());
        
        return ApiResponse.success("反馈提交成功", response);
    }

    @Operation(summary = "获取我的反馈列表", description = "获取当前用户提交的反馈记录")
    @GetMapping("/feedback/my-feedbacks")
    public ApiResponse<Map<String, Object>> getMyFeedbacks(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("list", Arrays.asList());
        response.put("total", 0L);
        response.put("page", page);
        response.put("size", size);
        
        return ApiResponse.success("查询成功", response);
    }

    // ==================== 联系我们 ====================

    @Operation(summary = "获取联系方式", description = "获取平台官方联系方式")
    @GetMapping("/contact")
    public ApiResponse<Map<String, Object>> getContactInfo() {
        Map<String, Object> contact = new HashMap<>();
        contact.put("serviceHotline", "400-123-4567");
        contact.put("techSupport", "tech@campus.edu.cn");
        contact.put("businessCooperation", "business@campus.edu.cn");
        contact.put("universityAccess", "university@campus.edu.cn");
        contact.put("workingHours", "周一至周五 9:00-18:00");
        
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
        
        Map<String, Object> response = new HashMap<>();
        response.put("contactId", System.currentTimeMillis());
        response.put("message", "联系表单已提交，我们会在2个工作日内回复");
        response.put("submitTime", LocalDateTime.now());
        
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
        
        List<Map<String, Object>> surveys = Arrays.asList(
                createSurvey(1L, "关于提升平台体验的问卷调查", "期待您的参与", false, LocalDateTime.now().minusDays(5))
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("list", surveys);
        response.put("total", surveys.size());
        
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "获取问卷详情", description = "获取指定问卷的详细内容和题目")
    @GetMapping("/surveys/{surveyId}")
    public ApiResponse<Map<String, Object>> getSurveyDetail(
            @Parameter(description = "问卷ID", required = true)
            @PathVariable Long surveyId) {
        
        Map<String, Object> survey = new HashMap<>();
        survey.put("id", surveyId);
        survey.put("title", "关于提升平台体验的问卷调查");
        survey.put("description", "为了提升平台服务质量，我们诚邀您参与本次调研");
        survey.put("questions", Arrays.asList());
        survey.put("isCompleted", false);
        survey.put("deadline", LocalDateTime.now().plusDays(30));
        
        return ApiResponse.success("查询成功", survey);
    }

    @Operation(summary = "提交问卷答案", description = "用户提交问卷答案")
    @PostMapping("/surveys/{surveyId}/submit")
    public ApiResponse<Map<String, Object>> submitSurvey(
            @Parameter(description = "问卷ID", required = true)
            @PathVariable Long surveyId,
            
            @Parameter(description = "答案JSON（题目ID和答案的映射）", required = true)
            @RequestBody Map<String, Object> answers) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("surveyId", surveyId);
        response.put("message", "问卷提交成功，感谢您的参与");
        response.put("submitTime", LocalDateTime.now());
        
        return ApiResponse.success("问卷提交成功", response);
    }

    // ==================== 平台介绍 ====================

    @Operation(summary = "获取平台介绍", description = "获取平台介绍信息")
    @GetMapping("/about")
    public ApiResponse<Map<String, Object>> getAboutInfo() {
        Map<String, Object> about = new HashMap<>();
        about.put("platformName", "上大学Online");
        about.put("slogan", "让校园生活更简单");
        about.put("vision", "实现'一校一集合、一人一身份、一站全服务、一策保安全'");
        about.put("description", "一款以'高校官方注册封装用户群体'为核心的校园综合服务平台");
        about.put("features", Arrays.asList(
                "一校一集合，一人一身份",
                "一站全服务，一策保安全",
                "基础功能+高校定制功能",
                "多源融合定位、支付接口对接"
        ));
        
        return ApiResponse.success("查询成功", about);
    }

    // ==================== 辅助方法 ====================

    private Map<String, Object> createHelpCategory(String name, String description, Integer articleCount) {
        Map<String, Object> category = new HashMap<>();
        category.put("name", name);
        category.put("description", description);
        category.put("articleCount", articleCount);
        return category;
    }

    private Map<String, Object> createAnnouncement(Long id, String title, String content, String type, Boolean isTop) {
        Map<String, Object> announcement = new HashMap<>();
        announcement.put("id", id);
        announcement.put("title", title);
        announcement.put("content", content);
        announcement.put("type", type);
        announcement.put("isTop", isTop);
        announcement.put("publishTime", LocalDateTime.now().minusDays(id));
        announcement.put("viewCount", id * 100);
        return announcement;
    }

    private Map<String, Object> createSurvey(Long id, String title, String description, Boolean isCompleted, LocalDateTime createTime) {
        Map<String, Object> survey = new HashMap<>();
        survey.put("id", id);
        survey.put("title", title);
        survey.put("description", description);
        survey.put("isCompleted", isCompleted);
        survey.put("createTime", createTime);
        survey.put("deadline", createTime.plusDays(30));
        return survey;
    }
}

