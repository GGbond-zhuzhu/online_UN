package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.PersonalSchedule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 个人行程Mapper接口
 */
@Mapper
public interface PersonalScheduleMapper extends BaseMapper<PersonalSchedule> {
}
