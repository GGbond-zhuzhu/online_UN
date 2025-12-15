package com.yourschool.campussystem.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "行程状态枚举")
public enum ScheduleStatusEnum {
    @Schema(description = "未开始")
    PENDING("未开始"),

    @Schema(description = "进行中")
    IN_PROGRESS("进行中"),

    @Schema(description = "已完成")
    COMPLETED("已完成"),

    @Schema(description = "已取消")
    CANCELLED("已取消"),

    @Schema(description = "已过期")
    EXPIRED("已过期");

    private final String description;

    ScheduleStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}