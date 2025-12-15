package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 团队邀请实体类
 */
@Data
@TableName("team_invite")
public class TeamInvite {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("team_id")
    private Long teamId;

    @TableField("inviter_id")
    private Long inviterId;

    @TableField("invitee_id")
    private Long inviteeId;

    @TableField("status")
    private String status;  // PENDING/ACCEPTED/REJECTED

    @TableField(value = "invite_time", fill = FieldFill.INSERT)
    private LocalDateTime inviteTime;

    @TableField("process_time")
    private LocalDateTime processTime;
}
