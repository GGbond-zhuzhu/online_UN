package com.yourschool.campussystem.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "提醒类型枚举")
public enum RemindTypeEnum {
    @Schema(description = "不提醒")
    NONE("不提醒"),

    @Schema(description = "提前5分钟")
    MINUTES_5("提前5分钟"),

    @Schema(description = "提前15分钟")
    MINUTES_15("提前15分钟"),

    @Schema(description = "提前30分钟")
    MINUTES_30("提前30分钟"),

    @Schema(description = "提前1小时")
    HOURS_1("提前1小时"),

    @Schema(description = "提前1天")
    DAYS_1("提前1天"),

    @Schema(description = "自定义")
    CUSTOM("自定义");

    private final String description;

    RemindTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}