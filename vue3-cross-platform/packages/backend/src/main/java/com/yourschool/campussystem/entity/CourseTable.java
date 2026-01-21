package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * 课程表实体类
 */
@Data
@TableName("course_table")
public class CourseTable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("course_name")
    private String courseName;

    @TableField("day_of_week")
    private String dayOfWeek; // 星期（周一、周二...周日）

    @TableField("start_time")
    private LocalTime startTime;

    @TableField("end_time")
    private LocalTime endTime;

    @TableField("location")
    private String location; // 上课地点

    @TableField("teacher")
    private String teacher; // 授课教师

    @TableField("week_range")
    private String weekRange; // 周次范围（如：1-16周）

    @TableField("background_color")
    private String backgroundColor; // 背景颜色

    @TableField("source")
    private String source; // 来源：AUTO（自动导入）、MANUAL（手动添加）

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted = 0;
}

