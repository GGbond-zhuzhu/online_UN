package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yourschool.campussystem.enums.ConsumeTypeEnum;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 消费记录实体类
 */
@Data
@TableName("consume_record")
public class ConsumeRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("card_no")
    private String cardNo;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("balance_after")
    private BigDecimal balanceAfter;

    @TableField("merchant_id")
    private String merchantId;

    @TableField("merchant_name")
    private String merchantName;

    @TableField("consume_type")
    private ConsumeTypeEnum consumeType;

    @TableField("description")
    private String description;

    @TableField("longitude")
    private BigDecimal longitude;

    @TableField("latitude")
    private BigDecimal latitude;

    @TableField("is_in_campus")
    private Boolean isInCampus = true;

    @TableField("pay_method")
    private String payMethod = "CARD";

    @TableField(value = "consume_time", fill = FieldFill.INSERT)
    private LocalDateTime consumeTime;
}
