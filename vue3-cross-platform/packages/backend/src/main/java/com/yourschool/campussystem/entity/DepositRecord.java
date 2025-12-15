package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 保证金记录实体类
 */
@Data
@TableName("deposit_record")
public class DepositRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("merchant_id")
    private Long merchantId;

    @TableField("order_id")
    private String orderId;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("pay_method")
    private String payMethod;  // ALIPAY/WECHAT/BANK

    @TableField("pay_status")
    private String payStatus;  // PENDING/PAID/FAILED/REFUNDED

    @TableField("pay_time")
    private LocalDateTime payTime;

    @TableField("refund_time")
    private LocalDateTime refundTime;

    @TableField("refund_reason")
    private String refundReason;

    @TableField("transaction_id")
    private String transactionId;

    @TableField("remark")
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
