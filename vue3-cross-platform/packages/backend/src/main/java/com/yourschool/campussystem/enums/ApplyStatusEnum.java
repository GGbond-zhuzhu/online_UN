package com.yourschool.campussystem.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "报名状态枚举")
public enum ApplyStatusEnum {
    @Schema(description = "待处理")
    PENDING("待处理"),

    @Schema(description = "已通过")
    APPROVED("已通过"),

    @Schema(description = "已拒绝")
    REJECTED("已拒绝"),

    @Schema(description = "已取消")
    CANCELLED("已取消");

    private final String description;

    ApplyStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}