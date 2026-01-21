package com.yourschool.campussystem.service;

import com.yourschool.campussystem.dto.DietRecordDTO;
import com.yourschool.campussystem.vo.DietRecordVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 饮食记录服务接口
 */
public interface DietRecordService {

    /**
     * 添加饮食记录
     */
    DietRecordVO addRecord(Long userId, DietRecordDTO dto);

    /**
     * 删除饮食记录
     */
    void deleteRecord(Long userId, Long recordId);

    /**
     * 更新饮食记录
     */
    DietRecordVO updateRecord(Long userId, Long recordId, DietRecordDTO dto);

    /**
     * 查询饮食记录列表
     */
    List<DietRecordVO> getRecords(Long userId, LocalDate startDate, LocalDate endDate, 
                                  String mealType, Integer page, Integer size);

    /**
     * 获取某天的饮食记录
     */
    List<DietRecordVO> getRecordsByDate(Long userId, LocalDate date);

    /**
     * 根据ID获取单条饮食记录
     */
    DietRecordVO getRecordById(Long userId, Long recordId);

    /**
     * 自动导入E卡通食堂消费记录
     */
    void autoImportFromEcard(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取饮食统计
     */
    Object getStatistics(Long userId, LocalDate startDate, LocalDate endDate);
}

