package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.TeamInvite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 团队邀请Mapper接口
 */
@Mapper
public interface TeamInviteMapper extends BaseMapper<TeamInvite> {
}
