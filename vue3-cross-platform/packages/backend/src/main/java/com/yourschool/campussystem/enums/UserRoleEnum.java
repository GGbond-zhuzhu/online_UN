package com.yourschool.campussystem.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "用户角色枚举")
public enum UserRoleEnum {
    @Schema(description = "游客")
    TOURIST("tourist", "游客"),

    @Schema(description = "学生")
    STUDENT("student", "学生"),

    @Schema(description = "教师")
    TEACHER("teacher", "教师"),

    @Schema(description = "管理员")
    ADMIN("admin", "管理员"),

    @Schema(description = "高校")
    UNIVERSITY("university", "高校");

    private final String code;
    private final String description;

    UserRoleEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // 根据code获取枚举
    public static UserRoleEnum getByCode(String code) {
        for (UserRoleEnum role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        return TOURIST;
    }

    // 根据name获取枚举
    public static UserRoleEnum getByName(String name) {
        try {
            return UserRoleEnum.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TOURIST;
        }
    }
}