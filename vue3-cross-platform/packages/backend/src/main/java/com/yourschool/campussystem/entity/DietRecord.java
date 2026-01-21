package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 饮食记录实体类
 */
@Data
@TableName("diet_record")
public class DietRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("diet_date")
    private LocalDate dietDate; // 饮食日期

    @TableField("meal_type")
    private String mealType; // 餐次：BREAKFAST（早餐）、LUNCH（午餐）、DINNER（晚餐）、SNACK（加餐）

    @TableField("food_name")
    private String foodName; // 食物名称

    @TableField("food_detail")
    private String foodDetail; // 食物详情描述

    @TableField("location")
    private String location; // 用餐地点

    @TableField("source")
    private String source; // 来源：CANTEEN（食堂）、TAKEOUT（外卖）、RESTAURANT（餐厅）、HOME（家里）、OTHER（其他）

    @TableField("is_auto_import")
    private Boolean isAutoImport = false; // 是否自动导入（来自E卡通食堂消费）

    @TableField("ecard_record_id")
    private Long ecardRecordId; // 关联的E卡通消费记录ID（如果是自动导入）

    @TableField("background_color")
    private String backgroundColor; // 背景颜色（用户自定义）

    @TableField("calories")
    private Integer calories; // 卡路里（可选）

    @TableField("price")
    private java.math.BigDecimal price; // 价格（可选）

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

