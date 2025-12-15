package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yourschool.campussystem.enums.ScheduleStatusEnum;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 团队行程实体类
 */
@Data
@TableName("team_schedule")
public class TeamSchedule {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("team_id")
    private Long teamId;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("location")
    private String location;

    @TableField("need_confirm")
    private Boolean needConfirm = false;

    @TableField("status")
    private ScheduleStatusEnum status;

    @TableField("creator_id")
    private Long creatorId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted = 0;
}
