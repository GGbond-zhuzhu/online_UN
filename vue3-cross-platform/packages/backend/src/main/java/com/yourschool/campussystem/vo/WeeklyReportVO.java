package com.yourschool.campussystem.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 周报VO
 */
@Data
public class WeeklyReportVO {
    private String weekRange; // 周范围，如 "2024-01-01 ~ 2024-01-07"
    private BigDecimal totalAmount; // 总消费金额
    private Integer totalCount; // 总消费次数
    private BigDecimal avgDailyAmount; // 日均消费
    private List<CategoryStatVO> categoryStats; // 分类统计
    private List<LocationStatVO> locationStats; // 地点统计
    private List<DailyStatVO> dailyStats; // 每日统计

    @Data
    public static class CategoryStatVO {
        private String category;
        private BigDecimal amount;
        private Integer count;
        private Double percentage; // 占比百分比
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

