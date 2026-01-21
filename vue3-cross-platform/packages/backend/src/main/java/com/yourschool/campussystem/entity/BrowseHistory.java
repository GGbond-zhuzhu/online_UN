package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 浏览记录实体类
 */
@Data
@TableName("browse_history")
public class BrowseHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("content_type")
    private String contentType;  // SECONDHAND/PARTTIME

    @TableField("content_id")
    private Long contentId;

    @TableField(value = "view_time", fill = FieldFill.INSERT)
    private LocalDateTime viewTime;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;  // 0-未删除，1-已删除
}
