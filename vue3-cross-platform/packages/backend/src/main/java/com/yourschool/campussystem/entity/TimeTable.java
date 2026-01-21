package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * 时间表实体类（用于记录用户自定义的时间段设置）
 */
@Data
@TableName("time_table")
public class TimeTable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("period_name")
    private String periodName; // 时间段名称（如：第一节、第二节）

    @TableField("start_time")
    private LocalTime startTime;

    @TableField("end_time")
    private LocalTime endTime;

    @TableField("sort_order")
    private Integer sortOrder; // 排序顺序

    @TableField("is_default")
    private Boolean isDefault; // 是否默认时间表

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted = 0;
}

