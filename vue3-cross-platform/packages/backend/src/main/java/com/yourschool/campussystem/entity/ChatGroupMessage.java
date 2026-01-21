package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 群聊消息实体类
 */
@Data
@TableName("chat_group_message")
public class ChatGroupMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("group_id")
    private Long groupId;

    @TableField("sender_id")
    private Long senderId;

    @TableField("content")
    private String content;

    @TableField("type")
    private String type; // TEXT/IMAGE/FILE

    @TableField("image_url")
    private String imageUrl;

    @TableField("file_url")
    private String fileUrl;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

