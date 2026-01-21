package com.yourschool.campussystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yourschool.campussystem.entity.RouteSeriesLocation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 行程系列地点Mapper
 */
@Mapper
public interface RouteSeriesLocationMapper extends BaseMapper<RouteSeriesLocation> {
    
    /**
     * 根据行程ID查询地点列表（按排序顺序）
     */
    @Select("SELECT * FROM route_series_location WHERE schedule_id = #{scheduleId} ORDER BY sort_order ASC")
    List<RouteSeriesLocation> selectByScheduleIdOrderBySort(Long scheduleId);
}

