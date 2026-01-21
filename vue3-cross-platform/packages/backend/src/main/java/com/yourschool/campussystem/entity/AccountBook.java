package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 记账本实体类
 */
@Data
@TableName("account_book")
public class AccountBook {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("category")
    private String category; // 消费分类：餐饮、交通、购物、娱乐、学习、其他

    @TableField("description")
    private String description; // 消费描述

    @TableField("consume_date")
    private LocalDate consumeDate; // 消费日期

    @TableField("is_auto_import")
    private Boolean isAutoImport = false; // 是否自动导入（来自E卡通）

    @TableField("ecard_record_id")
    private Long ecardRecordId; // 关联的E卡通消费记录ID（如果是自动导入）

    @TableField("location")
    private String location; // 消费地点

    @TableField("pay_method")
    private String payMethod; // 支付方式：CARD（校园卡）、CASH（现金）、ALIPAY（支付宝）、WECHAT（微信）、OTHER（其他）

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

