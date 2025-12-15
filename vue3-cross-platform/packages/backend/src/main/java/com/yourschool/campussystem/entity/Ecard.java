package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yourschool.campussystem.enums.CardStatusEnum;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("ecard")
public class Ecard {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("card_no")
    private String cardNo;

    @TableField("user_id")
    private Long userId;

    @TableField("balance")
    private BigDecimal balance;

    @TableField("status")
    private CardStatusEnum status;

    @TableField("is_visitor_card")
    private Boolean isVisitorCard = false;

    @TableField("visitor_expire_time")
    private LocalDateTime visitorExpireTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}