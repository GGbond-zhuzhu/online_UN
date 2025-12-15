package com.yourschool.campussystem.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 高校管理服务接口
 */
public interface UniversityService {

    // ==================== 高校信息管理 ====================

    /**
     * 获取高校信息
     */
    Map<String, Object> getUniversityInfo(Long universityId);

    /**
     * 更新高校信息
     */
    Map<String, Object> updateUniversityInfo(Long universityId, String universityName,
                                            String contactName, String contactPhone,
                                            String contactEmail, String description);

    // ==================== 用户管理 ====================

    /**
     * 获取本校用户列表
     */
    Map<String, Object> getUniversityUsers(Long universityId, String role, String authStatus,
                                           String keyword, Integer page, Integer size);

    /**
     * 审核本校用户认证申请
     */
    void reviewUserAuthApply(Long universityId, Long applyId, String result, String note, Long reviewerId);

    /**
     * 批量导入用户
     */
    Map<String, Object> importUsers(Long universityId, MultipartFile file, String userType);

    /**
     * 导出用户列表
     */
    org.springframework.core.io.Resource exportUsers(Long universityId, String role);

    // ==================== 学生信息管理 ====================

    /**
     * 获取学生信息列表
     */
    Map<String, Object> getStudentInfoList(Long universityId, String keyword, String grade, String major, Integer page, Integer size);

    /**
     * 添加学生信息
     */
    Map<String, Object> addStudentInfo(Long universityId, String studentId, String name, String idCard, String major, String grade, String className);

    /**
     * 批量导入学生信息（Excel）
     */
    Map<String, Object> importStudentInfo(Long universityId, MultipartFile file);

    /**
     * 导出学生信息（Excel）
     */
    org.springframework.core.io.Resource exportStudentInfo(Long universityId);

    /**
     * 更新学生信息
     */
    Map<String, Object> updateStudentInfo(Long universityId, Long studentInfoId, String major, String grade, String className, String status);

    /**
     * 删除学生信息
     */
    void deleteStudentInfo(Long universityId, Long studentInfoId);

    // ==================== 教师信息管理 ====================

    /**
     * 获取教师信息列表
     */
    Map<String, Object> getTeacherInfoList(Long universityId, String keyword, String department, String title, Integer page, Integer size);

    /**
     * 添加教师信息
     */
    Map<String, Object> addTeacherInfo(Long universityId, String teacherId, String name, String idCard, String department, String title, String phone, String email);

    /**
     * 批量导入教师信息（Excel）
     */
    Map<String, Object> importTeacherInfo(Long universityId, MultipartFile file);

    /**
     * 导出教师信息（Excel）
     */
    org.springframework.core.io.Resource exportTeacherInfo(Long universityId);

    /**
     * 更新教师信息
     */
    Map<String, Object> updateTeacherInfo(Long universityId, Long teacherInfoId, String department, String title, String phone, String email, String status);

    /**
     * 删除教师信息
     */
    void deleteTeacherInfo(Long universityId, Long teacherInfoId);

    // ==================== 功能配置 ====================

    /**
     * 获取功能配置
     */
    Map<String, Object> getUniversityConfig(Long universityId);

    /**
     * 更新功能配置
     */
    void updateUniversityConfig(Long universityId, Map<String, Object> config);

    // ==================== 数据统计 ====================

    /**
     * 获取高校数据统计
     */
    Map<String, Object> getUniversityStatistics(Long universityId, String timeRange);

    /**
     * 获取用户活跃度统计
     */
    Map<String, Object> getUserActivityStatistics(Long universityId, String startDate, String endDate);

    /**
     * 获取本校内容统计
     */
    Map<String, Object> getContentStatistics(Long universityId);

    // ==================== 内容管理 ====================

    /**
     * 审核本校内容
     */
    void reviewContent(Long universityId, Long contentId, String contentType, String result, String note, Long reviewerId);

    // ==================== 通知推送 ====================

    /**
     * 发送通知
     */
    Map<String, Object> sendNotification(Long universityId, String title, String content,
                                        String targetType, Boolean isUrgent);

    /**
     * 获取通知发送记录
     */
    Map<String, Object> getNotificationHistory(Long universityId, Integer page, Integer size);
}
