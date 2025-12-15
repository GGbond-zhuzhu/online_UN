package com.yourschool.campussystem.service;

import java.util.Map;

/**
 * 管理员服务接口
 */
public interface AdminService {

    // ==================== 用户管理 ====================

    /**
     * 获取用户列表
     */
    Map<String, Object> getUserList(Integer page, Integer size, String role, Long schoolId, String keyword);

    /**
     * 更新用户状态
     */
    void updateUserStatus(Long userId, String status);

    /**
     * 重置用户密码
     */
    String resetUserPassword(Long userId);

    // ==================== 认证审核 ====================

    /**
     * 获取认证申请列表
     */
    Map<String, Object> getAuthApplies(String status, Integer page, Integer size);

    /**
     * 审核认证申请
     */
    void reviewAuthApply(Long applyId, String result, String note, Long auditorId);

    // ==================== 内容审核 ====================

    /**
     * 获取待审核内容列表
     */
    Map<String, Object> getReviewContentList(String contentType, Integer page, Integer size);

    /**
     * 审核内容
     */
    void reviewContent(Long contentId, String contentType, String result, String note, Long auditorId);

    // ==================== 高校管理 ====================

    /**
     * 获取高校列表
     */
    Map<String, Object> getUniversityList(Integer page, Integer size);

    /**
     * 审核高校接入申请
     */
    void reviewUniversityApply(Long applyId, String result, String note, Long auditorId);

    // ==================== 系统配置 ====================

    /**
     * 获取系统配置
     */
    Map<String, Object> getSystemConfig();

    /**
     * 更新系统配置
     */
    void updateSystemConfig(Map<String, Object> config);

    // ==================== 数据统计 ====================

    /**
     * 获取平台数据统计
     */
    Map<String, Object> getStatistics();
}
