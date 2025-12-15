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
}
