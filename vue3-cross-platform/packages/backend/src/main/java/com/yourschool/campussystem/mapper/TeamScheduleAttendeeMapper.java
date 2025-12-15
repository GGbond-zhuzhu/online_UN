package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.TeamScheduleAttendee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 团队行程参与者Mapper接口
 */
@Mapper
public interface TeamScheduleAttendeeMapper extends BaseMapper<TeamScheduleAttendee> {
}
