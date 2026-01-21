package com.yourschool.campussystem.service;

import com.yourschool.campussystem.dto.MapSearchDTO;
import com.yourschool.campussystem.dto.RoutePlanDTO;

import java.util.List;
import java.util.Map;

/**
 * 地图服务接口
 */
public interface MapService {

    /**
     * POI搜索（地点搜索）
     */
    List<Map<String, Object>> searchPOI(MapSearchDTO searchDTO);

    /**
     * 路径规划
     */
    Map<String, Object> planRoute(RoutePlanDTO planDTO);

    /**
     * 逆地理编码（根据坐标获取地址）
     */
    Map<String, Object> reverseGeocode(Double longitude, Double latitude);
}

