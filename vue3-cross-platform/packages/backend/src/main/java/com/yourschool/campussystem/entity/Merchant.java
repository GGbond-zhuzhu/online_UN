package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户实体类
 */
@Data
@TableName("merchant")
public class Merchant {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("merchant_name")
    private String merchantName;

    @TableField("merchant_type")
    private String merchantType;  // COMPANY/INDIVIDUAL

    @TableField("business_scope")
    private String businessScope;

    @TableField("credit_code")
    private String creditCode;

    @TableField("contact_name")
    private String contactName;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("contact_email")
    private String contactEmail;

    @TableField("business_license_url")
    private String businessLicenseUrl;

    @TableField("legal_id_card_url")
    private String legalIdCardUrl;

    @TableField("status")
    private String status;  // PENDING/APPROVED/REJECTED/SUSPENDED

    @TableField("deposit_amount")
    private BigDecimal depositAmount;

    @TableField("credit_score")
    private Integer creditScore;

    @TableField("approved_time")
    private LocalDateTime approvedTime;

    @TableField("approver_id")
    private Long approverId;

    @TableField("reject_reason")
    private String rejectReason;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted = 0;
}
