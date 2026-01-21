package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 行程系列路线结果实体类
 */
@Data
@TableName("route_series_route")
public class RouteSeriesRoute {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("schedule_id")
    private Long scheduleId;

    @TableField("total_distance")
    private BigDecimal totalDistance;

    @TableField("total_duration")
    private Integer totalDuration;

    @TableField("route_data")
    private String routeData; // JSON格式存储路线详细数据

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}

