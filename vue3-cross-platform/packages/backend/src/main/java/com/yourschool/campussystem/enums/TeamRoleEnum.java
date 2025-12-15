package com.yourschool.campussystem.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "团队角色枚举")
public enum TeamRoleEnum {
    @Schema(description = "创建者")
    CREATOR("创建者"),

    @Schema(description = "管理员")
    ADMIN("管理员"),

    @Schema(description = "成员")
    MEMBER("成员");

    private final String description;

    TeamRoleEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}