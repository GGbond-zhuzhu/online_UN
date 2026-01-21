package com.yourschool.campussystem.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 记账本DTO
 */
@Data
public class AccountBookDTO {

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    @NotNull(message = "消费分类不能为空")
    private String category; // 餐饮、交通、购物、娱乐、学习、其他

    private String description; // 消费描述

    private LocalDate consumeDate; // 消费日期，不传则使用当前日期

    private String location; // 消费地点

    private String payMethod; // 支付方式：CARD、CASH、ALIPAY、WECHAT、OTHER
}

