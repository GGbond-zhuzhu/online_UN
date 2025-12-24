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
import com.yourschool.campussystem.service.BaiduFaceService;  // 引入百度AI云人脸识别服务接口，用于调用真实的人脸检测与活体检测
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
    private final BaiduFaceService baiduFaceService;  // 注入百度AI云人脸识别服务，用于执行真实的人脸检测和活体检测
    private final EmailService emailService;
    private final UserLoginLogMapper userLoginLogMapper;
    private final IpLocationService ipLocationService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> studentFaceDetect(String faceImage, String studentId, String name,
                                                  String idCard, Long schoolId) {
        // 处理前端可能传入的 data:image/jpeg;base64, 前缀，只保留纯Base64数据
        String pureBase64 = faceImage; // 默认使用原始字符串
        if (pureBase64 != null && pureBase64.contains(",")) { // 如果包含逗号，说明可能带有前缀
            pureBase64 = pureBase64.substring(pureBase64.indexOf(",") + 1); // 截取逗号之后的部分作为纯Base64
        }

        // 步骤1：调用百度AI云人脸检测接口，确认图片中是否存在人脸
        Map<String, Object> detectResult = baiduFaceService.detectFace(pureBase64); // 调用人脸检测服务
        log.info("学生身份认证-百度AI云人脸检测结果: studentId={}, detectResult={}", studentId, detectResult); // 打印检测结果日志

        // 步骤2：调用百度AI云在线活体检测接口，确保为真实活体而非照片/视频
        Map<String, Object> livenessResult = baiduFaceService.faceLiveness(pureBase64); // 调用活体检测服务
        log.info("学生身份认证-百度AI云活体检测结果: studentId={}, livenessResult={}", studentId, livenessResult); // 打印活体检测结果日志

        // 从检测结果中提取人脸数量和活体分数等关键信息
        int faceNum = 0; // 默认人脸数量为0
        Object faceNumObj = detectResult.get("faceNum"); // 从返回Map中读取faceNum字段
        if (faceNumObj instanceof Number) { // 如果该字段为数字类型
            faceNum = ((Number) faceNumObj).intValue(); // 转换为int类型
        }

        double livenessScore = 0.0; // 默认活体分数为0
        Object scoreObj = livenessResult.get("livenessScore"); // 从返回Map中读取livenessScore字段
        if (scoreObj instanceof Number) { // 如果为数字类型
            livenessScore = ((Number) scoreObj).doubleValue(); // 转换为double类型
        }

        boolean isAlive = Boolean.TRUE.equals(livenessResult.get("isAlive")); // 根据返回的isAlive字段判断是否为活体

        // 构建统一的响应结果，返回给前端页面
        Map<String, Object> response = new HashMap<>(); // 创建返回结果Map
        response.put("detectResult", "SUCCESS"); // 标记检测流程执行成功
        response.put("livenessScore", livenessScore); // 返回活体检测分数
        response.put("isAlive", isAlive); // 返回是否为真实活体
        response.put("faceNum", faceNum); // 返回检测到的人脸数量
        response.put("message", isAlive && faceNum == 1 ? "人脸识别和活体检测通过" : 
                faceNum == 0 ? "未检测到人脸，请确保照片清晰且人脸完整" :
                faceNum > 1 ? "检测到多张人脸，请确保照片中只有您本人" :
                "活体检测未通过，请使用真人照片"); // 根据结果给出提示文案
        response.put("timestamp", LocalDateTime.now()); // 返回当前时间戳，便于前端展示

        return response; // 将检测结果返回给调用方（Controller）
    }

    @Override
    @Transactional
    public Map<String, Object> applyStudentAuth(Long userId, String studentId, String verificationCode,
                                                String name, String idCard, Long schoolId, String faceImage) {
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
        // 处理前端可能传入的 data:image/jpeg;base64, 前缀，只保留纯Base64数据
        String pureBase64 = faceImage; // 默认使用原始字符串
        if (pureBase64 != null && pureBase64.contains(",")) { // 如果包含逗号，说明可能带有前缀
            pureBase64 = pureBase64.substring(pureBase64.indexOf(",") + 1); // 截取逗号之后的部分作为纯Base64
        }

        // 步骤1：调用百度AI云人脸检测接口，确认图片中是否存在人脸
        Map<String, Object> detectResult = baiduFaceService.detectFace(pureBase64); // 调用人脸检测服务
        log.info("百度AI云人脸检测结果: {}", detectResult); // 打印检测结果日志，便于后续排查问题

        // 步骤2：调用百度AI云在线活体检测接口，确保为真实活体而非照片/视频
        Map<String, Object> livenessResult = baiduFaceService.faceLiveness(pureBase64); // 调用活体检测服务
        log.info("百度AI云活体检测结果: {}", livenessResult); // 打印活体检测结果日志

        // 从检测结果中提取人脸数量和活体分数等关键信息
        int faceNum = 0; // 默认人脸数量为0
        Object faceNumObj = detectResult.get("faceNum"); // 从返回Map中读取faceNum字段
        if (faceNumObj instanceof Number) { // 如果该字段为数字类型
            faceNum = ((Number) faceNumObj).intValue(); // 转换为int类型
        }

        double livenessScore = 0.0; // 默认活体分数为0
        Object scoreObj = livenessResult.get("livenessScore"); // 从返回Map中读取livenessScore字段
        if (scoreObj instanceof Number) { // 如果为数字类型
            livenessScore = ((Number) scoreObj).doubleValue(); // 转换为double类型
        }

        boolean isAlive = Boolean.TRUE.equals(livenessResult.get("isAlive")); // 根据返回的isAlive字段判断是否为活体

        // 构建统一的响应结果，返回给前端页面
        Map<String, Object> response = new HashMap<>(); // 创建返回结果Map
        response.put("detectResult", "SUCCESS"); // 标记检测流程执行成功
        response.put("livenessScore", livenessScore); // 返回活体检测分数
        response.put("isAlive", isAlive); // 返回是否为真实活体
        response.put("faceNum", faceNum); // 返回检测到的人脸数量
        response.put("message", isAlive ? "活体检测通过，可以申请游客临时卡" : "活体检测未通过，请重新拍摄"); // 根据结果给出提示文案
        response.put("timestamp", LocalDateTime.now()); // 返回当前时间戳，便于前端展示

        return response; // 将检测结果返回给调用方（Controller）
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

    @Override
    public Map<String, Object> sendResetPasswordEmailCode(String email) {
        log.info("发送重置密码邮箱验证码请求: email={}", email); // 记录发送重置密码验证码的请求日志
        // 这里直接复用EmailService，指定验证码类型为RESET_PASSWORD，便于后续区分用途
        Map<String, Object> result = emailService.sendVerificationCode(email, "RESET_PASSWORD"); // 发送重置密码用验证码
        log.info("重置密码验证码发送成功: email={}, codeId={}", email, result.get("codeId")); // 打印成功日志，包含验证码ID
        return result; // 将包含codeId和expireTime等信息的Map返回给Controller
    }

    @Override
    @Transactional
    public Map<String, Object> resetPasswordByEmail(String email, String code, String codeId, String newPassword) {
        log.info("邮箱重置密码请求: email={}", email); // 记录重置密码请求日志

        // 1. 校验邮箱验证码是否正确且未过期（校验成功后会自动标记为已使用）
        boolean isValid = emailService.verifyCode(email, code, codeId); // 调用EmailService进行验证码校验
        if (!isValid) { // 如果校验失败
            log.warn("重置密码验证码验证失败: email={}", email); // 打印警告日志
            throw new BusinessException(ErrorCode.BAD_REQUEST, "验证码错误或已过期"); // 抛出业务异常提示前端
        }

        // 2. 根据邮箱查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>(); // 创建查询条件包装器
        queryWrapper.eq(User::getEmail, email); // 条件：email字段等于传入邮箱
        User user = userMapper.selectOne(queryWrapper); // 从数据库中查询用户

        if (user == null) { // 如果未查询到用户
            log.warn("重置密码失败，邮箱未注册: email={}", email); // 打印警告日志
            throw new BusinessException(ErrorCode.USER_NOT_EXIST, "该邮箱尚未注册账号"); // 抛出“用户不存在”业务异常
        }

        // 3. 加密新密码并更新到数据库
        try {
            String encodedPassword = passwordEncoder.encode(newPassword); // 使用BCrypt对新密码进行加密
            user.setPassword(encodedPassword); // 设置新的加密密码
            user.setUpdateTime(LocalDateTime.now()); // 更新用户信息的更新时间
            userMapper.updateById(user); // 将修改后的用户信息写回数据库
            log.info("用户密码重置成功: userId={}, email={}", user.getId(), email); // 打印成功日志
        } catch (Exception e) { // 捕获加密或数据库更新过程中可能出现的异常
            log.error("重置密码时发生异常: email={}", email, e); // 打印错误日志和堆栈
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "重置密码失败，请稍后重试"); // 抛出统一的服务器错误提示
        }

        // 4. 构造返回结果，前端无需拿到过多信息，仅做确认提示即可
        Map<String, Object> response = new HashMap<>(); // 创建返回结果Map
        response.put("userId", user.getId()); // 返回用户ID，便于前端调试或后续扩展
        response.put("email", user.getEmail()); // 返回邮箱地址
        response.put("message", "密码重置成功"); // 提示信息

        return response; // 返回结果Map
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
