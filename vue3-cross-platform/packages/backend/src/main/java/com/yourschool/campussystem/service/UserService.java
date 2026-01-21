package com.yourschool.campussystem.service;

import com.yourschool.campussystem.dto.LoginDTO;
import com.yourschool.campussystem.dto.UserRegisterDTO;
import com.yourschool.campussystem.dto.UserUpdateDTO;
import com.yourschool.campussystem.vo.LoginVO;
import com.yourschool.campussystem.vo.UserInfoVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface UserService {

    /**
     * 用户注册
     */
    Long register(UserRegisterDTO registerDTO);

    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @param request HTTP请求（可选，用于记录登录日志）
     */
    LoginVO login(LoginDTO loginDTO, HttpServletRequest request);

    /**
     * 获取当前用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 更新用户信息
     */
    UserInfoVO updateUserInfo(Long userId, UserUpdateDTO updateDTO);

    /**
     * 获取用户统计数据
     */
    Map<String, Object> getUserStats(Long userId);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 提交身份认证申请
     */
    Long applyAuth(Long userId, String applyRole, String realName, String idCard,
                   String schoolName, String major, String grade,
                   String studentCardFront, String studentCardBack);
}