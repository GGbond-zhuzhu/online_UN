package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 团队行程参与者实体类
 */
@Data
@TableName("team_schedule_attendee")
public class TeamScheduleAttendee {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("schedule_id")
    private Long scheduleId;

    @TableField("user_id")
    private Long userId;

    @TableField("status")
    private String status;  // PENDING/ACCEPTED/REJECTED

    @TableField("confirm_time")
    private LocalDateTime confirmTime;
}
