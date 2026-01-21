package com.yourschool.campussystem.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 年报VO（与前端 YearlyReport 对齐）
 */
@Data
public class YearlyReportVO {
    private String yearRange; // 年范围，如 "2026-01-01 ~ 2026-12-31"
    private BigDecimal totalAmount;
    private Integer totalCount;
    private BigDecimal avgMonthlyAmount; // 月均消费
    private List<CategoryStatVO> categoryStats;
    private List<LocationStatVO> locationStats;
    private List<MonthlyStatVO> monthlyStats;
    private ComparisonVO comparison; // 与去年对比

    @Data
    public static class ComparisonVO {
        private BigDecimal lastYearAmount;
        private Integer lastYearCount;
        private BigDecimal amountChange;
        private Double amountChangePercent;
    }

    @Data
    public static class MonthlyStatVO {
        private String month;
        private BigDecimal amount;
        private Integer count;
    }

    @Data
    public static class CategoryStatVO {
        private String category;
        private BigDecimal amount;
        private Integer count;
        private Double percentage;
    }

    @Data
    public static class LocationStatVO {
        private String location;
        private BigDecimal amount;
        private Integer count;
        private Double percentage;
    }
}

