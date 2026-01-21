package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.TimeTable;
import org.apache.ibatis.annotations.Mapper;

/**
 * 时间表Mapper
 */
@Mapper
public interface TimeTableMapper extends BaseMapper<TimeTable> {
}

