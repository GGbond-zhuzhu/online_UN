package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.TeamMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * 团队成员Mapper接口
 */
@Mapper
public interface TeamMemberMapper extends BaseMapper<TeamMember> {
}
