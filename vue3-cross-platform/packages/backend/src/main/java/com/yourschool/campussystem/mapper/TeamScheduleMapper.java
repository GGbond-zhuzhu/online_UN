package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.TeamSchedule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 团队行程Mapper接口
 */
@Mapper
public interface TeamScheduleMapper extends BaseMapper<TeamSchedule> {
}
