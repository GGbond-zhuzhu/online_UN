package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yourschool.campussystem.enums.ParttimeStatusEnum;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 兼职实体类
 */
@Data
@TableName("parttime")
public class Parttime {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("salary_per_hour")
    private BigDecimal salaryPerHour;

    @TableField("recruit_count")
    private Integer recruitCount = 1;

    @TableField("applied_count")
    private Integer appliedCount = 0;

    @TableField("work_start_time")
    private LocalDateTime workStartTime;

    @TableField("work_end_time")
    private LocalDateTime workEndTime;

    @TableField("location")
    private String location;

    @TableField("requirements")
    private String requirements;

    @TableField("status")
    private ParttimeStatusEnum status;

    @TableField("publisher_id")
    private Long publisherId;

    @TableField("school_id")
    private Long schoolId;

    @TableField("contact_name")
    private String contactName;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("image_urls")
    private String imageUrls;  // JSON字符串存储多个图片URL

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted = 0;
}
