package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.dto.MapSearchDTO;
import com.yourschool.campussystem.dto.RoutePlanDTO;
import com.yourschool.campussystem.service.MapService;
import com.yourschool.campussystem.util.UserContextUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 地图服务Controller
 * 提供POI搜索、路径规划、逆地理编码等功能
 */
@RestController
@RequestMapping("/api/map")
@Tag(name = "地图服务", description = "高德地图API集成，提供POI搜索、路径规划、定位等功能")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @Operation(summary = "POI搜索", description = "搜索地点（POI），支持关键词搜索")
    @PostMapping("/search")
    public ApiResponse<List<Map<String, Object>>> searchPOI(
            HttpServletRequest request,
            @Parameter(description = "搜索参数", required = true)
            @Valid @RequestBody MapSearchDTO searchDTO) {
        UserContextUtils.getUserIdRequired(request); // 验证登录
        List<Map<String, Object>> results = mapService.searchPOI(searchDTO);
        return ApiResponse.success("搜索成功", results);
    }

    @Operation(summary = "路径规划", description = "规划多个地点之间的最优路线")
    @PostMapping("/route/plan")
    public ApiResponse<Map<String, Object>> planRoute(
            HttpServletRequest request,
            @Parameter(description = "路径规划参数", required = true)
            @Valid @RequestBody RoutePlanDTO planDTO) {
        UserContextUtils.getUserIdRequired(request); // 验证登录
        Map<String, Object> result = mapService.planRoute(planDTO);
        return ApiResponse.success("路径规划成功", result);
    }

    @Operation(summary = "逆地理编码", description = "根据经纬度获取地址信息")
    @GetMapping("/reverse-geocode")
    public ApiResponse<Map<String, Object>> reverseGeocode(
            HttpServletRequest request,
            @Parameter(description = "经度", example = "116.397128", required = true)
            @RequestParam Double longitude,
            @Parameter(description = "纬度", example = "39.916527", required = true)
            @RequestParam Double latitude) {
        UserContextUtils.getUserIdRequired(request); // 验证登录
        Map<String, Object> result = mapService.reverseGeocode(longitude, latitude);
        return ApiResponse.success("逆地理编码成功", result);
    }
}

