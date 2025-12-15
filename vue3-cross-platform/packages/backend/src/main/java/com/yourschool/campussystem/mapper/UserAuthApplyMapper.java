package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.UserAuthApply;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户认证申请Mapper
 */
@Mapper
public interface UserAuthApplyMapper extends BaseMapper<UserAuthApply> {
}
