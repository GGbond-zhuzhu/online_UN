package com.yourschool.campussystem.service.impl;

import com.yourschool.campussystem.dto.MapSearchDTO;
import com.yourschool.campussystem.dto.RoutePlanDTO;
import com.yourschool.campussystem.service.MapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 地图服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {

    private final RestTemplate restTemplate;

    @Value("${amap.web-service.key}")
    private String amapKey;

    // 高德地图API基础URL
    private static final String AMAP_BASE_URL = "https://restapi.amap.com/v3";

    @Override
    public List<Map<String, Object>> searchPOI(MapSearchDTO searchDTO) {
        try {
            // 构建请求URL
            String url = AMAP_BASE_URL + "/place/text?key={key}&keywords={keywords}&city={city}&output=JSON&offset={offset}&page={page}";
            
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapKey);
            params.put("keywords", searchDTO.getKeyword());
            params.put("city", searchDTO.getCity() != null ? searchDTO.getCity() : "");
            params.put("offset", searchDTO.getPageSize());
            params.put("page", searchDTO.getPage());

            // 调用高德地图API
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class, params);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null || !"1".equals(String.valueOf(responseBody.get("status")))) {
                log.error("高德地图POI搜索失败: {}", responseBody);
                return Collections.emptyList();
            }

            // 解析返回结果
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> pois = (List<Map<String, Object>>) responseBody.get("pois");
            if (pois == null) {
                return Collections.emptyList();
            }

            // 转换为标准格式
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map<String, Object> poi : pois) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", poi.get("name"));
                String address = (String) poi.get("address");
                if (address == null || address.isEmpty()) {
                    String pname = poi.get("pname") != null ? String.valueOf(poi.get("pname")) : "";
                    String cityname = poi.get("cityname") != null ? String.valueOf(poi.get("cityname")) : "";
                    String adname = poi.get("adname") != null ? String.valueOf(poi.get("adname")) : "";
                    address = pname + cityname + adname;
                }
                item.put("address", address);
                
                // 解析经纬度
                String location = (String) poi.get("location");
                if (location != null && location.contains(",")) {
                    String[] coords = location.split(",");
                    item.put("longitude", new BigDecimal(coords[0]));
                    item.put("latitude", new BigDecimal(coords[1]));
                }
                
                result.add(item);
            }

            return result;
        } catch (Exception e) {
            log.error("POI搜索异常", e);
            return Collections.emptyList();
        }
    }

    @Override
    public Map<String, Object> planRoute(RoutePlanDTO planDTO) {
        try {
            List<RoutePlanDTO.PointDTO> points = planDTO.getPoints();
            if (points == null || points.size() < 2) {
                throw new IllegalArgumentException("至少需要2个地点");
            }

            // 构建起点、终点和途经点
            RoutePlanDTO.PointDTO origin = points.get(0);
            RoutePlanDTO.PointDTO destination = points.get(points.size() - 1);
            
            String originStr = origin.getLongitude() + "," + origin.getLatitude();
            String destinationStr = destination.getLongitude() + "," + destination.getLatitude();
            
            // 构建途经点（如果有超过2个地点）
            String waypoints = "";
            if (points.size() > 2) {
                List<String> waypointList = new ArrayList<>();
                for (int i = 1; i < points.size() - 1; i++) {
                    RoutePlanDTO.PointDTO point = points.get(i);
                    waypointList.add(point.getLongitude() + "," + point.getLatitude());
                }
                waypoints = String.join("|", waypointList);
            }

            // 构建请求URL
            String url = AMAP_BASE_URL + "/direction/driving?key={key}&origin={origin}&destination={destination}&strategy={strategy}&output=JSON";
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapKey);
            params.put("origin", originStr);
            params.put("destination", destinationStr);
            params.put("strategy", planDTO.getStrategy() != null ? planDTO.getStrategy() : 0);
            
            // 如果有途经点，添加到参数中
            if (!waypoints.isEmpty()) {
                url += "&waypoints={waypoints}";
                params.put("waypoints", waypoints);
            }

            // 调用高德地图API
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class, params);
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null || !"1".equals(String.valueOf(responseBody.get("status")))) {
                log.error("高德地图路径规划失败: {}", responseBody);
                String errorInfo = responseBody != null ? String.valueOf(responseBody.get("info")) : "未知错误";
                throw new RuntimeException("路径规划失败: " + errorInfo);
            }

            // 解析返回结果
            @SuppressWarnings("unchecked")
            Map<String, Object> route = (Map<String, Object>) responseBody.get("route");
            if (route == null) {
                throw new RuntimeException("路径规划结果为空");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> paths = (List<Map<String, Object>>) route.get("paths");
            if (paths == null || paths.isEmpty()) {
                throw new RuntimeException("未找到可行路径");
            }

            Map<String, Object> path = paths.get(0);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("totalDistance", new BigDecimal(String.valueOf(path.get("distance"))).divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP));
            result.put("totalDuration", Integer.parseInt(String.valueOf(path.get("duration"))) / 60); // 转换为分钟
            
            // 解析路线步骤
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> steps = (List<Map<String, Object>>) path.get("steps");
            List<Map<String, Object>> stepList = new ArrayList<>();
            if (steps != null) {
                for (Map<String, Object> step : steps) {
                    Map<String, Object> stepItem = new HashMap<>();
                    stepItem.put("instruction", step.get("instruction"));
                    if (step.get("distance") != null) {
                        stepItem.put("distance", new BigDecimal(String.valueOf(step.get("distance"))).divide(new BigDecimal("1000"), 2, RoundingMode.HALF_UP));
                    }
                    stepList.add(stepItem);
                }
            }
            result.put("steps", stepList);

            return result;
        } catch (Exception e) {
            log.error("路径规划异常", e);
            throw new RuntimeException("路径规划失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> reverseGeocode(Double longitude, Double latitude) {
        try {
            String url = AMAP_BASE_URL + "/geocode/regeo?key={key}&location={location}&output=JSON";
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapKey);
            params.put("location", longitude + "," + latitude);

            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class, params);
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null || !"1".equals(String.valueOf(responseBody.get("status")))) {
                log.error("高德地图逆地理编码失败: {}", responseBody);
                return Collections.emptyMap();
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> regeocode = (Map<String, Object>) responseBody.get("regeocode");
            if (regeocode == null) {
                return Collections.emptyMap();
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> addressComponent = (Map<String, Object>) regeocode.get("addressComponent");
            Map<String, Object> result = new HashMap<>();
            result.put("formattedAddress", regeocode.get("formatted_address"));
            if (addressComponent != null) {
                result.put("province", addressComponent.get("province"));
                result.put("city", addressComponent.get("city"));
                result.put("district", addressComponent.get("district"));
                result.put("street", addressComponent.get("street"));
            }

            return result;
        } catch (Exception e) {
            log.error("逆地理编码异常", e);
            return Collections.emptyMap();
        }
    }
}

