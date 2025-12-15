package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.Team;
import org.apache.ibatis.annotations.Mapper;

/**
 * 团队Mapper接口
 */
@Mapper
public interface TeamMapper extends BaseMapper<Team> {
}
