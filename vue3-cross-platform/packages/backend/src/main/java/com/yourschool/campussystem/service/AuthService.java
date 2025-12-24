package com.yourschool.campussystem.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 身份认证服务接口
 */
public interface AuthService {

    /**
     * 学生身份认证申请
     */
    Map<String, Object> applyStudentAuth(Long userId, String studentId, String verificationCode,
                                         String name, String idCard, Long schoolId);

    /**
     * 教师身份认证申请
     */
    Map<String, Object> applyTeacherAuth(Long userId, String teacherId, String name, String idCard,
                                        Long schoolId, MultipartFile certificateFile);

    /**
     * 游客刷脸活体检测
     */
    Map<String, Object> visitorFaceDetect(String faceImage, String name, String phone,
                                          String idCard, String reason);

    /**
     * 游客进校登记
     */
    Map<String, Object> visitorRegister(String name, String phone, String idCard,
                                        String reason, Integer stayHours, Long schoolId);

    /**
     * 查询认证申请状态
     */
    Map<String, Object> getAuthApplyStatus(Long applyId);

    /**
     * 高校官方接入申请
     */
    Map<String, Object> applyUniversityAccess(String universityName, String universityCode,
                                              String contactName, String contactPhone,
                                              String contactEmail, MultipartFile certificateFile,
                                              String description);

    /**
     * 获取认证申请记录
     */
    Map<String, Object> getAuthApplyRecords(Long userId, Integer page, Integer size);

    /**
     * 发送邮箱验证码
     */
    Map<String, Object> sendEmailCode(String email);

    /**
     * 邮箱登录
     */
    Map<String, Object> emailLogin(String email, String code, String codeId, HttpServletRequest request);

    /**
     * 发送重置密码的邮箱验证码
     * @param email 用户注册时绑定的邮箱地址
     * @return 包含验证码ID和过期时间等信息的Map
     */
    Map<String, Object> sendResetPasswordEmailCode(String email);

    /**
     * 使用邮箱验证码重置登录密码
     * @param email 用户邮箱地址
     * @param code 邮箱收到的验证码
     * @param codeId 验证码ID（从发送验证码接口返回）
     * @param newPassword 新密码明文（会在服务中进行加密）
     * @return 包含用户ID和邮箱等基础信息的Map
     */
    Map<String, Object> resetPasswordByEmail(String email, String code, String codeId, String newPassword);
}
