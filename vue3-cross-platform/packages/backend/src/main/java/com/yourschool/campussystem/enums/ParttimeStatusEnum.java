package com.yourschool.campussystem.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "兼职状态枚举")
public enum ParttimeStatusEnum {
    @Schema(description = "待审核")
    PENDING("待审核"),

    @Schema(description = "招募中")
    RECRUITING("招募中"),

    @Schema(description = "已截止")
    CLOSED("已截止"),

    @Schema(description = "进行中")
    IN_PROGRESS("进行中"),

    @Schema(description = "已完成")
    COMPLETED("已完成"),

    @Schema(description = "已取消")
    CANCELLED("已取消"),

    @Schema(description = "已拒绝")
    REJECTED("已拒绝");

    private final String description;

    ParttimeStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}