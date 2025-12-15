package com.yourschool.campussystem.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "二手商品状态枚举")
public enum GoodsStatusEnum {
    @Schema(description = "待审核")
    PENDING("待审核"),

    @Schema(description = "出售中")
    ON_SALE("出售中"),

    @Schema(description = "已下架")
    OFF_SHELF("已下架"),

    @Schema(description = "已售出")
    SOLD_OUT("已售出"),

    @Schema(description = "已拒绝")
    REJECTED("已拒绝");

    private final String description;

    GoodsStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}