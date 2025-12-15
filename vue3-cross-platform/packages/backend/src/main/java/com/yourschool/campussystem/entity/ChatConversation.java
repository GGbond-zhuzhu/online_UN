package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 聊天会话实体类
 */
@Data
@TableName("chat_conversation")
public class ChatConversation {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user1_id")
    private Long user1Id;

    @TableField("user2_id")
    private Long user2Id;

    @TableField("last_message_id")
    private Long lastMessageId;

    @TableField("last_message_time")
    private LocalDateTime lastMessageTime;

    @TableField("user1_unread_count")
    private Integer user1UnreadCount;

    @TableField("user2_unread_count")
    private Integer user2UnreadCount;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
