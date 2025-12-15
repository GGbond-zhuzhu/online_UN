package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_auth_apply")
public class UserAuthApply implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("real_name")
    private String realName;

    @TableField("id_card")
    private String idCard;

    @TableField("student_card_front")
    private String studentCardFront;

    @TableField("student_card_back")
    private String studentCardBack;

    @TableField("school_name")
    private String schoolName;

    @TableField("major")
    private String major;

    @TableField("grade")
    private String grade;

    @TableField("apply_role")
    private String applyRole;

    @TableField("status")
    private Integer status;

    @TableField("audit_remark")
    private String auditRemark;

    @TableField("audit_time")
    private LocalDateTime auditTime;

    @TableField("auditor_id")
    private Long auditorId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}