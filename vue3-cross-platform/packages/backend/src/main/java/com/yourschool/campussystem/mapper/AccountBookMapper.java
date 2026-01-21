package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.AccountBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * 记账本Mapper
 */
@Mapper
public interface AccountBookMapper extends BaseMapper<AccountBook> {
}

