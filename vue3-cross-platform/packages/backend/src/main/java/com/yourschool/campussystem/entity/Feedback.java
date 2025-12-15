package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 反馈实体类
 */
@Data
@TableName("feedback")
public class Feedback {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("feedback_type")
    private String feedbackType;  // BUG/SUGGESTION/COMPLAINT/OTHER

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("contact")
    private String contact;

    @TableField("screenshots")
    private String screenshots;  // JSON字符串存储多个截图URL

    @TableField("status")
    private String status;  // SUBMITTED/PROCESSING/RESOLVED

    @TableField("processor_id")
    private Long processorId;

    @TableField("process_note")
    private String processNote;

    @TableField(value = "submit_time", fill = FieldFill.INSERT)
    private LocalDateTime submitTime;

    @TableField("process_time")
    private LocalDateTime processTime;
}
