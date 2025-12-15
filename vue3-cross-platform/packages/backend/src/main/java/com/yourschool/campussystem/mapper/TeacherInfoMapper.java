package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.TeacherInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 教师信息Mapper接口
 */
@Mapper
public interface TeacherInfoMapper extends BaseMapper<TeacherInfo> {
}
