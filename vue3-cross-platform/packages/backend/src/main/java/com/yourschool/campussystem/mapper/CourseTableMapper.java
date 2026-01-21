package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.CourseTable;
import org.apache.ibatis.annotations.Mapper;

/**
 * 课程表Mapper
 */
@Mapper
public interface CourseTableMapper extends BaseMapper<CourseTable> {
}

