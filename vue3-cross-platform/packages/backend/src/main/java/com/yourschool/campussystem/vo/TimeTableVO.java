package com.yourschool.campussystem.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * 时间表信息响应
 */
@Data
@Schema(description = "时间表信息响应")
public class TimeTableVO {

    @Schema(description = "时间表ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "时间段名称（如：第一节、第二节）", example = "第一节")
    private String periodName;

    @Schema(description = "开始时间", example = "08:00:00")
    private LocalTime startTime;

    @Schema(description = "结束时间", example = "09:40:00")
    private LocalTime endTime;

    @Schema(description = "排序顺序", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否默认时间表", example = "false")
    private Boolean isDefault;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

