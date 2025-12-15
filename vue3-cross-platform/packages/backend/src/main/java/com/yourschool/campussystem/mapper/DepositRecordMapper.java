package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.DepositRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 保证金记录Mapper接口
 */
@Mapper
public interface DepositRecordMapper extends BaseMapper<DepositRecord> {
}
