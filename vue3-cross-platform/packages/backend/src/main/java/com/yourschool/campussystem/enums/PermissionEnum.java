package com.yourschool.campussystem.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "接口权限枚举")
public enum PermissionEnum {

    // 游客权限
    TOURIST(1, "游客", "只能浏览同校公开信息"),

    // 学生权限
    STUDENT(2, "学生", "完整的学生功能"),

    // 教师权限
    TEACHER(3, "教师", "教师特有功能，如发布兼职"),

    // 管理员权限
    ADMIN(4, "管理员", "系统管理权限"),

    // 高校权限
    UNIVERSITY(5, "高校", "高校级别管理权限");

    private final Integer level;
    private final String name;
    private final String description;

    PermissionEnum(Integer level, String name, String description) {
        this.level = level;
        this.name = name;
        this.description = description;
    }
}