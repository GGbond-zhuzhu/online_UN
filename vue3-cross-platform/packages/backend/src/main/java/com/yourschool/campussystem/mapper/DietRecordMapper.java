package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.DietRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 饮食记录Mapper
 */
@Mapper
public interface DietRecordMapper extends BaseMapper<DietRecord> {
}

