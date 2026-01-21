package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.dto.TimeTableDTO;
import com.yourschool.campussystem.entity.TimeTable;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.TimeTableMapper;
import com.yourschool.campussystem.service.TimeTableService;
import com.yourschool.campussystem.vo.TimeTableVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 时间表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TimeTableServiceImpl extends ServiceImpl<TimeTableMapper, TimeTable> implements TimeTableService {

    private final TimeTableMapper timeTableMapper;

    @Override
    @Transactional
    public TimeTableVO createTimePeriod(Long userId, TimeTableDTO timeTableDTO) {
        TimeTable timeTable = new TimeTable();
        BeanUtils.copyProperties(timeTableDTO, timeTable);
        timeTable.setUserId(userId);
        
        // 如果设置为默认，先取消其他默认设置
        if (Boolean.TRUE.equals(timeTableDTO.getIsDefault())) {
            LambdaUpdateWrapper<TimeTable> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(TimeTable::getUserId, userId)
                    .set(TimeTable::getIsDefault, false);
            timeTableMapper.update(null, updateWrapper);
        }
        
        timeTableMapper.insert(timeTable);
        return convertToVO(timeTable);
    }

    @Override
    public List<TimeTableVO> getTimeTableByUserId(Long userId) {
        LambdaQueryWrapper<TimeTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimeTable::getUserId, userId)
                .eq(TimeTable::getIsDeleted, 0)
                .orderByAsc(TimeTable::getSortOrder)
                .orderByAsc(TimeTable::getStartTime);
        
        List<TimeTable> timeTables = timeTableMapper.selectList(queryWrapper);
        return timeTables.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<TimeTableVO> getDefaultTimeTable() {
        LambdaQueryWrapper<TimeTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TimeTable::getIsDefault, true)
                .eq(TimeTable::getIsDeleted, 0)
                .orderByAsc(TimeTable::getSortOrder)
                .orderByAsc(TimeTable::getStartTime);
        
        List<TimeTable> timeTables = timeTableMapper.selectList(queryWrapper);
        return timeTables.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public TimeTableVO getTimePeriodDetail(Long userId, Long timePeriodId) {
        TimeTable timeTable = timeTableMapper.selectOne(
                new LambdaQueryWrapper<TimeTable>()
                        .eq(TimeTable::getId, timePeriodId)
                        .eq(TimeTable::getUserId, userId)
                        .eq(TimeTable::getIsDeleted, 0)
        );
        
        if (timeTable == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "时间段不存在");
        }
        
        return convertToVO(timeTable);
    }

    @Override
    @Transactional
    public TimeTableVO updateTimePeriod(Long userId, Long timePeriodId, TimeTableDTO timeTableDTO) {
        TimeTable timeTable = timeTableMapper.selectOne(
                new LambdaQueryWrapper<TimeTable>()
                        .eq(TimeTable::getId, timePeriodId)
                        .eq(TimeTable::getUserId, userId)
                        .eq(TimeTable::getIsDeleted, 0)
        );
        
        if (timeTable == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "时间段不存在");
        }
        
        // 如果设置为默认，先取消其他默认设置
        if (Boolean.TRUE.equals(timeTableDTO.getIsDefault())) {
            LambdaUpdateWrapper<TimeTable> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(TimeTable::getUserId, userId)
                    .ne(TimeTable::getId, timePeriodId)
                    .set(TimeTable::getIsDefault, false);
            timeTableMapper.update(null, updateWrapper);
        }
        
        BeanUtils.copyProperties(timeTableDTO, timeTable);
        timeTableMapper.updateById(timeTable);
        
        return convertToVO(timeTable);
    }

    @Override
    @Transactional
    public void deleteTimePeriod(Long userId, Long timePeriodId) {
        TimeTable timeTable = timeTableMapper.selectOne(
                new LambdaQueryWrapper<TimeTable>()
                        .eq(TimeTable::getId, timePeriodId)
                        .eq(TimeTable::getUserId, userId)
                        .eq(TimeTable::getIsDeleted, 0)
        );
        
        if (timeTable == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "时间段不存在");
        }
        
        timeTableMapper.deleteById(timePeriodId);
    }

    @Override
    @Transactional
    public void setDefaultTimeTable(Long userId, Long timePeriodId) {
        TimeTable timeTable = timeTableMapper.selectOne(
                new LambdaQueryWrapper<TimeTable>()
                        .eq(TimeTable::getId, timePeriodId)
                        .eq(TimeTable::getUserId, userId)
                        .eq(TimeTable::getIsDeleted, 0)
        );
        
        if (timeTable == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "时间段不存在");
        }
        
        // 先取消其他默认设置
        LambdaUpdateWrapper<TimeTable> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TimeTable::getUserId, userId)
                .set(TimeTable::getIsDefault, false);
        timeTableMapper.update(null, updateWrapper);
        
        // 设置当前为默认
        timeTable.setIsDefault(true);
        timeTableMapper.updateById(timeTable);
    }

    /**
     * 转换为VO
     */
    private TimeTableVO convertToVO(TimeTable timeTable) {
        TimeTableVO vo = new TimeTableVO();
        BeanUtils.copyProperties(timeTable, vo);
        return vo;
    }
}

