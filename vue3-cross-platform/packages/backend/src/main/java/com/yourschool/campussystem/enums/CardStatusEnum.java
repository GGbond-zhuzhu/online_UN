package com.yourschool.campussystem.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "校园卡状态枚举")
public enum CardStatusEnum {
    @Schema(description = "正常")
    NORMAL("正常"),

    @Schema(description = "挂失")
    LOST("挂失"),

    @Schema(description = "冻结")
    FROZEN("冻结"),

    @Schema(description = "注销")
    CANCELLED("注销");

    private final String description;

    CardStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}