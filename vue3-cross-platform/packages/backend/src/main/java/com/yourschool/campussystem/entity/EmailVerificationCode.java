package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 邮箱验证码实体类
 */
@Data
@TableName("email_verification_code")
public class EmailVerificationCode {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("email")
    private String email;

    @TableField("code")
    private String code;

    @TableField("code_id")
    private String codeId;

    @TableField("type")
    private String type;  // LOGIN/RESET_PASSWORD

    @TableField("expire_time")
    private LocalDateTime expireTime;

    @TableField("is_used")
    private Integer isUsed;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
