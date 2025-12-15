package com.yourschool.campussystem.annotation;

import com.yourschool.campussystem.enums.UserRoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限验证注解
 * 用于标记需要特定角色才能访问的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    /**
     * 允许访问的角色列表
     * 如果为空，则只需要登录即可
     */
    UserRoleEnum[] value() default {};

    /**
     * 是否允许管理员访问所有接口
     * 默认为true，管理员可以访问所有接口
     */
    boolean allowAdmin() default true;
}
