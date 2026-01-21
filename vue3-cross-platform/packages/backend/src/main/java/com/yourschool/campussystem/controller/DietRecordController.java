package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.dto.DietRecordDTO;
import com.yourschool.campussystem.service.DietRecordService;
import com.yourschool.campussystem.util.UserContextUtils;
import com.yourschool.campussystem.vo.DietRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 饮食记录控制器
 */
@RestController
@RequestMapping("/api/diet-record")
@Tag(name = "饮食记录管理", description = "饮食记录功能：记录饮食、自动导入、统计等")
@RequiredArgsConstructor
public class DietRecordController {

    private final DietRecordService dietRecordService;

    @Operation(summary = "添加饮食记录", description = "手动添加一条饮食记录")
    @PostMapping("/add")
    public ApiResponse<DietRecordVO> addRecord(
            HttpServletRequest request,
            @Parameter(description = "饮食信息", required = true)
            @Valid @RequestBody DietRecordDTO dto) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        DietRecordVO vo = dietRecordService.addRecord(userId, dto);
        return ApiResponse.success("添加成功", vo);
    }

    @Operation(summary = "删除饮食记录", description = "删除指定的饮食记录")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRecord(
            HttpServletRequest request,
            @Parameter(description = "记录ID", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        dietRecordService.deleteRecord(userId, id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "更新饮食记录", description = "更新指定的饮食记录")
    @PutMapping("/{id}")
    public ApiResponse<DietRecordVO> updateRecord(
            HttpServletRequest request,
            @Parameter(description = "记录ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "饮食信息", required = true)
            @Valid @RequestBody DietRecordDTO dto) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        DietRecordVO vo = dietRecordService.updateRecord(userId, id, dto);
        return ApiResponse.success("更新成功", vo);
    }

    @Operation(summary = "查询饮食记录", description = "分页查询饮食记录，支持时间范围和餐次筛选")
    @GetMapping("/list")
    public ApiResponse<List<DietRecordVO>> getRecords(
            HttpServletRequest request,
            @Parameter(description = "开始日期（yyyy-MM-dd）")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期（yyyy-MM-dd）")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "餐次")
            @RequestParam(required = false) String mealType,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        List<DietRecordVO> records = dietRecordService.getRecords(userId, startDate, endDate, mealType, page, size);
        return ApiResponse.success("查询成功", records);
    }

    @Operation(summary = "获取某天的饮食记录", description = "获取指定日期的所有饮食记录")
    @GetMapping("/by-date")
    public ApiResponse<List<DietRecordVO>> getRecordsByDate(
            HttpServletRequest request,
            @Parameter(description = "日期（yyyy-MM-dd），不传则使用今天")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        if (date == null) {
            date = LocalDate.now();
        }
        List<DietRecordVO> records = dietRecordService.getRecordsByDate(userId, date);
        return ApiResponse.success("查询成功", records);
    }

    @Operation(summary = "根据ID获取饮食记录", description = "根据记录ID获取单条饮食记录详情")
    @GetMapping("/{id}")
    public ApiResponse<DietRecordVO> getRecordById(
            HttpServletRequest request,
            @Parameter(description = "记录ID", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        DietRecordVO vo = dietRecordService.getRecordById(userId, id);
        return ApiResponse.success("查询成功", vo);
    }

    @Operation(summary = "自动导入E卡通食堂消费", description = "从E卡通食堂消费记录自动导入到饮食表")
    @PostMapping("/auto-import")
    public ApiResponse<Void> autoImport(
            HttpServletRequest request,
            @Parameter(description = "开始日期（yyyy-MM-dd）")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期（yyyy-MM-dd）")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        dietRecordService.autoImportFromEcard(userId, startDate, endDate);
        return ApiResponse.success("导入成功", null);
    }

    @Operation(summary = "获取饮食统计", description = "获取指定时间范围内的饮食统计")
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics(
            HttpServletRequest request,
            @Parameter(description = "开始日期（yyyy-MM-dd）")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期（yyyy-MM-dd）")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        Object stats = dietRecordService.getStatistics(userId, startDate, endDate);
        return ApiResponse.success("查询成功", (Map<String, Object>) stats);
    }
}

