package com.yourschool.campussystem.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "消费类型枚举")
public enum ConsumeTypeEnum {
    @Schema(description = "食堂消费")
    CANTEEN("食堂消费"),

    @Schema(description = "超市购物")
    SUPERMARKET("超市购物"),

    @Schema(description = "图书借阅")
    LIBRARY("图书借阅"),

    @Schema(description = "医疗费用")
    MEDICAL("医疗费用"),

    @Schema(description = "其他消费")
    OTHER("其他消费");

    private final String description;

    ConsumeTypeEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}