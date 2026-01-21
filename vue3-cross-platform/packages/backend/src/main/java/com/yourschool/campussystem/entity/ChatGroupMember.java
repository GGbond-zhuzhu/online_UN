package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yourschool.campussystem.enums.ChatGroupRoleEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 群聊成员实体类
 */
@Data
@TableName("chat_group_member")
public class ChatGroupMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("group_id")
    private Long groupId;

    @TableField("user_id")
    private Long userId;

    @TableField("role")
    private ChatGroupRoleEnum role;

    @TableField(value = "join_time", fill = FieldFill.INSERT)
    private LocalDateTime joinTime;
}

