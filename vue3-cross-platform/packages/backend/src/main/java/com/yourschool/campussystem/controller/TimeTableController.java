package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.dto.TimeTableDTO;
import com.yourschool.campussystem.service.TimeTableService;
import com.yourschool.campussystem.util.UserContextUtils;
import com.yourschool.campussystem.vo.TimeTableVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 时间表管理控制器
 */
@RestController
@RequestMapping("/api/time-table")
@Tag(name = "时间表管理", description = "时间段的增删改查、默认时间表设置等功能")
@RequiredArgsConstructor
public class TimeTableController {

    private final TimeTableService timeTableService;

    @Operation(summary = "创建时间段", description = "创建新的时间段记录")
    @PostMapping("/create")
    public ApiResponse<TimeTableVO> createTimePeriod(
            HttpServletRequest request,
            @Parameter(description = "时间段信息", required = true)
            @Valid @RequestBody TimeTableDTO timeTableDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        TimeTableVO timeTableVO = timeTableService.createTimePeriod(userId, timeTableDTO);
        return ApiResponse.success("时间段创建成功", timeTableVO);
    }

    @Operation(summary = "获取用户的时间表", description = "获取当前用户的所有时间段")
    @GetMapping("/list")
    public ApiResponse<List<TimeTableVO>> getTimeTable(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        List<TimeTableVO> timeTables = timeTableService.getTimeTableByUserId(userId);
        return ApiResponse.success("查询成功", timeTables);
    }

    @Operation(summary = "获取默认时间表", description = "获取系统默认的时间表")
    @GetMapping("/default")
    public ApiResponse<List<TimeTableVO>> getDefaultTimeTable() {
        List<TimeTableVO> timeTables = timeTableService.getDefaultTimeTable();
        return ApiResponse.success("查询成功", timeTables);
    }

    @Operation(summary = "获取时间段详情", description = "获取指定时间段的详细信息")
    @GetMapping("/detail/{id}")
    public ApiResponse<TimeTableVO> getTimePeriodDetail(
            HttpServletRequest request,
            @Parameter(description = "时间段ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        TimeTableVO timeTableVO = timeTableService.getTimePeriodDetail(userId, id);
        return ApiResponse.success("查询成功", timeTableVO);
    }

    @Operation(summary = "更新时间段", description = "更新时间段信息")
    @PutMapping("/update/{id}")
    public ApiResponse<TimeTableVO> updateTimePeriod(
            HttpServletRequest request,
            @Parameter(description = "时间段ID", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "时间段信息", required = true)
            @Valid @RequestBody TimeTableDTO timeTableDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        TimeTableVO timeTableVO = timeTableService.updateTimePeriod(userId, id, timeTableDTO);
        return ApiResponse.success("时间段更新成功", timeTableVO);
    }

    @Operation(summary = "删除时间段", description = "删除指定时间段")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteTimePeriod(
            HttpServletRequest request,
            @Parameter(description = "时间段ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        timeTableService.deleteTimePeriod(userId, id);
        return ApiResponse.success("时间段删除成功");
    }

    @Operation(summary = "设置默认时间表", description = "将指定时间段设置为默认时间表")
    @PutMapping("/set-default/{id}")
    public ApiResponse<String> setDefaultTimeTable(
            HttpServletRequest request,
            @Parameter(description = "时间段ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        timeTableService.setDefaultTimeTable(userId, id);
        return ApiResponse.success("默认时间表设置成功");
    }
}

