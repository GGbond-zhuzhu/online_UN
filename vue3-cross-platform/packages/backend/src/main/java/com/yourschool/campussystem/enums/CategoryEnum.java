package com.yourschool.campussystem.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "二手商品分类枚举")
public enum CategoryEnum {
    @Schema(description = "电子产品")
    ELECTRONICS("电子产品"),

    @Schema(description = "书籍资料")
    BOOKS("书籍资料"),

    @Schema(description = "服装鞋帽")
    CLOTHING("服装鞋帽"),

    @Schema(description = "生活用品")
    DAILY("生活用品"),

    @Schema(description = "运动器材")
    SPORTS("运动器材"),

    @Schema(description = "学习工具")
    STUDY("学习工具"),

    @Schema(description = "其他")
    OTHER("其他");

    private final String description;

    CategoryEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}