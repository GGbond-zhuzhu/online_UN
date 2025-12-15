package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.ConsumeRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消费记录Mapper接口
 */
@Mapper
public interface ConsumeRecordMapper extends BaseMapper<ConsumeRecord> {
}
