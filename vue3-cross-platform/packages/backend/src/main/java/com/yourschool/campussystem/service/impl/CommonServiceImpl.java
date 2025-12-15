package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourschool.campussystem.entity.*;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.*;
import com.yourschool.campussystem.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用服务实现类
 */
@Service
@RequiredArgsConstructor
public class CommonServiceImpl extends ServiceImpl<HelpArticleMapper, HelpArticle> implements CommonService {

    private final HelpArticleMapper helpArticleMapper;
    private final AnnouncementMapper announcementMapper;
    private final FeedbackMapper feedbackMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:http://localhost:8080/uploads}")
    private String urlPrefix;

    // ==================== 帮助中心 ====================

    @Override
    public Map<String, Object> getHelpCategories() {
        // 查询所有帮助文档，按分类统计
        List<HelpArticle> articles = helpArticleMapper.selectList(null);
        Map<String, Long> categoryCount = articles.stream()
                .collect(Collectors.groupingBy(HelpArticle::getCategory, Collectors.counting()));

        List<Map<String, Object>> categories = new ArrayList<>();
        categories.add(createCategory("注册登录", "用户注册、登录、密码找回等相关问题", 
                categoryCount.getOrDefault("注册登录", 0L).intValue()));
        categories.add(createCategory("校园E卡通", "校园卡使用、充值、挂失等问题", 
                categoryCount.getOrDefault("校园E卡通", 0L).intValue()));
        categories.add(createCategory("二手交易", "商品发布、交易流程、担保交易等问题", 
                categoryCount.getOrDefault("二手交易", 0L).intValue()));
        categories.add(createCategory("兼职服务", "兼职发布、报名、审核等问题", 
                categoryCount.getOrDefault("兼职服务", 0L).intValue()));
        categories.add(createCategory("行程管理", "课程表导入、团队行程、同步等问题", 
                categoryCount.getOrDefault("行程管理", 0L).intValue()));
        categories.add(createCategory("身份认证", "学生、教师、游客认证流程", 
                categoryCount.getOrDefault("身份认证", 0L).intValue()));

        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        response.put("total", categories.size());

        return response;
    }

    @Override
    public Map<String, Object> getHelpArticle(Long articleId) {
        HelpArticle article = helpArticleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        // 增加浏览次数
        article.setViewCount(article.getViewCount() + 1);
        helpArticleMapper.updateById(article);

        Map<String, Object> result = new HashMap<>();
        result.put("id", article.getId());
        result.put("title", article.getTitle());
        result.put("content", article.getContent());
        result.put("category", article.getCategory());
        result.put("viewCount", article.getViewCount());
        result.put("updateTime", article.getUpdateTime());

        return result;
    }

