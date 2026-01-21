package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourschool.campussystem.dto.LoginDTO;
import com.yourschool.campussystem.dto.UserRegisterDTO;
import com.yourschool.campussystem.dto.UserUpdateDTO;
import com.yourschool.campussystem.entity.BrowseHistory;
import com.yourschool.campussystem.entity.Favorite;
import com.yourschool.campussystem.entity.Parttime;
import com.yourschool.campussystem.entity.SecondhandGoods;
import com.yourschool.campussystem.entity.User;
import com.yourschool.campussystem.entity.UserLoginLog;
import com.yourschool.campussystem.enums.UserRoleEnum;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.BrowseHistoryMapper;
import com.yourschool.campussystem.mapper.FavoriteMapper;
import com.yourschool.campussystem.mapper.ParttimeMapper;
import com.yourschool.campussystem.mapper.SecondhandGoodsMapper;
import com.yourschool.campussystem.mapper.UserLoginLogMapper;
import com.yourschool.campussystem.mapper.UserMapper;
import com.yourschool.campussystem.service.IpLocationService;
import com.yourschool.campussystem.service.UserService;
import com.yourschool.campussystem.util.JWTUtils;
import com.yourschool.campussystem.vo.LoginVO;
import com.yourschool.campussystem.vo.UserInfoVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserLoginLogMapper userLoginLogMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final IpLocationService ipLocationService;
    private final FavoriteMapper favoriteMapper;
    private final BrowseHistoryMapper browseHistoryMapper;
    private final SecondhandGoodsMapper secondhandGoodsMapper;
    private final ParttimeMapper parttimeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(UserRegisterDTO registerDTO) {
        try {
            log.info("开始注册用户: username={}", registerDTO.getUsername());
            
            // 1. 验证用户名是否已存在
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, registerDTO.getUsername());
            Long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                log.warn("用户名已存在: username={}", registerDTO.getUsername());
                throw new BusinessException(ErrorCode.USERNAME_EXIST);
            }

            // 2. 验证密码和确认密码是否一致（如果提供了确认密码）
            if (registerDTO.getConfirmPassword() != null && 
                !registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
                log.warn("密码和确认密码不一致: username={}", registerDTO.getUsername());
                throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
            }

            // 3. 创建用户对象
            User user = new User();
            user.setUsername(registerDTO.getUsername());
            
            // 加密密码
            try {
                String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
                user.setPassword(encodedPassword);
                log.debug("密码加密成功: username={}", registerDTO.getUsername());
            } catch (Exception e) {
                log.error("密码加密失败: username={}", registerDTO.getUsername(), e);
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "密码加密失败");
            }
            
            // 设置昵称，如果没有提供则使用用户名
            String nickname = registerDTO.getNickname();
            if (nickname == null || nickname.trim().isEmpty()) {
                nickname = registerDTO.getUsername();
            }
            user.setNickname(nickname);
            log.debug("设置昵称: nickname={}", nickname);
            
            // 设置手机号和邮箱
            if (registerDTO.getPhone() != null && !registerDTO.getPhone().trim().isEmpty()) {
                user.setPhone(registerDTO.getPhone().trim());
            }
            if (registerDTO.getEmail() != null && !registerDTO.getEmail().trim().isEmpty()) {
                user.setEmail(registerDTO.getEmail().trim());
            }

            // 设置用户角色（确保不为null，默认为游客）
            UserRoleEnum role = registerDTO.getRole();
            if (role == null) {
                role = UserRoleEnum.TOURIST;
            }
            user.setRole(role);
            log.debug("设置用户角色: role={}", role);

            // 设置学校ID
            user.setSchoolId(registerDTO.getSchoolId());
            
            // 设置时间戳
            LocalDateTime now = LocalDateTime.now();
            user.setCreateTime(now);
            user.setUpdateTime(now);
            
            // 设置isDeleted字段，确保用户状态正常（0表示未删除）
            user.setIsDeleted(0);
            
            log.debug("准备保存用户到数据库: username={}, role={}, nickname={}", 
                    user.getUsername(), user.getRole(), user.getNickname());

            // 4. 保存用户到数据库
            try {
                int insertResult = userMapper.insert(user);
                if (insertResult <= 0) {
                    log.error("用户注册失败：数据库插入返回结果异常, username={}, insertResult={}", 
                            registerDTO.getUsername(), insertResult);
                    throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "用户注册失败，数据库操作异常");
                }
                
                // 验证是否成功获取到ID
                if (user.getId() == null) {
                    log.error("用户注册失败：未获取到用户ID, username={}", registerDTO.getUsername());
                    throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "用户注册失败，未获取到用户ID");
                }
                
                log.info("用户注册成功: userId={}, username={}, role={}, nickname={}", 
                        user.getId(), user.getUsername(), user.getRole(), user.getNickname());
                return user.getId();
                
            } catch (Exception e) {
                log.error("保存用户到数据库时发生异常: username={}", registerDTO.getUsername(), e);
                // 如果是业务异常，直接抛出
                if (e instanceof BusinessException) {
                    throw e;
                }
                // 其他异常包装为业务异常
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, 
                        "用户注册失败：" + (e.getMessage() != null ? e.getMessage() : "数据库操作异常"));
            }
            
        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            // 捕获所有其他异常
            log.error("用户注册过程发生未知异常: username={}", registerDTO.getUsername(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, 
                    "用户注册失败：" + (e.getMessage() != null ? e.getMessage() : "系统异常"));
        }
    }

    @Override
    public LoginVO login(LoginDTO loginDTO, HttpServletRequest request) {
        try {
            log.info("用户登录请求: username={}", loginDTO.getUsername());
            
            // 1. 查询用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUsername, loginDTO.getUsername());
            User user = userMapper.selectOne(queryWrapper);

            if (user == null) {
                log.warn("用户不存在: username={}", loginDTO.getUsername());
                throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
            }

            log.debug("查询到用户: id={}, username={}, role={}", user.getId(), user.getUsername(), user.getRole());

            // 2. 验证密码
            boolean passwordMatches = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
            log.debug("密码验证结果: {}", passwordMatches);
            
            if (!passwordMatches) {
                log.warn("密码验证失败: username={}", loginDTO.getUsername());
                throw new BusinessException(ErrorCode.USERNAME_OR_PASSWORD_ERROR);
            }

            // 3. 检查用户是否被逻辑删除
            if (user.getIsDeleted() != null && user.getIsDeleted() == 1) {
                log.warn("账号已被禁用: username={}", loginDTO.getUsername());
                throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
            }

            // 4. 生成JWT Token - 将枚举转换为字符串
            String roleName = user.getRole() != null ? user.getRole().name() : "TOURIST";
            String token = JWTUtils.generateToken(user.getId(), user.getUsername(), roleName);
            log.debug("Token生成成功: userId={}", user.getId());

            // 5. 记录登录日志
            if (request != null) {
                try {
                    recordLoginLog(user.getId(), request);
                } catch (Exception e) {
                    log.warn("记录登录日志失败: userId={}", user.getId(), e);
                    // 登录日志记录失败不影响登录流程
                }
            }

            // 6. 生成登录响应
            LoginVO loginVO = new LoginVO();
            loginVO.setUserId(user.getId());
            loginVO.setUsername(user.getUsername());
            loginVO.setRole(user.getRole());
            loginVO.setToken(token);
            loginVO.setExpiresIn(JWTUtils.EXPIRATION);

            log.info("用户登录成功: username={}, userId={}", loginDTO.getUsername(), user.getId());
            return loginVO;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("登录过程发生异常: username={}", loginDTO.getUsername(), e);
            // 输出详细的异常堆栈信息，方便调试
            e.printStackTrace();
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, 
                    "登录失败：" + (e.getMessage() != null ? e.getMessage() : "系统异常"));
        }
    }

    /**
     * 记录用户登录日志
     */
    private void recordLoginLog(Long userId, HttpServletRequest request) {
        UserLoginLog loginLog = new UserLoginLog();
        loginLog.setUserId(userId);
        
        // 获取客户端IP
        String ip = getClientIpAddress(request);
        loginLog.setLoginIp(ip);
        
        // 获取用户代理（设备信息）
        String userAgent = request.getHeader("User-Agent");
        loginLog.setLoginDevice(userAgent != null ? userAgent : "Unknown");
        
        // 根据IP获取地理位置（调用高德地图API）
        try {
            String location = ipLocationService.getLocationByIp(ip);
            loginLog.setLoginLocation(location);
        } catch (Exception e) {
            log.warn("获取IP地理位置失败: ip={}", ip, e);
            // IP定位失败不影响登录日志记录
            loginLog.setLoginLocation(null);
        }
        
        loginLog.setLoginTime(LocalDateTime.now());
        
        userLoginLogMapper.insert(loginLog);
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setRole(user.getRole());
        userInfo.setSchoolId(user.getSchoolId());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setPhone(user.getPhone());
        userInfo.setCreateTime(user.getCreateTime());
        userInfo.setUpdateTime(user.getUpdateTime());

        return userInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO updateUserInfo(Long userId, UserUpdateDTO updateDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 更新用户信息
        if (updateDTO.getNickname() != null) {
            user.setNickname(updateDTO.getNickname());
        }
        if (updateDTO.getAvatarUrl() != null) {
            user.setAvatarUrl(updateDTO.getAvatarUrl());
        }
        if (updateDTO.getPhone() != null) {
            user.setPhone(updateDTO.getPhone());
        }
        if (updateDTO.getEmail() != null) {
            user.setEmail(updateDTO.getEmail());
        }

        userMapper.updateById(user);

        // 返回更新后的用户信息
        return getUserInfo(userId);
    }

    @Override
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // 统计收藏数（二手商品 + 兼职岗位）
        LambdaQueryWrapper<Favorite> favoriteWrapper = new LambdaQueryWrapper<>();
        favoriteWrapper.eq(Favorite::getUserId, userId);
        favoriteWrapper.eq(Favorite::getIsDeleted, 0);
        Long favoritesCount = favoriteMapper.selectCount(favoriteWrapper);
        stats.put("favoritesCount", favoritesCount);

        // 统计浏览历史数
        LambdaQueryWrapper<BrowseHistory> historyWrapper = new LambdaQueryWrapper<>();
        historyWrapper.eq(BrowseHistory::getUserId, userId);
        historyWrapper.eq(BrowseHistory::getIsDeleted, 0);
        Long historyCount = browseHistoryMapper.selectCount(historyWrapper);
        stats.put("historyCount", historyCount);

        // 统计发布数（二手商品 + 兼职岗位）
        LambdaQueryWrapper<SecondhandGoods> secondhandWrapper = new LambdaQueryWrapper<>();
        secondhandWrapper.eq(SecondhandGoods::getPublisherId, userId);
        secondhandWrapper.eq(SecondhandGoods::getIsDeleted, 0);
        Long secondhandCount = secondhandGoodsMapper.selectCount(secondhandWrapper);

        LambdaQueryWrapper<Parttime> parttimeWrapper = new LambdaQueryWrapper<>();
        parttimeWrapper.eq(Parttime::getPublisherId, userId);
        parttimeWrapper.eq(Parttime::getIsDeleted, 0);
        Long parttimeCount = parttimeMapper.selectCount(parttimeWrapper);

        Long publishCount = secondhandCount + parttimeCount;
        stats.put("publishCount", publishCount);

        // 积分（暂时返回默认值，后续可以从积分表查询）
        stats.put("pointsCount", 1280);

        return stats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("开始修改密码: userId={}", userId);
        
        // 1. 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        
        // 2. 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("旧密码验证失败: userId={}", userId);
            throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR, "旧密码不正确");
        }
        
        // 3. 验证新密码不能与旧密码相同
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            log.warn("新密码不能与旧密码相同: userId={}", userId);
            throw new BusinessException(ErrorCode.BAD_REQUEST, "新密码不能与旧密码相同");
        }
        
        // 4. 加密新密码
        String encodedPassword = passwordEncoder.encode(newPassword);
        
        // 5. 更新密码
        user.setPassword(encodedPassword);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        
        log.info("密码修改成功: userId={}", userId);
    }

    @Override
    public Long applyAuth(Long userId, String applyRole, String realName, String idCard,
                          String schoolName, String major, String grade,
                          String studentCardFront, String studentCardBack) {
        return 1L;
    }
}