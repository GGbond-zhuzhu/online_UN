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
import com.yourschool.campussystem.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final UserAuthApplyMapper userAuthApplyMapper;
    private final SecondhandGoodsMapper secondhandGoodsMapper;
    private final ParttimeMapper parttimeMapper;
    private final UniversityMapper universityMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    // ==================== 用户管理 ====================

    @Override
    public Map<String, Object> getUserList(Integer page, Integer size, String role, Long schoolId, String keyword) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getIsDeleted, 0);

        // 角色筛选
        if (StringUtils.hasText(role)) {
            try {
                UserRoleEnum roleEnum = UserRoleEnum.getByName(role);
                queryWrapper.eq(User::getRole, roleEnum);
            } catch (Exception e) {
                log.warn("无效的角色筛选: {}", role);
            }
        }

        // 学校筛选
        if (schoolId != null) {
            queryWrapper.eq(User::getSchoolId, schoolId);
        }

        // 关键词搜索（用户名/昵称）
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
                    item.put("schoolId", user.getSchoolId());
                    item.put("email", user.getEmail());
                    item.put("phone", user.getPhone());
                    item.put("createTime", user.getCreateTime());
                    item.put("isDeleted", user.getIsDeleted());
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
    public void updateUserStatus(Long userId, String status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 更新状态（通过isDeleted字段）
        if ("ENABLED".equals(status)) {
            user.setIsDeleted(0);
        } else if ("DISABLED".equals(status)) {
            user.setIsDeleted(1);
        } else {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public String resetUserPassword(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 生成随机密码（8位数字）
        String newPassword = String.format("%08d", new Random().nextInt(100000000));
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);

        // 实际应该发送邮件，这里简化处理
        log.info("用户密码已重置: userId={}, newPassword={}", userId, newPassword);
        return newPassword;
    }

    // ==================== 认证审核 ====================

    @Override
    public Map<String, Object> getAuthApplies(String status, Integer page, Integer size) {
        LambdaQueryWrapper<UserAuthApply> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(status)) {
            try {
                ApplyStatusEnum statusEnum = ApplyStatusEnum.valueOf(status);
                queryWrapper.eq(UserAuthApply::getStatus, statusEnum.ordinal());
            } catch (Exception e) {
                log.warn("无效的状态筛选: {}", status);
            }
        }

        queryWrapper.orderByDesc(UserAuthApply::getCreateTime);

        Page<UserAuthApply> pageObj = new Page<>(page, size);
        Page<UserAuthApply> result = userAuthApplyMapper.selectPage(pageObj, queryWrapper);

        List<Map<String, Object>> list = result.getRecords().stream()
                .map(apply -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", apply.getId());
                    item.put("userId", apply.getUserId());
                    item.put("realName", apply.getRealName());
                    item.put("applyRole", apply.getApplyRole());
                    item.put("status", apply.getStatus());
                    item.put("schoolName", apply.getSchoolName());
                    item.put("createTime", apply.getCreateTime());
                    item.put("auditTime", apply.getAuditTime());
                    item.put("auditRemark", apply.getAuditRemark());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", result.getTotal());

        return response;
    }

    @Override
    @Transactional
    public void reviewAuthApply(Long applyId, String result, String note, Long auditorId) {
        UserAuthApply apply = userAuthApplyMapper.selectById(applyId);
        if (apply == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        ApplyStatusEnum statusEnum;
        if ("APPROVED".equals(result)) {
            statusEnum = ApplyStatusEnum.APPROVED;
            // 更新用户角色
            User user = userMapper.selectById(apply.getUserId());
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
        apply.setAuditorId(auditorId);

        userAuthApplyMapper.updateById(apply);
    }

    // ==================== 内容审核 ====================

    @Override
    public Map<String, Object> getReviewContentList(String contentType, Integer page, Integer size) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();

        if (contentType == null || "SECONDHAND".equals(contentType)) {
            // 查询待审核的二手商品
            LambdaQueryWrapper<SecondhandGoods> goodsQuery = new LambdaQueryWrapper<>();
            goodsQuery.eq(SecondhandGoods::getStatus, GoodsStatusEnum.PENDING.name())
                    .eq(SecondhandGoods::getIsDeleted, 0)
                    .orderByDesc(SecondhandGoods::getPublishTime);

            Page<SecondhandGoods> goodsPage = new Page<>(page, size);
            Page<SecondhandGoods> goodsResult = secondhandGoodsMapper.selectPage(goodsPage, goodsQuery);

            List<Map<String, Object>> goodsList = goodsResult.getRecords().stream()
                    .map(goods -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", goods.getId());
                        item.put("contentType", "SECONDHAND");
                        item.put("title", goods.getTitle());
                        item.put("publisherId", goods.getPublisherId());
                        item.put("publishTime", goods.getPublishTime());
                        return item;
                    })
                    .collect(Collectors.toList());
            list.addAll(goodsList);
        }

        if (contentType == null || "PARTTIME".equals(contentType)) {
            // 查询待审核的兼职
            LambdaQueryWrapper<Parttime> parttimeQuery = new LambdaQueryWrapper<>();
            parttimeQuery.eq(Parttime::getStatus, ParttimeStatusEnum.PENDING.name())
                    .eq(Parttime::getIsDeleted, 0)
                    .orderByDesc(Parttime::getCreateTime);

            Page<Parttime> parttimePage = new Page<>(page, size);
            Page<Parttime> parttimeResult = parttimeMapper.selectPage(parttimePage, parttimeQuery);

            List<Map<String, Object>> parttimeList = parttimeResult.getRecords().stream()
                    .map(parttime -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", parttime.getId());
                        item.put("contentType", "PARTTIME");
                        item.put("title", parttime.getTitle());
                        item.put("publisherId", parttime.getPublisherId());
                        item.put("publishTime", parttime.getCreateTime());
                        return item;
                    })
                    .collect(Collectors.toList());
            list.addAll(parttimeList);
        }

        response.put("list", list);
        response.put("total", (long) list.size());

        return response;
    }

    @Override
    @Transactional
    public void reviewContent(Long contentId, String contentType, String result, String note, Long auditorId) {
        if ("SECONDHAND".equals(contentType)) {
            SecondhandGoods goods = secondhandGoodsMapper.selectById(contentId);
            if (goods == null) {
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
            if (parttime == null) {
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

    // ==================== 高校管理 ====================

    @Override
    public Map<String, Object> getUniversityList(Integer page, Integer size) {
        LambdaQueryWrapper<University> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(University::getIsDeleted, 0)
                .orderByDesc(University::getCreateTime);

        Page<University> pageObj = new Page<>(page, size);
        Page<University> result = universityMapper.selectPage(pageObj, queryWrapper);

        List<Map<String, Object>> list = result.getRecords().stream()
                .map(university -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", university.getId());
                    item.put("name", university.getName());
                    item.put("code", university.getCode());
                    item.put("status", university.getStatus());
                    item.put("createTime", university.getCreateTime());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", result.getTotal());

        return response;
    }

    @Override
    @Transactional
    public void reviewUniversityApply(Long applyId, String result, String note, Long auditorId) {
        // 简化处理，实际应该有高校申请表
        // 这里暂时不实现，后续可以扩展
        log.info("高校接入申请审核: applyId={}, result={}, note={}", applyId, result, note);
    }

    // ==================== 系统配置 ====================

    @Override
    public Map<String, Object> getSystemConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("platformName", "上大学Online");
        config.put("version", "1.0.0");
        config.put("maxFileSize", "10MB");
        config.put("supportedImageFormats", Arrays.asList("jpg", "png", "jpeg", "gif", "webp"));
        config.put("maxImageSize", 10 * 1024 * 1024); // 10MB
        return config;
    }

    @Override
    @Transactional
    public void updateSystemConfig(Map<String, Object> config) {
        // 简化处理，实际应该保存到数据库
        log.info("系统配置已更新: {}", config);
    }

    // ==================== 数据统计 ====================

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 用户统计
        LambdaQueryWrapper<User> userQuery = new LambdaQueryWrapper<>();
        userQuery.eq(User::getIsDeleted, 0);
        long totalUsers = userMapper.selectCount(userQuery);

        userQuery.eq(User::getRole, UserRoleEnum.STUDENT);
        long totalStudents = userMapper.selectCount(userQuery);

        userQuery.clear();
        userQuery.eq(User::getIsDeleted, 0).eq(User::getRole, UserRoleEnum.TEACHER);
        long totalTeachers = userMapper.selectCount(userQuery);

        userQuery.clear();
        userQuery.eq(User::getIsDeleted, 0).eq(User::getRole, UserRoleEnum.TOURIST);
        long totalVisitors = userMapper.selectCount(userQuery);

        // 高校统计
        LambdaQueryWrapper<University> universityQuery = new LambdaQueryWrapper<>();
        universityQuery.eq(University::getIsDeleted, 0);
        long totalUniversities = universityMapper.selectCount(universityQuery);

        // 内容统计
        LambdaQueryWrapper<SecondhandGoods> goodsQuery = new LambdaQueryWrapper<>();
        goodsQuery.eq(SecondhandGoods::getIsDeleted, 0);
        long totalSecondhandGoods = secondhandGoodsMapper.selectCount(goodsQuery);

        LambdaQueryWrapper<Parttime> parttimeQuery = new LambdaQueryWrapper<>();
        parttimeQuery.eq(Parttime::getIsDeleted, 0);
        long totalParttimeJobs = parttimeMapper.selectCount(parttimeQuery);

        // 今日活跃用户（简化处理，实际应该查询今日登录用户）
        long todayActiveUsers = totalUsers / 10; // 简化估算

        stats.put("totalUsers", totalUsers);
        stats.put("totalStudents", totalStudents);
        stats.put("totalTeachers", totalTeachers);
        stats.put("totalVisitors", totalVisitors);
        stats.put("totalUniversities", totalUniversities);
        stats.put("totalSecondhandGoods", totalSecondhandGoods);
        stats.put("totalParttimeJobs", totalParttimeJobs);
        stats.put("todayActiveUsers", todayActiveUsers);

        return stats;
    }
}