    @Override
    public Map<String, Object> searchHelp(String keyword, Integer page, Integer size) {
        LambdaQueryWrapper<HelpArticle> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(HelpArticle::getTitle, keyword)
                    .or()
                    .like(HelpArticle::getContent, keyword)
            );
        }

        Page<HelpArticle> pageObj = new Page<>(page, size);
        Page<HelpArticle> result = helpArticleMapper.selectPage(pageObj, queryWrapper);

        List<Map<String, Object>> list = result.getRecords().stream()
                .map(article -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", article.getId());
                    item.put("title", article.getTitle());
                    item.put("category", article.getCategory());
                    item.put("viewCount", article.getViewCount());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", result.getTotal());
        response.put("keyword", keyword);

        return response;
    }

    // ==================== 公告管理 ====================

    @Override
    public Map<String, Object> getAnnouncements(String type, Integer page, Integer size) {
        LambdaQueryWrapper<Announcement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Announcement::getIsDeleted, 0);

        if (StringUtils.hasText(type) && !"ALL".equals(type)) {
            queryWrapper.eq(Announcement::getType, type);
        }

        // 置顶的在前
        queryWrapper.orderByDesc(Announcement::getIsTop);
        queryWrapper.orderByDesc(Announcement::getPublishTime);

        Page<Announcement> pageObj = new Page<>(page, size);
        Page<Announcement> result = announcementMapper.selectPage(pageObj, queryWrapper);

        List<Map<String, Object>> list = result.getRecords().stream()
                .map(announcement -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", announcement.getId());
                    item.put("title", announcement.getTitle());
                    item.put("content", announcement.getContent());
                    item.put("type", announcement.getType());
                    item.put("isTop", announcement.getIsTop());
                    item.put("publishTime", announcement.getPublishTime());
                    item.put("viewCount", announcement.getViewCount());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", result.getTotal());
        response.put("page", page);
        response.put("size", size);

        return response;
    }

    @Override
    public Map<String, Object> getAnnouncementDetail(Long announcementId) {
        Announcement announcement = announcementMapper.selectById(announcementId);
        if (announcement == null || announcement.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        // 增加浏览次数
        announcement.setViewCount(announcement.getViewCount() + 1);
        announcementMapper.updateById(announcement);

        Map<String, Object> result = new HashMap<>();
        result.put("id", announcement.getId());
        result.put("title", announcement.getTitle());
        result.put("content", announcement.getContent());
        result.put("type", announcement.getType());
        result.put("isTop", announcement.getIsTop());
        result.put("publishTime", announcement.getPublishTime());
        result.put("viewCount", announcement.getViewCount());

        return result;
    }

    // ==================== 文件上传 ====================

    @Override
    @Transactional
    public Map<String, Object> uploadImage(MultipartFile file, String uploadType) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!Arrays.asList("jpg", "jpeg", "png", "gif", "webp").contains(extension)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        // 验证文件大小（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        try {
            // 创建上传目录
            String dir = uploadType != null ? uploadType.toLowerCase() : "other";
            Path uploadDir = Paths.get(uploadPath, dir);
            Files.createDirectories(uploadDir);

            // 生成文件名
            String fileName = System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = uploadDir.resolve(fileName);

            // 保存文件
            file.transferTo(filePath.toFile());

            // 生成访问URL
            String url = urlPrefix + "/" + dir + "/" + fileName;

            Map<String, Object> response = new HashMap<>();
            response.put("url", url);
            response.put("fileName", originalFilename);
            response.put("fileSize", file.getSize());
            response.put("uploadTime", LocalDateTime.now());

            return response;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        try {
            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath, "files");
            Files.createDirectories(uploadDir);

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String fileName = System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = uploadDir.resolve(fileName);

            // 保存文件
            file.transferTo(filePath.toFile());

            // 生成访问URL
            String url = urlPrefix + "/files/" + fileName;

            Map<String, Object> response = new HashMap<>();
            response.put("url", url);
            response.put("fileName", originalFilename);
            response.put("fileSize", file.getSize());
            response.put("fileType", file.getContentType());
            response.put("uploadTime", LocalDateTime.now());

            return response;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // ==================== 安全保障 ====================

    @Override
    public Map<String, Object> getSecurityInfo() {
        Map<String, Object> securityInfo = new HashMap<>();
        securityInfo.put("encryption", "采用加密传输技术存储个人信息");
        securityInfo.put("locationPermission", "定位权限分级调用");
        securityInfo.put("transactionEncryption", "交易记录AES加密存储");
        securityInfo.put("dataProtection", "确保用户信息安全");
        return securityInfo;
    }

    @Override
    public Map<String, Object> getPrivacyPolicy() {
        Map<String, Object> policy = new HashMap<>();
        policy.put("title", "隐私政策");
        policy.put("content", "平台严格遵守相关法律法规，保护用户隐私信息。我们采用加密传输技术存储个人信息，定位权限分级调用，交易记录AES加密存储，确保用户信息安全。");
        policy.put("version", "1.0");
        policy.put("updateTime", LocalDateTime.now().minusMonths(1));
        return policy;
    }

    @Override
    public Map<String, Object> getServiceAgreement() {
        Map<String, Object> agreement = new HashMap<>();
        agreement.put("title", "服务协议");
        agreement.put("content", "用户在使用平台服务前，需同意本服务协议。平台提供校园E卡通、二手交易、兼职服务、行程管理等功能，用户应遵守相关使用规范。");
        agreement.put("version", "1.0");
        agreement.put("updateTime", LocalDateTime.now().minusMonths(1));
        return agreement;
    }

    // ==================== 用户反馈 ====================

    @Override
    @Transactional
    public Map<String, Object> submitFeedback(Long userId, String feedbackType, String title, 
                                              String content, String contact, String screenshots) {
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setFeedbackType(feedbackType);
        feedback.setTitle(title);
        feedback.setContent(content);
        feedback.setContact(contact);
        feedback.setScreenshots(screenshots);
        feedback.setStatus("SUBMITTED");
        feedback.setSubmitTime(LocalDateTime.now());

        feedbackMapper.insert(feedback);

        Map<String, Object> response = new HashMap<>();
        response.put("feedbackId", feedback.getId());
        response.put("status", feedback.getStatus());
        response.put("message", "反馈已提交，我们会在3个工作日内处理");
        response.put("submitTime", feedback.getSubmitTime());

        return response;
    }

    @Override
    public Map<String, Object> getMyFeedbacks(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Feedback> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Feedback::getUserId, userId)
                .orderByDesc(Feedback::getSubmitTime);

        Page<Feedback> pageObj = new Page<>(page, size);
        Page<Feedback> result = feedbackMapper.selectPage(pageObj, queryWrapper);

        List<Map<String, Object>> list = result.getRecords().stream()
                .map(feedback -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", feedback.getId());
                    item.put("feedbackType", feedback.getFeedbackType());
                    item.put("title", feedback.getTitle());
                    item.put("content", feedback.getContent());
                    item.put("status", feedback.getStatus());
                    item.put("submitTime", feedback.getSubmitTime());
                    item.put("processTime", feedback.getProcessTime());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", result.getTotal());
        response.put("page", page);
        response.put("size", size);

        return response;
    }

    // ==================== 联系我们 ====================

    @Override
    public Map<String, Object> getContactInfo() {
        Map<String, Object> contact = new HashMap<>();
        contact.put("serviceHotline", "400-123-4567");
        contact.put("techSupport", "tech@campus.edu.cn");
        contact.put("businessCooperation", "business@campus.edu.cn");
        contact.put("universityAccess", "university@campus.edu.cn");
        contact.put("workingHours", "周一至周五 9:00-18:00");
        return contact;
    }

    @Override
    @Transactional
    public Map<String, Object> submitContactForm(String contactType, String name, String email, 
                                                 String phone, String subject, String content) {
        // 这里可以保存到数据库，暂时简化处理
        Map<String, Object> response = new HashMap<>();
        response.put("contactId", System.currentTimeMillis());
        response.put("message", "联系表单已提交，我们会在2个工作日内回复");
        response.put("submitTime", LocalDateTime.now());
        return response;
    }

    // ==================== 用户调研 ====================

    @Override
    public Map<String, Object> getSurveys(Boolean onlyUnfinished, Integer page, Integer size) {
        // 简化处理，实际应该有调研表
        List<Map<String, Object>> surveys = new ArrayList<>();
        Map<String, Object> survey = new HashMap<>();
        survey.put("id", 1L);
        survey.put("title", "关于提升平台体验的问卷调查");
        survey.put("description", "期待您的参与");
        survey.put("isCompleted", false);
        survey.put("createTime", LocalDateTime.now().minusDays(5));
        survey.put("deadline", LocalDateTime.now().plusDays(30));
        surveys.add(survey);

        Map<String, Object> response = new HashMap<>();
        response.put("list", surveys);
        response.put("total", surveys.size());
        return response;
    }

    @Override
    public Map<String, Object> getSurveyDetail(Long surveyId) {
        Map<String, Object> survey = new HashMap<>();
        survey.put("id", surveyId);
        survey.put("title", "关于提升平台体验的问卷调查");
        survey.put("description", "为了提升平台服务质量，我们诚邀您参与本次调研");
        survey.put("questions", new ArrayList<>());
        survey.put("isCompleted", false);
        survey.put("deadline", LocalDateTime.now().plusDays(30));
        return survey;
    }

    @Override
    @Transactional
    public Map<String, Object> submitSurvey(Long userId, Long surveyId, Map<String, Object> answers) {
        // 简化处理，实际应该保存到数据库
        Map<String, Object> response = new HashMap<>();
        response.put("surveyId", surveyId);
        response.put("message", "问卷提交成功，感谢您的参与");
        response.put("submitTime", LocalDateTime.now());
        return response;
    }

    // ==================== 平台介绍 ====================

    @Override
    public Map<String, Object> getAboutInfo() {
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
        return about;
    }

    // ==================== 辅助方法 ====================

    private Map<String, Object> createCategory(String name, String description, Integer articleCount) {
        Map<String, Object> category = new HashMap<>();
        category.put("name", name);
        category.put("description", description);
        category.put("articleCount", articleCount);
        return category;
    }
}
