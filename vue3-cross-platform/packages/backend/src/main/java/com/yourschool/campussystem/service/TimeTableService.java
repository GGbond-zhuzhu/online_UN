package com.yourschool.campussystem.service;

import com.yourschool.campussystem.dto.TimeTableDTO;
import com.yourschool.campussystem.vo.TimeTableVO;

import java.util.List;

/**
 * 时间表服务接口
 */
public interface TimeTableService {

    /**
     * 创建时间段
     */
    TimeTableVO createTimePeriod(Long userId, TimeTableDTO timeTableDTO);

    /**
     * 获取用户的时间表
     */
    List<TimeTableVO> getTimeTableByUserId(Long userId);

    /**
     * 获取默认时间表
     */
    List<TimeTableVO> getDefaultTimeTable();

    /**
     * 获取时间段详情
     */
    TimeTableVO getTimePeriodDetail(Long userId, Long timePeriodId);

    /**
     * 更新时间段
     */
    TimeTableVO updateTimePeriod(Long userId, Long timePeriodId, TimeTableDTO timeTableDTO);

    /**
     * 删除时间段
     */
    void deleteTimePeriod(Long userId, Long timePeriodId);

    /**
     * 设置默认时间表
     */
    void setDefaultTimeTable(Long userId, Long timePeriodId);
}

