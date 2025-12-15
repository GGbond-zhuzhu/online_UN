package com.yourschool.campussystem.service;

import com.yourschool.campussystem.dto.LoginDTO;
import com.yourschool.campussystem.dto.UserRegisterDTO;
import com.yourschool.campussystem.vo.LoginVO;
import com.yourschool.campussystem.vo.UserInfoVO;
import jakarta.servlet.http.HttpServletRequest;

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
     * 提交身份认证申请
     */
    Long applyAuth(Long userId, String applyRole, String realName, String idCard,
                   String schoolName, String major, String grade,
                   String studentCardFront, String studentCardBack);
}