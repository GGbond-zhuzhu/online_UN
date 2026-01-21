package com.yourschool.campussystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalTime;

/**
 * 时间表创建/更新请求参数
 */
@Data
@Schema(description = "时间表创建/更新请求参数")
public class TimeTableDTO {

    @NotBlank(message = "时间段名称不能为空")
    @Schema(description = "时间段名称（如：第一节、第二节）", example = "第一节", requiredMode = Schema.RequiredMode.REQUIRED)
    private String periodName;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间", example = "08:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间", example = "09:40:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime endTime;

    @Schema(description = "排序顺序", example = "1")
    private Integer sortOrder = 0;

    @Schema(description = "是否默认时间表", example = "false")
    private Boolean isDefault = false;
}

