package com.yourschool.campussystem.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 饮食记录VO
 */
@Data
public class DietRecordVO {
    private Long id;
    private LocalDate dietDate;
    private String mealType;
    private String foodName;
    private String foodDetail;
    private String location;
    private String source;
    private Boolean isAutoImport;
    private String backgroundColor;
    private Integer calories;
    private BigDecimal price;
    private LocalDateTime createTime;
}

