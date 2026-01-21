package com.yourschool.campussystem.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 月报VO（与前端 MonthlyReport 对齐）
 */
@Data
public class MonthlyReportVO {
    private String monthRange; // 月范围，如 "2026-01-01 ~ 2026-01-31"
    private BigDecimal totalAmount; // 总消费金额
    private Integer totalCount; // 总消费次数
    private BigDecimal avgDailyAmount; // 日均消费
    private List<CategoryStatVO> categoryStats; // 分类统计
    private List<LocationStatVO> locationStats; // 地点统计
    private List<DailyStatVO> dailyStats; // 每日统计
    private ComparisonVO comparison; // 与上月对比

    @Data
    public static class ComparisonVO {
        private BigDecimal lastMonthAmount;
        private Integer lastMonthCount;
        private BigDecimal amountChange;
        private Double amountChangePercent;
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

    @Data
    public static class DailyStatVO {
        private String date;
        private BigDecimal amount;
        private Integer count;
    }
}

