package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.dto.AccountBookDTO;
import com.yourschool.campussystem.service.AccountBookService;
import com.yourschool.campussystem.util.UserContextUtils;
import com.yourschool.campussystem.vo.AccountBookVO;
import com.yourschool.campussystem.vo.MonthlyReportVO;
import com.yourschool.campussystem.vo.WeeklyReportVO;
import com.yourschool.campussystem.vo.YearlyReportVO;
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
 * 记账本控制器
 */
@RestController
@RequestMapping("/api/account-book")
@Tag(name = "记账本管理", description = "记账本功能：记录消费、自动导入、统计报表等")
@RequiredArgsConstructor
public class AccountBookController {

    private final AccountBookService accountBookService;

    @Operation(summary = "添加记账记录", description = "手动添加一条记账记录")
    @PostMapping("/add")
    public ApiResponse<AccountBookVO> addRecord(
            HttpServletRequest request,
            @Parameter(description = "记账信息", required = true)
            @Valid @RequestBody AccountBookDTO dto) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        AccountBookVO vo = accountBookService.addRecord(userId, dto);
        return ApiResponse.success("添加成功", vo);
    }

    @Operation(summary = "删除记账记录", description = "删除指定的记账记录")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRecord(
            HttpServletRequest request,
            @Parameter(description = "记录ID", required = true) @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        accountBookService.deleteRecord(userId, id);
        return ApiResponse.success("删除成功", null);
    }

    @Operation(summary = "更新记账记录", description = "更新指定的记账记录")
    @PutMapping("/{id}")
    public ApiResponse<AccountBookVO> updateRecord(
            HttpServletRequest request,
            @Parameter(description = "记录ID", required = true) @PathVariable Long id,
            @Parameter(description = "记账信息", required = true) @Valid @RequestBody AccountBookDTO dto) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        AccountBookVO vo = accountBookService.updateRecord(userId, id, dto);
        return ApiResponse.success("更新成功", vo);
    }

    @Operation(summary = "查询记账记录列表", description = "分页查询记账记录，支持时间范围和分类筛选")
    @GetMapping("/list")
    public ApiResponse<List<AccountBookVO>> getRecords(
            HttpServletRequest request,
            @Parameter(description = "开始日期（yyyy-MM-dd）")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期（yyyy-MM-dd）")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(description = "分类（餐饮/交通/购物...）")
            @RequestParam(required = false) String category,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        List<AccountBookVO> records = accountBookService.getRecords(userId, startDate, endDate, category, page, size);
        return ApiResponse.success("查询成功", records);
    }

    @Operation(summary = "获取周报", description = "获取指定周的消费周报，不传则默认本周")
    @GetMapping("/weekly-report")
    public ApiResponse<WeeklyReportVO> getWeeklyReport(
            HttpServletRequest request,
            @Parameter(description = "周起始日期（yyyy-MM-dd），建议周一")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStartDate) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        WeeklyReportVO report = accountBookService.getWeeklyReport(userId, weekStartDate);
        return ApiResponse.success("查询成功", report);
    }

    @Operation(summary = "获取月报", description = "获取指定月份消费月报，不传则默认本月")
    @GetMapping("/monthly-report")
    public ApiResponse<MonthlyReportVO> getMonthlyReport(
            HttpServletRequest request,
            @Parameter(description = "月份任意日期（yyyy-MM-dd），用于定位到该月")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate monthDate) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        MonthlyReportVO report = accountBookService.getMonthlyReport(userId, monthDate);
        return ApiResponse.success("查询成功", report);
    }

    @Operation(summary = "获取年报", description = "获取指定年份消费年报，不传则默认本年")
    @GetMapping("/yearly-report")
    public ApiResponse<YearlyReportVO> getYearlyReport(
            HttpServletRequest request,
            @Parameter(description = "年份任意日期（yyyy-MM-dd），用于定位到该年")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate yearDate) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        YearlyReportVO report = accountBookService.getYearlyReport(userId, yearDate);
        return ApiResponse.success("查询成功", report);
    }

    @Operation(summary = "自动导入E卡通消费", description = "从E卡通消费记录自动导入到记账本")
    @PostMapping("/auto-import")
    public ApiResponse<Void> autoImport(
            HttpServletRequest request,
            @Parameter(description = "开始日期（yyyy-MM-dd）")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期（yyyy-MM-dd）")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        accountBookService.autoImportFromEcard(userId, startDate, endDate);
        return ApiResponse.success("导入成功", null);
    }

    @Operation(summary = "获取统计信息", description = "获取指定时间范围内的消费统计，不传则默认近30天")
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
        Object stats = accountBookService.getStatistics(userId, startDate, endDate);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) stats;
        return ApiResponse.success("查询成功", data);
    }
}

