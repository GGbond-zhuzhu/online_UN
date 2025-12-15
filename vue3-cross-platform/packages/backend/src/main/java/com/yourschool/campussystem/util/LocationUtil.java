package com.yourschool.campussystem.util;

import java.math.BigDecimal;

/**
 * 定位工具类
 * 提供定位相关的工具方法
 */
public class LocationUtil {

    // 地球半径（米）
    private static final double EARTH_RADIUS = 6371000.0;

    /**
     * 使用Haversine公式计算两点间距离（米）
     * 
     * @param lat1 纬度1
     * @param lon1 经度1
     * @param lat2 纬度2
     * @param lon2 经度2
     * @return 距离（米）
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * 使用Haversine公式计算两点间距离（米）- BigDecimal版本
     */
    public static double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return Double.MAX_VALUE;
        }
        return calculateDistance(lat1.doubleValue(), lon1.doubleValue(), 
                                lat2.doubleValue(), lon2.doubleValue());
    }

    /**
     * 判断点是否在指定半径范围内
     * 
     * @param centerLat 中心点纬度
     * @param centerLon 中心点经度
     * @param pointLat 目标点纬度
     * @param pointLon 目标点经度
     * @param radius 半径（米）
     * @return 是否在范围内
     */
    public static boolean isWithinRadius(double centerLat, double centerLon, 
                                       double pointLat, double pointLon, double radius) {
        double distance = calculateDistance(centerLat, centerLon, pointLat, pointLon);
        return distance <= radius;
    }

    /**
     * 判断点是否在指定半径范围内 - BigDecimal版本
     */
    public static boolean isWithinRadius(BigDecimal centerLat, BigDecimal centerLon,
                                       BigDecimal pointLat, BigDecimal pointLon, int radius) {
        if (centerLat == null || centerLon == null || pointLat == null || pointLon == null) {
            return false;
        }
        double distance = calculateDistance(centerLat, centerLon, pointLat, pointLon);
        return distance <= radius;
    }

    /**
     * 格式化距离显示
     * 
     * @param distanceInMeters 距离（米）
     * @return 格式化后的距离字符串
     */
    public static String formatDistance(double distanceInMeters) {
        if (distanceInMeters < 1000) {
            return String.format("%.0f米", distanceInMeters);
        } else {
            return String.format("%.2f公里", distanceInMeters / 1000.0);
        }
    }
}
