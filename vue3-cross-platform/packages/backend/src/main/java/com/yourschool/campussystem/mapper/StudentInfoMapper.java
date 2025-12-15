package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.StudentInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学生信息Mapper接口
 */
@Mapper
public interface StudentInfoMapper extends BaseMapper<StudentInfo> {
}
