package com.yourschool.campussystem.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 记账本VO
 */
@Data
public class AccountBookVO {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String category;
    private String description;
    private LocalDate consumeDate;
    private Boolean isAutoImport;
    private String location;
    private String payMethod;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

