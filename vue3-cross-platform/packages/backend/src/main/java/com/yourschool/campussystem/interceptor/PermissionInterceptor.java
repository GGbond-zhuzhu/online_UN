package com.yourschool.campussystem.interceptor;

import com.yourschool.campussystem.annotation.RequireRole;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.entity.User;
import com.yourschool.campussystem.enums.UserRoleEnum;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.UserMapper;
import com.yourschool.campussystem.util.UserContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * 权限验证拦截器
 * 用于验证用户是否有权限访问特定接口
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    private final UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理Controller方法
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // 获取方法上的注解
        RequireRole methodAnnotation = handlerMethod.getMethodAnnotation(RequireRole.class);
        // 获取类上的注解
        RequireRole classAnnotation = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        
        // 优先使用方法上的注解，如果没有则使用类上的注解
        RequireRole requireRole = methodAnnotation != null ? methodAnnotation : classAnnotation;

        // 如果没有权限注解，直接放行
        if (requireRole == null) {
            return true;
        }

        // 获取当前用户ID
        Long userId = UserContextUtils.getUserId(request);
        if (userId == null) {
            // 如果没有登录，抛出未登录异常
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 检查用户是否被禁用
        if (user.getIsDeleted() != null && user.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        UserRoleEnum userRole = user.getRole();
        if (userRole == null) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        // 如果允许管理员访问所有接口，且当前用户是管理员，直接放行
        if (requireRole.allowAdmin() && userRole == UserRoleEnum.ADMIN) {
            return true;
        }

        // 获取允许的角色列表
        UserRoleEnum[] allowedRoles = requireRole.value();
        
        // 如果没有指定角色，只需要登录即可
        if (allowedRoles == null || allowedRoles.length == 0) {
            return true;
        }

        // 检查用户角色是否在允许列表中
        List<UserRoleEnum> allowedRoleList = Arrays.asList(allowedRoles);
        if (!allowedRoleList.contains(userRole)) {
            log.warn("用户权限不足: userId={}, userRole={}, requiredRoles={}", 
                    userId, userRole, allowedRoleList);
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }

        return true;
    }
}
