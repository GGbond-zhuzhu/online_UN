package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户登录日志实体类
 */
@Data
@TableName("user_login_log")
public class UserLoginLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("login_ip")
    private String loginIp;

    @TableField("login_device")
    private String loginDevice;

    @TableField("login_location")
    private String loginLocation;

    @TableField(value = "login_time", fill = FieldFill.INSERT)
    private LocalDateTime loginTime;
}
