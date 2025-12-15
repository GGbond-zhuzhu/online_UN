package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.entity.*;
import com.yourschool.campussystem.enums.ApplyStatusEnum;
import com.yourschool.campussystem.enums.GoodsStatusEnum;
import com.yourschool.campussystem.enums.ParttimeStatusEnum;
import com.yourschool.campussystem.enums.UserRoleEnum;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.*;
import com.yourschool.campussystem.service.CommonService;
import com.yourschool.campussystem.service.UniversityService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourschool.campussystem.dto.StudentInfoExcelDTO;
import com.yourschool.campussystem.dto.TeacherInfoExcelDTO;
import com.yourschool.campussystem.dto.UserExcelDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 高校管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UniversityServiceImpl implements UniversityService {

    private final UniversityMapper universityMapper;
    private final UniversityConfigMapper universityConfigMapper;
    private final NotificationMapper notificationMapper;
    private final UserLoginLogMapper userLoginLogMapper;
    private final UserMapper userMapper;
    private final UserAuthApplyMapper userAuthApplyMapper;
    private final StudentInfoMapper studentInfoMapper;
    private final TeacherInfoMapper teacherInfoMapper;
    private final SecondhandGoodsMapper secondhandGoodsMapper;
    private final ParttimeMapper parttimeMapper;
    private final CommonService commonService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ==================== 高校信息管理 ====================

    @Override
    public Map<String, Object> getUniversityInfo(Long universityId) {
        University university = universityMapper.selectById(universityId);
        if (university == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        // 统计用户数
        LambdaQueryWrapper<User> userQuery = new LambdaQueryWrapper<>();
        userQuery.eq(User::getSchoolId, universityId).eq(User::getIsDeleted, 0);
        long totalUsers = userMapper.selectCount(userQuery);

        userQuery.eq(User::getRole, UserRoleEnum.STUDENT);
        long totalStudents = userMapper.selectCount(userQuery);

        userQuery.clear();
        userQuery.eq(User::getSchoolId, universityId)
                .eq(User::getIsDeleted, 0)
                .eq(User::getRole, UserRoleEnum.TEACHER);
        long totalTeachers = userMapper.selectCount(userQuery);

        Map<String, Object> result = new HashMap<>();
        result.put("universityId", university.getId());
        result.put("universityName", university.getName());
        result.put("universityCode", university.getCode());
        result.put("status", university.getStatus());
        result.put("accessTime", university.getCreateTime());
        result.put("totalStudents", totalStudents);
        result.put("totalTeachers", totalTeachers);
        result.put("totalUsers", totalUsers);
        result.put("logoUrl", university.getLogoUrl());
        result.put("description", university.getDescription());
        result.put("contactName", university.getContactName());
        result.put("contactPhone", university.getContactPhone());
        result.put("contactEmail", university.getContactEmail());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> updateUniversityInfo(Long universityId, String universityName,
                                                     String contactName, String contactPhone,
                                                     String contactEmail, String description) {
        University university = universityMapper.selectById(universityId);
        if (university == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        if (StringUtils.hasText(universityName)) {
            university.setName(universityName);
        }
        if (StringUtils.hasText(contactName)) {
            university.setContactName(contactName);
        }
        if (StringUtils.hasText(contactPhone)) {
            university.setContactPhone(contactPhone);
        }
        if (StringUtils.hasText(contactEmail)) {
            university.setContactEmail(contactEmail);
        }
        if (StringUtils.hasText(description)) {
            university.setDescription(description);
        }

        universityMapper.updateById(university);

        Map<String, Object> result = new HashMap<>();
        result.put("universityId", university.getId());
        result.put("universityName", university.getName());
        result.put("message", "高校信息更新成功");
        result.put("updateTime", LocalDateTime.now());

        return result;
    }

    // ==================== 用户管理 ====================

    @Override
    public Map<String, Object> getUniversityUsers(Long universityId, String role, String authStatus,
                                                   String keyword, Integer page, Integer size) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getSchoolId, universityId)
                .eq(User::getIsDeleted, 0);

        // 角色筛选
        if (StringUtils.hasText(role)) {
            try {
                UserRoleEnum roleEnum = UserRoleEnum.getByName(role);
                queryWrapper.eq(User::getRole, roleEnum);
            } catch (Exception e) {
                log.warn("无效的角色筛选: {}", role);
            }
        }

        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword)
            );
        }

        queryWrapper.orderByDesc(User::getCreateTime);

        Page<User> pageObj = new Page<>(page, size);
        Page<User> result = userMapper.selectPage(pageObj, queryWrapper);

        List<Map<String, Object>> list = result.getRecords().stream()
                .map(user -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", user.getId());
                    item.put("username", user.getUsername());
                    item.put("nickname", user.getNickname());
                    item.put("role", user.getRole() != null ? user.getRole().getCode() : null);
                    item.put("email", user.getEmail());
                    item.put("phone", user.getPhone());
                    item.put("createTime", user.getCreateTime());
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
    @Transactional
    public void reviewUserAuthApply(Long universityId, Long applyId, String result, String note, Long reviewerId) {
        UserAuthApply apply = userAuthApplyMapper.selectById(applyId);
        if (apply == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        // 验证申请是否属于该高校
        User user = userMapper.selectById(apply.getUserId());
        if (user == null || !universityId.equals(user.getSchoolId())) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        ApplyStatusEnum statusEnum;
        if ("APPROVED".equals(result)) {
            statusEnum = ApplyStatusEnum.APPROVED;
            // 更新用户角色
            if (user != null) {
                try {
                    UserRoleEnum role = UserRoleEnum.getByName(apply.getApplyRole());
                    user.setRole(role);
                    userMapper.updateById(user);
                } catch (Exception e) {
                    log.error("更新用户角色失败", e);
                }
            }
        } else if ("REJECTED".equals(result)) {
            statusEnum = ApplyStatusEnum.REJECTED;
        } else {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        apply.setStatus(statusEnum.ordinal());
        apply.setAuditRemark(note);
        apply.setAuditTime(LocalDateTime.now());
        apply.setAuditorId(reviewerId);

        userAuthApplyMapper.updateById(apply);
    }

    @Override
    @Transactional
    public Map<String, Object> importUsers(Long universityId, MultipartFile file, String userType) {
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件格式不正确，请上传Excel文件（.xlsx或.xls）");
        }

        // 验证高校是否存在
        University university = universityMapper.selectById(universityId);
        if (university == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "高校不存在");
        }

        // 使用EasyExcel读取Excel文件
        List<UserExcelDTO> excelDataList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        try {
            EasyExcel.read(file.getInputStream(), UserExcelDTO.class, new UserExcelListener(excelDataList, errorMessages))
                    .sheet()
                    .headRowNumber(1) // 跳过表头
                    .doRead();
        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Excel文件读取失败：" + e.getMessage());
        }

        // 转换并保存数据
        int importedCount = 0;
        int failedCount = 0;
        LocalDateTime now = LocalDateTime.now();

        for (UserExcelDTO excelData : excelDataList) {
            try {
                // 跳过空行
                if (excelData.getUsername() == null || excelData.getUsername().trim().isEmpty()) {
                    continue;
                }

                String username = excelData.getUsername().trim();

                // 检查用户名是否已存在
                LambdaQueryWrapper<User> userQuery = new LambdaQueryWrapper<>();
                userQuery.eq(User::getUsername, username);
                Long existingUserCount = userMapper.selectCount(userQuery);
                if (existingUserCount > 0) {
                    errorMessages.add("用户《" + username + "》已存在，跳过导入");
                    failedCount++;
                    continue;
                }

                // 创建用户
                User user = new User();
                user.setUsername(username);

                // 密码处理：如果Excel中没有密码，使用默认密码（123456）
                String password = excelData.getPassword();
                if (password == null || password.trim().isEmpty()) {
                    password = "123456"; // 默认密码
                }
                user.setPassword(passwordEncoder.encode(password.trim()));

                // 昵称
                user.setNickname(excelData.getNickname() != null ? excelData.getNickname().trim() : username);

                // 角色处理
                UserRoleEnum role = parseUserRole(excelData.getRole(), userType);
                user.setRole(role);

                // 邮箱和手机号
                user.setEmail(excelData.getEmail() != null ? excelData.getEmail().trim() : null);
                user.setPhone(excelData.getPhone() != null ? excelData.getPhone().trim() : null);

                // 学校ID
                if (excelData.getSchoolId() != null && !excelData.getSchoolId().trim().isEmpty()) {
                    try {
                        Long schoolId = Long.parseLong(excelData.getSchoolId().trim());
                        // 验证学校ID是否匹配
                        if (!schoolId.equals(universityId)) {
                            errorMessages.add("用户《" + username + "》的学校ID不匹配，跳过导入");
                            failedCount++;
                            continue;
                        }
                        user.setSchoolId(schoolId);
                    } catch (NumberFormatException e) {
                        errorMessages.add("用户《" + username + "》的学校ID格式不正确，跳过导入");
                        failedCount++;
                        continue;
                    }
                } else {
                    // 如果没有指定学校ID，使用当前高校ID
                    user.setSchoolId(universityId);
                }

                user.setCreateTime(now);
                user.setUpdateTime(now);
                user.setIsDeleted(0);

                userMapper.insert(user);
                importedCount++;
            } catch (Exception e) {
                log.error("导入用户失败：{}", excelData.getUsername(), e);
                errorMessages.add("用户《" + excelData.getUsername() + "》导入失败：" + e.getMessage());
                failedCount++;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("importedCount", importedCount);
        response.put("failedCount", failedCount);
        response.put("userType", userType);
        response.put("errorMessages", errorMessages);
        response.put("message", String.format("用户导入完成，成功导入%d条，失败%d条", importedCount, failedCount));
        response.put("importTime", now);

        log.info("用户导入完成: universityId={}, userType={}, importedCount={}, failedCount={}", 
                universityId, userType, importedCount, failedCount);

        return response;
    }

    /**
     * 解析用户角色
     */
    private UserRoleEnum parseUserRole(String roleStr, String userType) {
        // 如果Excel中指定了角色，优先使用Excel中的角色
        if (roleStr != null && !roleStr.trim().isEmpty()) {
            try {
                return UserRoleEnum.getByName(roleStr.trim().toUpperCase());
            } catch (Exception e) {
                log.warn("无法解析角色: {}", roleStr);
            }
        }

        // 如果Excel中没有指定角色，使用userType参数
        if (userType != null && !userType.trim().isEmpty()) {
            try {
                return UserRoleEnum.getByName(userType.trim().toUpperCase());
            } catch (Exception e) {
                log.warn("无法解析用户类型: {}", userType);
            }
        }

        // 默认返回STUDENT
        return UserRoleEnum.STUDENT;
    }

    /**
     * Excel读取监听器
     */
    private static class UserExcelListener extends AnalysisEventListener<UserExcelDTO> {
        private final List<UserExcelDTO> cachedDataList;
        @SuppressWarnings("unused")
        private final List<String> errorMessages; // 保留用于未来扩展

        public UserExcelListener(List<UserExcelDTO> cachedDataList, List<String> errorMessages) {
            this.cachedDataList = cachedDataList;
            this.errorMessages = errorMessages;
        }

        @Override
        public void invoke(UserExcelDTO data, AnalysisContext context) {
            cachedDataList.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 读取完成后的处理
        }
    }

    @Override
    public Resource exportUsers(Long universityId, String role) {
        // 查询用户数据
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getSchoolId, universityId)
                .eq(User::getIsDeleted, 0)
                .orderByDesc(User::getCreateTime);

        // 角色筛选
        if (StringUtils.hasText(role)) {
            try {
                UserRoleEnum roleEnum = UserRoleEnum.getByName(role);
                queryWrapper.eq(User::getRole, roleEnum);
            } catch (Exception e) {
                log.warn("无效的角色筛选: {}", role);
            }
        }

        List<User> users = userMapper.selectList(queryWrapper);

        // 转换为Excel DTO
        List<UserExcelDTO> excelDataList = users.stream()
                .map(this::convertToExcelDTO)
                .collect(Collectors.toList());

        // 使用EasyExcel生成Excel文件
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            EasyExcel.write(outputStream, UserExcelDTO.class)
                    .sheet("用户列表")
                    .doWrite(excelDataList);

            byte[] bytes = outputStream.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);
            
            log.info("用户导出完成: universityId={}, role={}, count={}", universityId, role, excelDataList.size());
            
            return resource;
        } catch (IOException e) {
            log.error("生成Excel文件失败", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Excel文件生成失败：" + e.getMessage());
        }
    }

    /**
     * 将User转换为Excel DTO
     */
    private UserExcelDTO convertToExcelDTO(User user) {
        UserExcelDTO dto = new UserExcelDTO();
        dto.setUsername(user.getUsername());
        dto.setPassword(""); // 不导出密码
        dto.setNickname(user.getNickname());
        dto.setRole(user.getRole() != null ? user.getRole().getDescription() : "");
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setSchoolId(user.getSchoolId() != null ? user.getSchoolId().toString() : "");
        return dto;
    }

    // ==================== 功能配置 ====================

    @Override
    public Map<String, Object> getUniversityConfig(Long universityId) {
        // 从数据库读取功能配置
        LambdaQueryWrapper<UniversityConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UniversityConfig::getUniversityId, universityId);
        List<UniversityConfig> configs = universityConfigMapper.selectList(queryWrapper);

        // 默认配置
        Map<String, Object> config = new HashMap<>();
        config.put("enableEcard", true);
        config.put("enableSecondhand", true);
        config.put("enableParttime", true);
        config.put("enableSchedule", true);
        config.put("enableFacePay", true);
        config.put("enableDynamicCode", true);
        config.put("customModules", new ArrayList<>());

        // 从数据库读取配置并覆盖默认值
        for (UniversityConfig cfg : configs) {
            try {
                String configKey = cfg.getConfigKey();
                String configValue = cfg.getConfigValue();
                
                // 尝试解析JSON值
                if (configValue != null && (configValue.startsWith("{") || configValue.startsWith("["))) {
                    Object value = objectMapper.readValue(configValue, Object.class);
                    config.put(configKey, value);
                } else {
                    // 布尔值或字符串
                    if ("true".equalsIgnoreCase(configValue) || "false".equalsIgnoreCase(configValue)) {
                        config.put(configKey, Boolean.parseBoolean(configValue));
                    } else {
                        config.put(configKey, configValue);
                    }
                }
            } catch (Exception e) {
                log.warn("解析配置值失败: configKey={}, configValue={}", cfg.getConfigKey(), cfg.getConfigValue(), e);
            }
        }

        return config;
    }

    @Override
    @Transactional
    public void updateUniversityConfig(Long universityId, Map<String, Object> config) {
        // 保存功能配置到数据库
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String configKey = entry.getKey();
            Object configValue = entry.getValue();

            // 查询是否已存在
            LambdaQueryWrapper<UniversityConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UniversityConfig::getUniversityId, universityId)
                    .eq(UniversityConfig::getConfigKey, configKey);
            UniversityConfig existingConfig = universityConfigMapper.selectOne(queryWrapper);

            String configValueStr;
            try {
                // 将配置值转换为JSON字符串
                if (configValue instanceof String) {
                    configValueStr = (String) configValue;
                } else {
                    configValueStr = objectMapper.writeValueAsString(configValue);
                }
            } catch (Exception e) {
                log.error("序列化配置值失败: configKey={}, configValue={}", configKey, configValue, e);
                configValueStr = String.valueOf(configValue);
            }

            if (existingConfig != null) {
                // 更新现有配置
                existingConfig.setConfigValue(configValueStr);
                existingConfig.setUpdateTime(LocalDateTime.now());
                universityConfigMapper.updateById(existingConfig);
            } else {
                // 创建新配置
                UniversityConfig newConfig = new UniversityConfig();
                newConfig.setUniversityId(universityId);
                newConfig.setConfigKey(configKey);
                newConfig.setConfigValue(configValueStr);
                newConfig.setDescription("高校功能配置");
                newConfig.setCreateTime(LocalDateTime.now());
                newConfig.setUpdateTime(LocalDateTime.now());
                universityConfigMapper.insert(newConfig);
            }
        }

        log.info("更新高校功能配置: universityId={}, config={}", universityId, config);
    }

    // ==================== 数据统计 ====================

    @Override
    public Map<String, Object> getUniversityStatistics(Long universityId, String timeRange) {
        // 用户统计
        LambdaQueryWrapper<User> userQuery = new LambdaQueryWrapper<>();
        userQuery.eq(User::getSchoolId, universityId).eq(User::getIsDeleted, 0);
        long totalUsers = userMapper.selectCount(userQuery);

        userQuery.eq(User::getRole, UserRoleEnum.STUDENT);
        long totalStudents = userMapper.selectCount(userQuery);

        userQuery.clear();
        userQuery.eq(User::getSchoolId, universityId)
                .eq(User::getIsDeleted, 0)
                .eq(User::getRole, UserRoleEnum.TEACHER);
        long totalTeachers = userMapper.selectCount(userQuery);

        userQuery.clear();
        userQuery.eq(User::getSchoolId, universityId)
                .eq(User::getIsDeleted, 0)
                .eq(User::getRole, UserRoleEnum.TOURIST);
        long totalVisitors = userMapper.selectCount(userQuery);

        // 内容统计
        LambdaQueryWrapper<SecondhandGoods> goodsQuery = new LambdaQueryWrapper<>();
        goodsQuery.eq(SecondhandGoods::getSchoolId, universityId)
                .eq(SecondhandGoods::getIsDeleted, 0);
        long totalSecondhandGoods = secondhandGoodsMapper.selectCount(goodsQuery);

        LambdaQueryWrapper<Parttime> parttimeQuery = new LambdaQueryWrapper<>();
        parttimeQuery.eq(Parttime::getSchoolId, universityId)
                .eq(Parttime::getIsDeleted, 0);
        long totalParttimeJobs = parttimeMapper.selectCount(parttimeQuery);

        // 简化处理：今日活跃用户
        long activeUsers = totalUsers / 10;
        long todayActiveUsers = activeUsers / 10;
        long todayNewUsers = totalUsers / 1000;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalStudents", totalStudents);
        stats.put("totalTeachers", totalTeachers);
        stats.put("totalVisitors", totalVisitors);
        stats.put("activeUsers", activeUsers);
        stats.put("totalSecondhandGoods", totalSecondhandGoods);
        stats.put("totalParttimeJobs", totalParttimeJobs);
        stats.put("todayActiveUsers", todayActiveUsers);
        stats.put("todayNewUsers", todayNewUsers);

        return stats;
    }

    @Override
    public Map<String, Object> getUserActivityStatistics(Long universityId, String startDate, String endDate) {
        // 查询该高校的所有用户ID
        LambdaQueryWrapper<User> userQuery = new LambdaQueryWrapper<>();
        userQuery.eq(User::getSchoolId, universityId).eq(User::getIsDeleted, 0)
                .select(User::getId);
        List<User> users = userMapper.selectList(userQuery);
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());

        if (userIds.isEmpty()) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("dailyActiveUsers", new ArrayList<>());
            stats.put("averageDailyActive", 0);
            stats.put("peakDailyActive", 0);
            stats.put("totalLogins", 0L);
            return stats;
        }

        // 查询登录日志
        LambdaQueryWrapper<UserLoginLog> logQuery = new LambdaQueryWrapper<>();
        logQuery.in(UserLoginLog::getUserId, userIds);
        
        // 日期范围筛选
        if (startDate != null && !startDate.isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            logQuery.ge(UserLoginLog::getLoginTime, start);
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            logQuery.le(UserLoginLog::getLoginTime, end);
        }

        List<UserLoginLog> logs = userLoginLogMapper.selectList(logQuery);
        
        // 按日期统计每日活跃用户数
        Map<String, Set<Long>> dailyActiveMap = new HashMap<>();
        for (UserLoginLog log : logs) {
            String date = log.getLoginTime().toLocalDate().toString();
            dailyActiveMap.computeIfAbsent(date, k -> new HashSet<>()).add(log.getUserId());
        }

        List<Map<String, Object>> dailyActiveUsers = dailyActiveMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", entry.getKey());
                    item.put("activeUsers", entry.getValue().size());
                    return item;
                })
                .collect(Collectors.toList());

        // 计算平均值和峰值
        int averageDailyActive = dailyActiveUsers.isEmpty() ? 0 :
                (int) dailyActiveUsers.stream()
                        .mapToInt(item -> (Integer) item.get("activeUsers"))
                        .average()
                        .orElse(0.0);
        
        int peakDailyActive = dailyActiveUsers.isEmpty() ? 0 :
                dailyActiveUsers.stream()
                        .mapToInt(item -> (Integer) item.get("activeUsers"))
                        .max()
                        .orElse(0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("dailyActiveUsers", dailyActiveUsers);
        stats.put("averageDailyActive", averageDailyActive);
        stats.put("peakDailyActive", peakDailyActive);
        stats.put("totalLogins", (long) logs.size());

        return stats;
    }

    @Override
    public Map<String, Object> getContentStatistics(Long universityId) {
        LambdaQueryWrapper<SecondhandGoods> goodsQuery = new LambdaQueryWrapper<>();
        goodsQuery.eq(SecondhandGoods::getSchoolId, universityId)
                .eq(SecondhandGoods::getIsDeleted, 0);
        long totalGoods = secondhandGoodsMapper.selectCount(goodsQuery);

        goodsQuery.eq(SecondhandGoods::getStatus, GoodsStatusEnum.ON_SALE.name());
        long onSaleGoods = secondhandGoodsMapper.selectCount(goodsQuery);

        goodsQuery.clear();
        goodsQuery.eq(SecondhandGoods::getSchoolId, universityId)
                .eq(SecondhandGoods::getIsDeleted, 0)
                .eq(SecondhandGoods::getStatus, GoodsStatusEnum.SOLD_OUT.name());
        long soldGoods = secondhandGoodsMapper.selectCount(goodsQuery);

        LambdaQueryWrapper<Parttime> parttimeQuery = new LambdaQueryWrapper<>();
        parttimeQuery.eq(Parttime::getSchoolId, universityId)
                .eq(Parttime::getIsDeleted, 0);
        long totalJobs = parttimeMapper.selectCount(parttimeQuery);

        parttimeQuery.eq(Parttime::getStatus, ParttimeStatusEnum.RECRUITING.name());
        long recruitingJobs = parttimeMapper.selectCount(parttimeQuery);

        parttimeQuery.clear();
        parttimeQuery.eq(Parttime::getSchoolId, universityId)
                .eq(Parttime::getIsDeleted, 0)
                .eq(Parttime::getStatus, ParttimeStatusEnum.CLOSED.name());
        long closedJobs = parttimeMapper.selectCount(parttimeQuery);

        Map<String, Object> stats = new HashMap<>();
        stats.put("secondhandGoods", totalGoods);
        stats.put("onSaleGoods", onSaleGoods);
        stats.put("soldGoods", soldGoods);
        stats.put("parttimeJobs", totalJobs);
        stats.put("recruitingJobs", recruitingJobs);
        stats.put("closedJobs", closedJobs);

        return stats;
    }

    // ==================== 内容管理 ====================

    @Override
    @Transactional
    public void reviewContent(Long universityId, Long contentId, String contentType, String result, String note, Long reviewerId) {
        if ("SECONDHAND".equals(contentType)) {
            SecondhandGoods goods = secondhandGoodsMapper.selectById(contentId);
            if (goods == null || !universityId.equals(goods.getSchoolId())) {
                throw new BusinessException(ErrorCode.NOT_FOUND);
            }

            if ("APPROVED".equals(result)) {
                goods.setStatus(GoodsStatusEnum.ON_SALE);
            } else if ("REJECTED".equals(result)) {
                goods.setStatus(GoodsStatusEnum.REJECTED);
            }
            secondhandGoodsMapper.updateById(goods);

        } else if ("PARTTIME".equals(contentType)) {
            Parttime parttime = parttimeMapper.selectById(contentId);
            if (parttime == null || !universityId.equals(parttime.getSchoolId())) {
                throw new BusinessException(ErrorCode.NOT_FOUND);
            }

            if ("APPROVED".equals(result)) {
                parttime.setStatus(ParttimeStatusEnum.RECRUITING);
            } else if ("REJECTED".equals(result)) {
                parttime.setStatus(ParttimeStatusEnum.REJECTED);
            }
            parttimeMapper.updateById(parttime);
        } else {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
    }

    // ==================== 通知推送 ====================

    @Override
    @Transactional
    public Map<String, Object> sendNotification(Long universityId, String title, String content,
                                                 String targetType, Boolean isUrgent) {
        // 查询目标用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getSchoolId, universityId).eq(User::getIsDeleted, 0);

        if (!"ALL".equals(targetType)) {
            try {
                UserRoleEnum role = UserRoleEnum.getByName(targetType);
                queryWrapper.eq(User::getRole, role);
            } catch (Exception e) {
                log.warn("无效的目标类型: {}", targetType);
            }
        }

        List<User> targetUsers = userMapper.selectList(queryWrapper);
        long sentCount = targetUsers.size();

        // 为每个目标用户创建通知记录
        LocalDateTime now = LocalDateTime.now();
        for (User user : targetUsers) {
            Notification notification = new Notification();
            notification.setUniversityId(universityId);
            notification.setTitle(title);
            notification.setContent(content);
            notification.setTargetType(targetType);
            notification.setTargetUserId(user.getId());
            notification.setIsUrgent(isUrgent != null && isUrgent ? 1 : 0);
            notification.setIsRead(0);
            notification.setSendTime(now);
            notification.setCreateTime(now);
            notification.setUpdateTime(now);
            notificationMapper.insert(notification);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("notificationId", System.currentTimeMillis()); // 简化处理，实际可以使用第一个通知ID
        response.put("sentCount", sentCount);
        response.put("message", "通知发送成功");
        response.put("sendTime", now);

        log.info("发送通知: universityId={}, targetType={}, sentCount={}", universityId, targetType, sentCount);

        return response;
    }

    @Override
    public Map<String, Object> getNotificationHistory(Long universityId, Integer page, Integer size) {
        // 查询通知历史（按发送时间倒序）
        LambdaQueryWrapper<Notification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notification::getUniversityId, universityId)
                .orderByDesc(Notification::getSendTime);

        Page<Notification> pageObj = new Page<>(page, size);
        Page<Notification> result = notificationMapper.selectPage(pageObj, queryWrapper);

        // 统计已读和未读数量
        LambdaQueryWrapper<Notification> readQuery = new LambdaQueryWrapper<>();
        readQuery.eq(Notification::getUniversityId, universityId)
                .eq(Notification::getIsRead, 1);
        long readCount = notificationMapper.selectCount(readQuery);

        LambdaQueryWrapper<Notification> unreadQuery = new LambdaQueryWrapper<>();
        unreadQuery.eq(Notification::getUniversityId, universityId)
                .eq(Notification::getIsRead, 0);
        long unreadCount = notificationMapper.selectCount(unreadQuery);

        List<Map<String, Object>> list = result.getRecords().stream()
                .map(notification -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", notification.getId());
                    item.put("title", notification.getTitle());
                    item.put("content", notification.getContent());
                    item.put("targetType", notification.getTargetType());
                    item.put("isUrgent", notification.getIsUrgent());
                    item.put("isRead", notification.getIsRead());
                    item.put("readTime", notification.getReadTime());
                    item.put("sendTime", notification.getSendTime());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", result.getTotal());
        response.put("readCount", readCount);
        response.put("unreadCount", unreadCount);
        response.put("page", page);
        response.put("size", size);

        return response;
    }

    // ==================== 学生信息管理 ====================

    @Override
    public Map<String, Object> getStudentInfoList(Long universityId, String keyword, String grade, String major, Integer page, Integer size) {
        LambdaQueryWrapper<StudentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentInfo::getSchoolId, universityId)
                .eq(StudentInfo::getIsDeleted, 0);

        // 关键词搜索（学号、姓名）
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(StudentInfo::getStudentId, keyword)
                    .or()
                    .like(StudentInfo::getName, keyword)
            );
        }

        // 年级筛选
        if (StringUtils.hasText(grade)) {
            queryWrapper.eq(StudentInfo::getGrade, grade);
        }

        // 专业筛选
        if (StringUtils.hasText(major)) {
            queryWrapper.like(StudentInfo::getMajor, major);
        }

        queryWrapper.orderByDesc(StudentInfo::getCreateTime);

        Page<StudentInfo> pageObj = new Page<>(page, size);
        Page<StudentInfo> result = studentInfoMapper.selectPage(pageObj, queryWrapper);

        List<Map<String, Object>> list = result.getRecords().stream()
                .map(student -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", student.getId());
                    item.put("studentId", student.getStudentId());
                    item.put("name", student.getName());
                    item.put("idCard", student.getIdCard());
                    item.put("major", student.getMajor());
                    item.put("grade", student.getGrade());
                    item.put("className", student.getClassName());
                    item.put("status", student.getStatus());
                    item.put("createTime", student.getCreateTime());
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
    @Transactional
    public Map<String, Object> addStudentInfo(Long universityId, String studentId, String name, String idCard, String major, String grade, String className) {
        // 检查学号是否已存在
        LambdaQueryWrapper<StudentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentInfo::getSchoolId, universityId)
                .eq(StudentInfo::getStudentId, studentId)
                .eq(StudentInfo::getIsDeleted, 0);
        StudentInfo existing = studentInfoMapper.selectOne(queryWrapper);
        if (existing != null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "学号已存在");
        }

        StudentInfo studentInfo = new StudentInfo();
        studentInfo.setSchoolId(universityId);
        studentInfo.setStudentId(studentId);
        studentInfo.setName(name);
        studentInfo.setIdCard(idCard);
        studentInfo.setMajor(major);
        studentInfo.setGrade(grade);
        studentInfo.setClassName(className);
        studentInfo.setStatus("ACTIVE");
        studentInfo.setCreateTime(LocalDateTime.now());
        studentInfo.setUpdateTime(LocalDateTime.now());

        studentInfoMapper.insert(studentInfo);

        Map<String, Object> response = new HashMap<>();
        response.put("id", studentInfo.getId());
        response.put("studentId", studentId);
        response.put("name", name);
        response.put("message", "学生信息添加成功");

        return response;
    }

    @Override
    @Transactional
    public Map<String, Object> importStudentInfo(Long universityId, MultipartFile file) {
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件格式不正确，请上传Excel文件");
        }

        // 使用EasyExcel读取Excel文件
        List<StudentInfoExcelDTO> excelDataList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        try {
            EasyExcel.read(file.getInputStream(), StudentInfoExcelDTO.class, new StudentInfoExcelListener(excelDataList, errorMessages))
                    .sheet()
                    .headRowNumber(1)
                    .doRead();
        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Excel文件读取失败：" + e.getMessage());
        }

        // 转换并保存数据
        int importedCount = 0;
        int failedCount = 0;
        LocalDateTime now = LocalDateTime.now();

        for (StudentInfoExcelDTO excelData : excelDataList) {
            try {
                // 跳过空行
                if (excelData.getStudentId() == null || excelData.getStudentId().trim().isEmpty()) {
                    continue;
                }

                String studentId = excelData.getStudentId().trim();

                // 检查学号是否已存在
                LambdaQueryWrapper<StudentInfo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(StudentInfo::getSchoolId, universityId)
                        .eq(StudentInfo::getStudentId, studentId)
                        .eq(StudentInfo::getIsDeleted, 0);
                StudentInfo existing = studentInfoMapper.selectOne(queryWrapper);
                if (existing != null) {
                    errorMessages.add("学号《" + studentId + "》已存在，跳过导入");
                    failedCount++;
                    continue;
                }

                StudentInfo studentInfo = new StudentInfo();
                studentInfo.setSchoolId(universityId);
                studentInfo.setStudentId(studentId);
                studentInfo.setName(excelData.getName() != null ? excelData.getName().trim() : "");
                studentInfo.setIdCard(excelData.getIdCard() != null ? excelData.getIdCard().trim() : "");
                studentInfo.setMajor(excelData.getMajor() != null ? excelData.getMajor().trim() : null);
                studentInfo.setGrade(excelData.getGrade() != null ? excelData.getGrade().trim() : null);
                studentInfo.setClassName(excelData.getClassName() != null ? excelData.getClassName().trim() : null);
                studentInfo.setStatus(excelData.getStatus() != null && !excelData.getStatus().trim().isEmpty() ? 
                        excelData.getStatus().trim() : "ACTIVE");
                studentInfo.setCreateTime(now);
                studentInfo.setUpdateTime(now);

                studentInfoMapper.insert(studentInfo);
                importedCount++;
            } catch (Exception e) {
                log.error("导入学生信息失败：{}", excelData.getStudentId(), e);
                errorMessages.add("学号《" + excelData.getStudentId() + "》导入失败：" + e.getMessage());
                failedCount++;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("importedCount", importedCount);
        response.put("failedCount", failedCount);
        response.put("errorMessages", errorMessages);
        response.put("message", String.format("学生信息导入完成，成功导入%d条，失败%d条", importedCount, failedCount));
        response.put("importTime", now);

        return response;
    }

    @Override
    public Resource exportStudentInfo(Long universityId) {
        LambdaQueryWrapper<StudentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StudentInfo::getSchoolId, universityId)
                .eq(StudentInfo::getIsDeleted, 0)
                .orderByDesc(StudentInfo::getCreateTime);

        List<StudentInfo> students = studentInfoMapper.selectList(queryWrapper);

        List<StudentInfoExcelDTO> excelDataList = students.stream()
                .map(this::convertToStudentExcelDTO)
                .collect(Collectors.toList());

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            EasyExcel.write(outputStream, StudentInfoExcelDTO.class)
                    .sheet("学生信息")
                    .doWrite(excelDataList);

            byte[] bytes = outputStream.toByteArray();
            return new ByteArrayResource(bytes);
        } catch (IOException e) {
            log.error("生成Excel文件失败", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Excel文件生成失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Map<String, Object> updateStudentInfo(Long universityId, Long studentInfoId, String major, String grade, String className, String status) {
        StudentInfo studentInfo = studentInfoMapper.selectById(studentInfoId);
        if (studentInfo == null || !universityId.equals(studentInfo.getSchoolId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学生信息不存在");
        }

        if (StringUtils.hasText(major)) {
            studentInfo.setMajor(major);
        }
        if (StringUtils.hasText(grade)) {
            studentInfo.setGrade(grade);
        }
        if (StringUtils.hasText(className)) {
            studentInfo.setClassName(className);
        }
        if (StringUtils.hasText(status)) {
            studentInfo.setStatus(status);
        }
        studentInfo.setUpdateTime(LocalDateTime.now());

        studentInfoMapper.updateById(studentInfo);

        Map<String, Object> response = new HashMap<>();
        response.put("id", studentInfo.getId());
        response.put("studentId", studentInfo.getStudentId());
        response.put("message", "学生信息更新成功");

        return response;
    }

    @Override
    @Transactional
    public void deleteStudentInfo(Long universityId, Long studentInfoId) {
        StudentInfo studentInfo = studentInfoMapper.selectById(studentInfoId);
        if (studentInfo == null || !universityId.equals(studentInfo.getSchoolId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "学生信息不存在");
        }

        studentInfoMapper.deleteById(studentInfoId);
    }

    // ==================== 教师信息管理 ====================

    @Override
    public Map<String, Object> getTeacherInfoList(Long universityId, String keyword, String department, String title, Integer page, Integer size) {
        LambdaQueryWrapper<TeacherInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeacherInfo::getSchoolId, universityId)
                .eq(TeacherInfo::getIsDeleted, 0);

        // 关键词搜索（工号、姓名）
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(TeacherInfo::getTeacherId, keyword)
                    .or()
                    .like(TeacherInfo::getName, keyword)
            );
        }

        // 部门筛选
        if (StringUtils.hasText(department)) {
            queryWrapper.like(TeacherInfo::getDepartment, department);
        }

        // 职称筛选
        if (StringUtils.hasText(title)) {
            queryWrapper.eq(TeacherInfo::getTitle, title);
        }

        queryWrapper.orderByDesc(TeacherInfo::getCreateTime);

        Page<TeacherInfo> pageObj = new Page<>(page, size);
        Page<TeacherInfo> result = teacherInfoMapper.selectPage(pageObj, queryWrapper);

        List<Map<String, Object>> list = result.getRecords().stream()
                .map(teacher -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", teacher.getId());
                    item.put("teacherId", teacher.getTeacherId());
                    item.put("name", teacher.getName());
                    item.put("idCard", teacher.getIdCard());
                    item.put("department", teacher.getDepartment());
                    item.put("title", teacher.getTitle());
                    item.put("phone", teacher.getPhone());
                    item.put("email", teacher.getEmail());
                    item.put("status", teacher.getStatus());
                    item.put("createTime", teacher.getCreateTime());
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
    @Transactional
    public Map<String, Object> addTeacherInfo(Long universityId, String teacherId, String name, String idCard, String department, String title, String phone, String email) {
        // 检查工号是否已存在
        LambdaQueryWrapper<TeacherInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeacherInfo::getSchoolId, universityId)
                .eq(TeacherInfo::getTeacherId, teacherId)
                .eq(TeacherInfo::getIsDeleted, 0);
        TeacherInfo existing = teacherInfoMapper.selectOne(queryWrapper);
        if (existing != null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "工号已存在");
        }

        TeacherInfo teacherInfo = new TeacherInfo();
        teacherInfo.setSchoolId(universityId);
        teacherInfo.setTeacherId(teacherId);
        teacherInfo.setName(name);
        teacherInfo.setIdCard(idCard);
        teacherInfo.setDepartment(department);
        teacherInfo.setTitle(title);
        teacherInfo.setPhone(phone);
        teacherInfo.setEmail(email);
        teacherInfo.setStatus("ACTIVE");
        teacherInfo.setCreateTime(LocalDateTime.now());
        teacherInfo.setUpdateTime(LocalDateTime.now());

        teacherInfoMapper.insert(teacherInfo);

        Map<String, Object> response = new HashMap<>();
        response.put("id", teacherInfo.getId());
        response.put("teacherId", teacherId);
        response.put("name", name);
        response.put("message", "教师信息添加成功");

        return response;
    }

    @Override
    @Transactional
    public Map<String, Object> importTeacherInfo(Long universityId, MultipartFile file) {
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文件格式不正确，请上传Excel文件");
        }

        // 使用EasyExcel读取Excel文件
        List<TeacherInfoExcelDTO> excelDataList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();

        try {
            EasyExcel.read(file.getInputStream(), TeacherInfoExcelDTO.class, new TeacherInfoExcelListener(excelDataList, errorMessages))
                    .sheet()
                    .headRowNumber(1)
                    .doRead();
        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Excel文件读取失败：" + e.getMessage());
        }

        // 转换并保存数据
        int importedCount = 0;
        int failedCount = 0;
        LocalDateTime now = LocalDateTime.now();

        for (TeacherInfoExcelDTO excelData : excelDataList) {
            try {
                // 跳过空行
                if (excelData.getTeacherId() == null || excelData.getTeacherId().trim().isEmpty()) {
                    continue;
                }

                String teacherId = excelData.getTeacherId().trim();

                // 检查工号是否已存在
                LambdaQueryWrapper<TeacherInfo> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TeacherInfo::getSchoolId, universityId)
                        .eq(TeacherInfo::getTeacherId, teacherId)
                        .eq(TeacherInfo::getIsDeleted, 0);
                TeacherInfo existing = teacherInfoMapper.selectOne(queryWrapper);
                if (existing != null) {
                    errorMessages.add("工号《" + teacherId + "》已存在，跳过导入");
                    failedCount++;
                    continue;
                }

                TeacherInfo teacherInfo = new TeacherInfo();
                teacherInfo.setSchoolId(universityId);
                teacherInfo.setTeacherId(teacherId);
                teacherInfo.setName(excelData.getName() != null ? excelData.getName().trim() : "");
                teacherInfo.setIdCard(excelData.getIdCard() != null ? excelData.getIdCard().trim() : "");
                teacherInfo.setDepartment(excelData.getDepartment() != null ? excelData.getDepartment().trim() : null);
                teacherInfo.setTitle(excelData.getTitle() != null ? excelData.getTitle().trim() : null);
                teacherInfo.setPhone(excelData.getPhone() != null ? excelData.getPhone().trim() : null);
                teacherInfo.setEmail(excelData.getEmail() != null ? excelData.getEmail().trim() : null);
                teacherInfo.setStatus(excelData.getStatus() != null && !excelData.getStatus().trim().isEmpty() ? 
                        excelData.getStatus().trim() : "ACTIVE");
                teacherInfo.setCreateTime(now);
                teacherInfo.setUpdateTime(now);

                teacherInfoMapper.insert(teacherInfo);
                importedCount++;
            } catch (Exception e) {
                log.error("导入教师信息失败：{}", excelData.getTeacherId(), e);
                errorMessages.add("工号《" + excelData.getTeacherId() + "》导入失败：" + e.getMessage());
                failedCount++;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("importedCount", importedCount);
        response.put("failedCount", failedCount);
        response.put("errorMessages", errorMessages);
        response.put("message", String.format("教师信息导入完成，成功导入%d条，失败%d条", importedCount, failedCount));
        response.put("importTime", now);

        return response;
    }

    @Override
    public Resource exportTeacherInfo(Long universityId) {
        LambdaQueryWrapper<TeacherInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeacherInfo::getSchoolId, universityId)
                .eq(TeacherInfo::getIsDeleted, 0)
                .orderByDesc(TeacherInfo::getCreateTime);

        List<TeacherInfo> teachers = teacherInfoMapper.selectList(queryWrapper);

        List<TeacherInfoExcelDTO> excelDataList = teachers.stream()
                .map(this::convertToTeacherExcelDTO)
                .collect(Collectors.toList());

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            EasyExcel.write(outputStream, TeacherInfoExcelDTO.class)
                    .sheet("教师信息")
                    .doWrite(excelDataList);

            byte[] bytes = outputStream.toByteArray();
            return new ByteArrayResource(bytes);
        } catch (IOException e) {
            log.error("生成Excel文件失败", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Excel文件生成失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Map<String, Object> updateTeacherInfo(Long universityId, Long teacherInfoId, String department, String title, String phone, String email, String status) {
        TeacherInfo teacherInfo = teacherInfoMapper.selectById(teacherInfoId);
        if (teacherInfo == null || !universityId.equals(teacherInfo.getSchoolId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "教师信息不存在");
        }

        if (StringUtils.hasText(department)) {
            teacherInfo.setDepartment(department);
        }
        if (StringUtils.hasText(title)) {
            teacherInfo.setTitle(title);
        }
        if (StringUtils.hasText(phone)) {
            teacherInfo.setPhone(phone);
        }
        if (StringUtils.hasText(email)) {
            teacherInfo.setEmail(email);
        }
        if (StringUtils.hasText(status)) {
            teacherInfo.setStatus(status);
        }
        teacherInfo.setUpdateTime(LocalDateTime.now());

        teacherInfoMapper.updateById(teacherInfo);

        Map<String, Object> response = new HashMap<>();
        response.put("id", teacherInfo.getId());
        response.put("teacherId", teacherInfo.getTeacherId());
        response.put("message", "教师信息更新成功");

        return response;
    }

    @Override
    @Transactional
    public void deleteTeacherInfo(Long universityId, Long teacherInfoId) {
        TeacherInfo teacherInfo = teacherInfoMapper.selectById(teacherInfoId);
        if (teacherInfo == null || !universityId.equals(teacherInfo.getSchoolId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "教师信息不存在");
        }

        teacherInfoMapper.deleteById(teacherInfoId);
    }

    // ==================== 辅助方法 ====================

    /**
     * 将StudentInfo转换为Excel DTO
     */
    private StudentInfoExcelDTO convertToStudentExcelDTO(StudentInfo student) {
        StudentInfoExcelDTO dto = new StudentInfoExcelDTO();
        dto.setStudentId(student.getStudentId());
        dto.setName(student.getName());
        dto.setIdCard(student.getIdCard());
        dto.setMajor(student.getMajor());
        dto.setGrade(student.getGrade());
        dto.setClassName(student.getClassName());
        dto.setStatus(student.getStatus());
        return dto;
    }

    /**
     * 将TeacherInfo转换为Excel DTO
     */
    private TeacherInfoExcelDTO convertToTeacherExcelDTO(TeacherInfo teacher) {
        TeacherInfoExcelDTO dto = new TeacherInfoExcelDTO();
        dto.setTeacherId(teacher.getTeacherId());
        dto.setName(teacher.getName());
        dto.setIdCard(teacher.getIdCard());
        dto.setDepartment(teacher.getDepartment());
        dto.setTitle(teacher.getTitle());
        dto.setPhone(teacher.getPhone());
        dto.setEmail(teacher.getEmail());
        dto.setStatus(teacher.getStatus());
        return dto;
    }

    /**
     * 学生信息Excel读取监听器
     */
    private static class StudentInfoExcelListener extends AnalysisEventListener<StudentInfoExcelDTO> {
        private final List<StudentInfoExcelDTO> cachedDataList;
        @SuppressWarnings("unused")
        private final List<String> errorMessages;

        public StudentInfoExcelListener(List<StudentInfoExcelDTO> cachedDataList, List<String> errorMessages) {
            this.cachedDataList = cachedDataList;
            this.errorMessages = errorMessages;
        }

        @Override
        public void invoke(StudentInfoExcelDTO data, AnalysisContext context) {
            cachedDataList.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 读取完成后的处理
        }
    }

    /**
     * 教师信息Excel读取监听器
     */
    private static class TeacherInfoExcelListener extends AnalysisEventListener<TeacherInfoExcelDTO> {
        private final List<TeacherInfoExcelDTO> cachedDataList;
        @SuppressWarnings("unused")
        private final List<String> errorMessages;

        public TeacherInfoExcelListener(List<TeacherInfoExcelDTO> cachedDataList, List<String> errorMessages) {
            this.cachedDataList = cachedDataList;
            this.errorMessages = errorMessages;
        }

        @Override
        public void invoke(TeacherInfoExcelDTO data, AnalysisContext context) {
            cachedDataList.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 读取完成后的处理
        }
    }
}
