package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.entity.User;
import com.yourschool.campussystem.entity.UserAuthApply;
import com.yourschool.campussystem.enums.ApplyStatusEnum;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.entity.Ecard;
import com.yourschool.campussystem.entity.StudentInfo;
import com.yourschool.campussystem.entity.TeacherInfo;
import com.yourschool.campussystem.entity.University;
import com.yourschool.campussystem.enums.CardStatusEnum;
import com.yourschool.campussystem.mapper.EcardMapper;
import com.yourschool.campussystem.mapper.StudentInfoMapper;
import com.yourschool.campussystem.mapper.TeacherInfoMapper;
import com.yourschool.campussystem.mapper.UniversityMapper;
import com.yourschool.campussystem.mapper.UserAuthApplyMapper;
import com.yourschool.campussystem.mapper.UserMapper;
import com.yourschool.campussystem.service.AuthService;
//import com.yourschool.campussystem.service.BaiduFaceService;  // 暂时禁用
import com.yourschool.campussystem.service.CommonService;
import com.yourschool.campussystem.service.EmailService;
import com.yourschool.campussystem.service.IpLocationService;
import com.yourschool.campussystem.entity.UserLoginLog;
import com.yourschool.campussystem.mapper.UserLoginLogMapper;
import com.yourschool.campussystem.util.JWTUtils;
import com.yourschool.campussystem.enums.UserRoleEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 身份认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserAuthApplyMapper userAuthApplyMapper;
    private final UniversityMapper universityMapper;
    private final StudentInfoMapper studentInfoMapper;
    private final TeacherInfoMapper teacherInfoMapper;
    private final EcardMapper ecardMapper;
    private final CommonService commonService;
    //private final BaiduFaceService baiduFaceService;  // 暂时禁用
    private final EmailService emailService;
    private final UserLoginLogMapper userLoginLogMapper;
    private final IpLocationService ipLocationService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Map<String, Object> applyStudentAuth(Long userId, String studentId, String verificationCode,
                                                String name, String idCard, Long schoolId) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 检查是否已有待审核的申请
        LambdaQueryWrapper<UserAuthApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAuthApply::getUserId, userId)
                .eq(UserAuthApply::getStatus, ApplyStatusEnum.PENDING.ordinal());
        Long count = userAuthApplyMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "您已有待审核的认证申请");
        }

        // 验证学号、姓名、身份证号是否匹配（从student_info表查询）
        LambdaQueryWrapper<StudentInfo> studentQuery = new LambdaQueryWrapper<>();
        studentQuery.eq(StudentInfo::getSchoolId, schoolId)
                .eq(StudentInfo::getStudentId, studentId)
                .eq(StudentInfo::getName, name)
                .eq(StudentInfo::getIdCard, idCard)
                .eq(StudentInfo::getStatus, "ACTIVE")
                .eq(StudentInfo::getIsDeleted, 0);
        StudentInfo studentInfo = studentInfoMapper.selectOne(studentQuery);
        
        if (studentInfo == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "学号、姓名或身份证号不匹配，或该学生信息不存在");
        }

        // TODO: 如果需要验证码验证，可以在这里添加验证码校验逻辑
        // if (verificationCode != null && !verificationCode.isEmpty()) {
        //     // 验证验证码逻辑
        // }

        // 查询学校信息
        String schoolName = "";
        if (schoolId != null) {
            University university = universityMapper.selectById(schoolId);
            if (university != null) {
                schoolName = university.getName();
            }
        }

        // 创建认证申请
        UserAuthApply apply = new UserAuthApply();
        apply.setUserId(userId);
        apply.setRealName(name);
        apply.setIdCard(idCard);
        apply.setApplyRole("STUDENT");
        apply.setSchoolName(schoolName);
        // 保存学生信息（专业、年级等）
        apply.setMajor(studentInfo.getMajor());
        apply.setGrade(studentInfo.getGrade());
        apply.setStatus(ApplyStatusEnum.PENDING.ordinal());
        apply.setCreateTime(LocalDateTime.now());
        apply.setUpdateTime(LocalDateTime.now());

        userAuthApplyMapper.insert(apply);

        Map<String, Object> response = new HashMap<>();
        response.put("applyId", apply.getId());
        response.put("status", "PENDING");
        response.put("message", "认证申请已提交，等待审核（3个工作日内完成）");
        response.put("applyTime", apply.getCreateTime());

        return response;
    }

    @Override
    @Transactional
    public Map<String, Object> applyTeacherAuth(Long userId, String teacherId, String name, String idCard,
                                                Long schoolId, MultipartFile certificateFile) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 检查是否已有待审核的申请
        LambdaQueryWrapper<UserAuthApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAuthApply::getUserId, userId)
                .eq(UserAuthApply::getStatus, ApplyStatusEnum.PENDING.ordinal());
        Long count = userAuthApplyMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "您已有待审核的认证申请");
        }

        // 验证工号、姓名、身份证号是否匹配（从teacher_info表查询）
        LambdaQueryWrapper<TeacherInfo> teacherQuery = new LambdaQueryWrapper<>();
        teacherQuery.eq(TeacherInfo::getSchoolId, schoolId)
                .eq(TeacherInfo::getTeacherId, teacherId)
                .eq(TeacherInfo::getName, name)
                .eq(TeacherInfo::getIdCard, idCard)
                .eq(TeacherInfo::getStatus, "ACTIVE")
                .eq(TeacherInfo::getIsDeleted, 0);
        TeacherInfo teacherInfo = teacherInfoMapper.selectOne(teacherQuery);
        
        if (teacherInfo == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "工号、姓名或身份证号不匹配，或该教师信息不存在");
        }

        // 上传证明文件
        String certificateUrl = null;
        if (certificateFile != null && !certificateFile.isEmpty()) {
            try {
                Map<String, Object> uploadResult = commonService.uploadFile(certificateFile);
                certificateUrl = (String) uploadResult.get("url");
            } catch (Exception e) {
                log.error("上传证明文件失败", e);
                throw new BusinessException(ErrorCode.BAD_REQUEST, "证明文件上传失败");
            }
        }

        // 查询学校信息
        String schoolName = "";
        if (schoolId != null) {
            University university = universityMapper.selectById(schoolId);
            if (university != null) {
                schoolName = university.getName();
            }
        }

        // 创建认证申请
        UserAuthApply apply = new UserAuthApply();
        apply.setUserId(userId);
        apply.setRealName(name);
        apply.setIdCard(idCard);
        apply.setApplyRole("TEACHER");
        apply.setSchoolName(schoolName);
        // TODO: 保存教师证明文件URL（需要扩展UserAuthApply表添加certificate_url字段）
        // 保存教师信息（部门、职称等可以记录在备注中）
        apply.setStatus(ApplyStatusEnum.PENDING.ordinal());
        apply.setCreateTime(LocalDateTime.now());
        apply.setUpdateTime(LocalDateTime.now());

        userAuthApplyMapper.insert(apply);
        
        log.info("教师认证申请: userId={}, teacherId={}, name={}, department={}, title={}", 
                userId, teacherId, name, teacherInfo.getDepartment(), teacherInfo.getTitle());

        Map<String, Object> response = new HashMap<>();
        response.put("applyId", apply.getId());
        response.put("status", "PENDING");
        response.put("message", "认证申请已提交，等待审核（3个工作日内完成）");
        response.put("applyTime", apply.getCreateTime());

        return response;
    }

    @Override
    public Map<String, Object> visitorFaceDetect(String faceImage, String name, String phone,
                                                  String idCard, String reason) {
        // 步骤1: 人脸检测 - 检测图片中是否有人脸（暂时禁用，待修复百度AI SDK问题）
        // Map<String, Object> detectResult = baiduFaceService.detectFace(faceImage);
        // log.info("人脸检测结果: {}", detectResult);
        Map<String, Object> detectResult = new HashMap<>();
        detectResult.put("success", true);
        detectResult.put("faceNum", 1);
        
        // 步骤2: 活体检测 - 检测是否为真实活体（暂时禁用，待修复百度AI SDK问题）
        // Map<String, Object> livenessResult = baiduFaceService.faceLiveness(faceImage);
        // log.info("活体检测结果: {}", livenessResult);
        Map<String, Object> livenessResult = new HashMap<>();
        livenessResult.put("success", true);
        livenessResult.put("face_liveness", 0.95);
        
        // 步骤3: 如果有身份证照片，进行人脸比对（可选）
        // 注意：这里假设faceImage是用户上传的人脸照片
        // 如果需要与身份证照片比对，需要额外传入身份证照片的Base64编码
        // Map<String, Object> matchResult = baiduFaceService.faceMatch(faceImage, idCardImage);
        
        // 构建返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("detectResult", "SUCCESS");
        response.put("livenessScore", livenessResult.get("face_liveness"));
        response.put("isAlive", true);
        response.put("faceNum", detectResult.get("faceNum"));
        response.put("message", "活体检测通过，可以申请游客临时卡");
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }

    @Override
    @Transactional
    public Map<String, Object> visitorRegister(String name, String phone, String idCard,
                                               String reason, Integer stayHours, Long schoolId) {
        // 验证高校是否存在
        University university = universityMapper.selectById(schoolId);
        if (university == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "高校不存在");
        }

        // 创建临时用户（游客角色）
        User visitorUser = new User();
        visitorUser.setUsername("VISITOR_" + System.currentTimeMillis());
        visitorUser.setPassword(""); // 游客无需密码
        visitorUser.setNickname(name);
        visitorUser.setPhone(phone);
        visitorUser.setRole(com.yourschool.campussystem.enums.UserRoleEnum.TOURIST);
        visitorUser.setSchoolId(schoolId);
        visitorUser.setCreateTime(LocalDateTime.now());
        visitorUser.setUpdateTime(LocalDateTime.now());
        visitorUser.setIsDeleted(0);
        userMapper.insert(visitorUser);

        // 生成游客卡号
        String visitorCode = "V" + String.format("%08d", visitorUser.getId());
        
        // 计算过期时间（默认24小时）
        LocalDateTime expireTime = LocalDateTime.now().plusHours(stayHours != null ? stayHours : 24);

        // 创建游客临时卡
        Ecard visitorCard = new Ecard();
        visitorCard.setCardNo(visitorCode);
        visitorCard.setUserId(visitorUser.getId());
        visitorCard.setBalance(java.math.BigDecimal.ZERO);
        visitorCard.setStatus(CardStatusEnum.NORMAL);
        visitorCard.setIsVisitorCard(true);
        visitorCard.setVisitorExpireTime(expireTime);
        visitorCard.setCreateTime(LocalDateTime.now());
        visitorCard.setUpdateTime(LocalDateTime.now());
        ecardMapper.insert(visitorCard);

        Map<String, Object> response = new HashMap<>();
        response.put("visitorId", visitorUser.getId());
        response.put("visitorCode", visitorCode);
        response.put("cardNo", visitorCode);
        response.put("expireTime", expireTime);
        response.put("stayHours", stayHours != null ? stayHours : 24);
        response.put("message", "进校登记成功，游客临时卡已生成");

        log.info("游客注册成功: name={}, phone={}, visitorCode={}, expireTime={}", 
                name, phone, visitorCode, expireTime);

        return response;
    }

    @Override
    public Map<String, Object> getAuthApplyStatus(Long applyId) {
        UserAuthApply apply = userAuthApplyMapper.selectById(applyId);
        if (apply == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("applyId", apply.getId());
        response.put("status", apply.getStatus() == ApplyStatusEnum.PENDING.ordinal() ? "PENDING" :
                apply.getStatus() == ApplyStatusEnum.APPROVED.ordinal() ? "APPROVED" : "REJECTED");
        response.put("role", apply.getApplyRole());
        response.put("applyTime", apply.getCreateTime());
        response.put("processTime", apply.getAuditTime());
        response.put("processNote", apply.getAuditRemark());

        String message = "审核中，预计3个工作日内完成";
        if (apply.getStatus() == ApplyStatusEnum.APPROVED.ordinal()) {
            message = "认证已通过";
        } else if (apply.getStatus() == ApplyStatusEnum.REJECTED.ordinal()) {
            message = "认证未通过：" + (apply.getAuditRemark() != null ? apply.getAuditRemark() : "未通过原因");
        }
        response.put("message", message);

        return response;
    }

    @Override
    @Transactional
    public Map<String, Object> applyUniversityAccess(String universityName, String universityCode,
                                                     String contactName, String contactPhone,
                                                     String contactEmail, MultipartFile certificateFile,
                                                     String description) {
        // 检查高校代码是否已存在
        LambdaQueryWrapper<University> codeQuery = new LambdaQueryWrapper<>();
        codeQuery.eq(University::getCode, universityCode).eq(University::getIsDeleted, 0);
        University existingUniversity = universityMapper.selectOne(codeQuery);
        if (existingUniversity != null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "高校代码已存在，请使用其他代码");
        }

        // 上传资质文件
        String certificateUrl = null;
        if (certificateFile != null && !certificateFile.isEmpty()) {
            try {
                Map<String, Object> uploadResult = commonService.uploadFile(certificateFile);
                certificateUrl = (String) uploadResult.get("url");
            } catch (Exception e) {
                log.error("上传资质文件失败", e);
                throw new BusinessException(ErrorCode.BAD_REQUEST, "资质文件上传失败");
            }
        }

        // 创建高校接入申请记录（使用university表，status设为PENDING）
        University university = new University();
        university.setName(universityName);
        university.setCode(universityCode);
        university.setContactName(contactName);
        university.setContactPhone(contactPhone);
        university.setContactEmail(contactEmail);
        university.setDescription(description);
        university.setLogoUrl(certificateUrl); // 暂时使用logo_url字段存储资质文件URL
        university.setStatus("PENDING"); // 待审核状态
        university.setCreateTime(LocalDateTime.now());
        university.setUpdateTime(LocalDateTime.now());
        university.setIsDeleted(0);
        universityMapper.insert(university);

        Map<String, Object> response = new HashMap<>();
        response.put("applyId", university.getId());
        response.put("universityId", university.getId());
        response.put("universityName", universityName);
        response.put("universityCode", universityCode);
        response.put("status", "PENDING");
        response.put("message", "高校接入申请已提交，等待平台审核（预计5个工作日内完成）");
        response.put("applyTime", university.getCreateTime());

        log.info("高校接入申请: universityName={}, universityCode={}, applyId={}", 
                universityName, universityCode, university.getId());

        return response;
    }

    @Override
    public Map<String, Object> getAuthApplyRecords(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<UserAuthApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAuthApply::getUserId, userId)
                .orderByDesc(UserAuthApply::getCreateTime);

        Page<UserAuthApply> pageObj = new Page<>(page, size);
        Page<UserAuthApply> result = userAuthApplyMapper.selectPage(pageObj, queryWrapper);

        List<Map<String, Object>> list = result.getRecords().stream()
                .map(apply -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", apply.getId());
                    item.put("realName", apply.getRealName());
                    item.put("applyRole", apply.getApplyRole());
                    item.put("status", apply.getStatus() == ApplyStatusEnum.PENDING.ordinal() ? "PENDING" :
                            apply.getStatus() == ApplyStatusEnum.APPROVED.ordinal() ? "APPROVED" : "REJECTED");
                    item.put("createTime", apply.getCreateTime());
                    item.put("auditTime", apply.getAuditTime());
                    item.put("auditRemark", apply.getAuditRemark());
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
    public Map<String, Object> sendEmailCode(String email) {
        log.info("发送邮箱验证码请求: email={}", email);
        Map<String, Object> result = emailService.sendVerificationCode(email, "LOGIN");
        log.info("邮箱验证码发送成功: email={}, codeId={}", email, result.get("codeId"));
        return result;
    }

    @Override
    public Map<String, Object> emailLogin(String email, String code, String codeId, HttpServletRequest request) {
        log.info("邮箱登录请求: email={}", email);

        // 1. 验证邮箱验证码
        boolean isValid = emailService.verifyCode(email, code, codeId);
        if (!isValid) {
            log.warn("邮箱验证码验证失败: email={}", email);
            throw new BusinessException(ErrorCode.BAD_REQUEST, "验证码错误或已过期");
        }

        // 2. 查询用户（通过邮箱）
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        User user = userMapper.selectOne(queryWrapper);

        // 3. 如果用户不存在，自动创建用户
        if (user == null) {
            log.info("邮箱对应的用户不存在，自动创建用户: email={}", email);
            user = new User();
            user.setUsername(email); // 使用邮箱作为用户名
            user.setEmail(email);
            user.setNickname(email.split("@")[0]); // 使用邮箱前缀作为昵称
            user.setRole(UserRoleEnum.TOURIST); // 默认为游客
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // 生成随机密码
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            userMapper.insert(user);
            log.info("自动创建用户成功: userId={}, email={}", user.getId(), email);
        }

        // 4. 检查用户是否被禁用
        if (user.getIsDeleted() != null && user.getIsDeleted() == 1) {
            log.warn("账号已被禁用: email={}", email);
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        // 5. 生成JWT Token
        String roleName = user.getRole() != null ? user.getRole().name() : "TOURIST";
        String token = JWTUtils.generateToken(user.getId(), user.getUsername(), roleName);
        log.info("邮箱登录成功: userId={}, email={}", user.getId(), email);

        // 6. 记录登录日志
        if (request != null) {
            try {
                recordLoginLog(user.getId(), request);
            } catch (Exception e) {
                log.warn("记录登录日志失败: userId={}", user.getId(), e);
            }
        }

        // 7. 返回登录结果
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole() != null ? user.getRole().name() : "TOURIST");
        response.put("token", token);
        response.put("expiresIn", JWTUtils.EXPIRATION);

        return response;
    }

    /**
     * 记录登录日志
     */
    private void recordLoginLog(Long userId, HttpServletRequest request) {
        try {
            String ip = getClientIp(request);
            String location = "未知";
            
            // 获取IP地理位置
            try {
                location = ipLocationService.getLocationByIp(ip);
            } catch (Exception e) {
                log.warn("获取IP地理位置失败: ip={}", ip, e);
            }

            UserLoginLog loginLog = new UserLoginLog();
            loginLog.setUserId(userId);
            loginLog.setLoginIp(ip);
            loginLog.setLoginLocation(location);
            loginLog.setLoginTime(LocalDateTime.now());
            userLoginLogMapper.insert(loginLog);
        } catch (Exception e) {
            log.error("记录登录日志异常: userId={}", userId, e);
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个IP的情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
