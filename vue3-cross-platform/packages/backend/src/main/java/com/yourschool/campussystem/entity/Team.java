package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 团队实体类
 */
@Data
@TableName("team")
public class Team {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("avatar")
    private String avatar;

    @TableField("creator_id")
    private Long creatorId;

    @TableField("school_id")
    private Long schoolId;

    @TableField("invite_code")
    private String inviteCode;

    @TableField("need_approve")
    private Boolean needApprove = true;

    @TableField("max_members")
    private Integer maxMembers = 50;

    @TableField("member_count")
    private Integer memberCount = 1;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted = 0;
}
