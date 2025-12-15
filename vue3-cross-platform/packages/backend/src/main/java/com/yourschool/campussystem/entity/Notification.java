package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通知实体类
 */
@Data
@TableName("notification")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("university_id")
    private Long universityId;  // NULL表示平台通知

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("target_type")
    private String targetType;  // ALL/STUDENT/TEACHER/VISITOR

    @TableField("target_user_id")
    private Long targetUserId;  // NULL表示群发

    @TableField("is_urgent")
    private Integer isUrgent;

    @TableField("is_read")
    private Integer isRead;

    @TableField("read_time")
    private LocalDateTime readTime;

    @TableField("sender_id")
    private Long senderId;

    @TableField(value = "send_time", fill = FieldFill.INSERT)
    private LocalDateTime sendTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
