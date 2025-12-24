package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yourschool.campussystem.enums.UserRoleEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("nickname")
    private String nickname;

    @TableField(value = "role", typeHandler = com.yourschool.campussystem.config.UserRoleEnumTypeHandler.class)
    private UserRoleEnum role;

    @TableField("school_id")
    private Long schoolId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;
}