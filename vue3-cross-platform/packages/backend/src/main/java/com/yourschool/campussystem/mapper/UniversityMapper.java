package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.University;
import org.apache.ibatis.annotations.Mapper;

/**
 * 高校Mapper接口
 */
@Mapper
public interface UniversityMapper extends BaseMapper<University> {
}
