package com.yourschool.campussystem.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "群聊成员角色枚举")
public enum ChatGroupRoleEnum {
    @Schema(description = "群主")
    OWNER("群主"),

    @Schema(description = "管理员")
    ADMIN("管理员"),

    @Schema(description = "成员")
    MEMBER("成员");

    private final String description;

    ChatGroupRoleEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

