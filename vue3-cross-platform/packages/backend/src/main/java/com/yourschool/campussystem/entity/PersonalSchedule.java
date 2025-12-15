package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yourschool.campussystem.enums.RemindTypeEnum;
import com.yourschool.campussystem.enums.ScheduleStatusEnum;
import com.yourschool.campussystem.enums.ScheduleTypeEnum;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 个人行程实体类
 */
@Data
@TableName("personal_schedule")
public class PersonalSchedule {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("type")
    private ScheduleTypeEnum type;

    @TableField("status")
    private ScheduleStatusEnum status;

    @TableField("location")
    private String location;

    @TableField("is_all_day")
    private Boolean isAllDay = false;

    @TableField("remind_type")
    private RemindTypeEnum remindType;

    @TableField("custom_remind_minutes")
    private Integer customRemindMinutes;

    @TableField("is_repeat")
    private Boolean isRepeat = false;

    @TableField("repeat_rule")
    private String repeatRule;

    @TableField("tag")
    private String tag;

    @TableField("creator_id")
    private Long creatorId;

    @TableField("synced_team_schedule_id")
    private Long syncedTeamScheduleId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted = 0;
}
