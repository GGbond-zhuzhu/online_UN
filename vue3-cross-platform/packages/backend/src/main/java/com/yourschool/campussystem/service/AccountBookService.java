package com.yourschool.campussystem.service;


import com.yourschool.campussystem.dto.AccountBookDTO;
import com.yourschool.campussystem.vo.AccountBookVO;
import com.yourschool.campussystem.vo.MonthlyReportVO;
import com.yourschool.campussystem.vo.WeeklyReportVO;
import com.yourschool.campussystem.vo.YearlyReportVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 记账本服务接口
 */
public interface AccountBookService {

    /**
     * 添加记账记录
     */
    AccountBookVO addRecord(Long userId, AccountBookDTO dto);

    /**
     * 删除记账记录
     */
    void deleteRecord(Long userId, Long recordId);

    /**
     * 更新记账记录
     */
    AccountBookVO updateRecord(Long userId, Long recordId, AccountBookDTO dto);

    /**
     * 查询记账记录列表
     */
    List<AccountBookVO> getRecords(Long userId, LocalDate startDate, LocalDate endDate, 
                                   String category, Integer page, Integer size);

    /**
     * 获取周报
     */
    WeeklyReportVO getWeeklyReport(Long userId, LocalDate weekStartDate);

    /**
     * 自动导入E卡通消费记录
     */
    void autoImportFromEcard(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取月报
     */
    MonthlyReportVO getMonthlyReport(Long userId, LocalDate monthStartDate);

    /**
     * 获取年报
     */
    YearlyReportVO getYearlyReport(Long userId, LocalDate yearStartDate);

    /**
     * 获取统计信息
     */
    Object getStatistics(Long userId, LocalDate startDate, LocalDate endDate);
}

