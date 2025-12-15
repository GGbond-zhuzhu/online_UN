package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.BrowseHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 浏览记录Mapper接口
 */
@Mapper
public interface BrowseHistoryMapper extends BaseMapper<BrowseHistory> {
}
