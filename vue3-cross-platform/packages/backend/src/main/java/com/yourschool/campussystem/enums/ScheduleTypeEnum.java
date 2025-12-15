package com.yourschool.campussystem.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "行程类型枚举")
public enum ScheduleTypeEnum {
    @Schema(description = "课程")
    CLASS("课程"),

    @Schema(description = "会议")
    MEETING("会议"),

    @Schema(description = "活动")
    EVENT("活动"),

    @Schema(description = "考试")
    EXAM("考试"),

    @Schema(description = "作业")
    HOMEWORK("作业"),

    @Schema(description = "个人事务")
    PERSONAL("个人事务"),

    @Schema(description = "团队事务")
    TEAM("团队事务"),

    @Schema(description = "其他")
    OTHER("其他");

    private final String description;

    ScheduleTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}