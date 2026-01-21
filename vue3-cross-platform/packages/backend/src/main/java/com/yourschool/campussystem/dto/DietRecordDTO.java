package com.yourschool.campussystem.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 饮食记录DTO
 */
@Data
public class DietRecordDTO {

    @NotNull(message = "餐次不能为空")
    private String mealType; // BREAKFAST、LUNCH、DINNER、SNACK

    @NotNull(message = "食物名称不能为空")
    private String foodName; // 食物名称

    private String foodDetail; // 食物详情描述

    private String location; // 用餐地点

    @NotNull(message = "来源不能为空")
    private String source; // CANTEEN、TAKEOUT、RESTAURANT、HOME、OTHER

    private LocalDate dietDate; // 饮食日期，不传则使用当前日期

    private String backgroundColor; // 背景颜色

    private Integer calories; // 卡路里

    private java.math.BigDecimal price; // 价格
}

