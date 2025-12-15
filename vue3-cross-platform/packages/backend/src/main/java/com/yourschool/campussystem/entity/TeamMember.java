package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yourschool.campussystem.enums.TeamRoleEnum;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 团队成员实体类
 */
@Data
@TableName("team_member")
public class TeamMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("team_id")
    private Long teamId;

    @TableField("user_id")
    private Long userId;

    @TableField("role")
    private TeamRoleEnum role;

    @TableField(value = "join_time", fill = FieldFill.INSERT)
    private LocalDateTime joinTime;
}
