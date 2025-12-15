package com.yourschool.campussystem.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 通用服务接口
 */
public interface CommonService {

    // ==================== 帮助中心 ====================

    /**
     * 获取帮助文档分类列表
     */
    Map<String, Object> getHelpCategories();

    /**
     * 获取帮助文档详情
     */
    Map<String, Object> getHelpArticle(Long articleId);

    /**
     * 搜索帮助文档
     */
    Map<String, Object> searchHelp(String keyword, Integer page, Integer size);

    // ==================== 公告管理 ====================

    /**
     * 获取公告列表
     */
    Map<String, Object> getAnnouncements(String type, Integer page, Integer size);

    /**
     * 获取公告详情
     */
    Map<String, Object> getAnnouncementDetail(Long announcementId);

    // ==================== 文件上传 ====================

    /**
     * 上传图片
     */
    Map<String, Object> uploadImage(MultipartFile file, String uploadType);

    /**
     * 上传文件
     */
    Map<String, Object> uploadFile(MultipartFile file);

    // ==================== 安全保障 ====================

    /**
     * 获取安全保障说明
     */
    Map<String, Object> getSecurityInfo();

    /**
     * 获取隐私政策
     */
    Map<String, Object> getPrivacyPolicy();

    /**
     * 获取服务协议
     */
    Map<String, Object> getServiceAgreement();

    // ==================== 用户反馈 ====================

    /**
     * 提交用户反馈
     */
    Map<String, Object> submitFeedback(Long userId, String feedbackType, String title, 
                                      String content, String contact, String screenshots);

    /**
     * 获取我的反馈列表
     */
    Map<String, Object> getMyFeedbacks(Long userId, Integer page, Integer size);

    // ==================== 联系我们 ====================

    /**
     * 获取联系方式
     */
    Map<String, Object> getContactInfo();

    /**
     * 提交联系表单
     */
    Map<String, Object> submitContactForm(String contactType, String name, String email, 
                                          String phone, String subject, String content);

    // ==================== 用户调研 ====================

    /**
     * 获取调研问卷列表
     */
    Map<String, Object> getSurveys(Boolean onlyUnfinished, Integer page, Integer size);

    /**
     * 获取问卷详情
     */
    Map<String, Object> getSurveyDetail(Long surveyId);

    /**
     * 提交问卷答案
     */
    Map<String, Object> submitSurvey(Long userId, Long surveyId, Map<String, Object> answers);

    // ==================== 平台介绍 ====================

    /**
     * 获取平台介绍
     */
    Map<String, Object> getAboutInfo();
}
