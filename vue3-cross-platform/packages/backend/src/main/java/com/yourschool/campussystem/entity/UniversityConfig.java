package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 高校功能配置实体类
 */
@Data
@TableName("university_config")
public class UniversityConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("university_id")
    private Long universityId;

    @TableField("config_key")
    private String configKey;

    @TableField("config_value")
    private String configValue;  // JSON格式存储配置值

    @TableField("description")
    private String description;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
