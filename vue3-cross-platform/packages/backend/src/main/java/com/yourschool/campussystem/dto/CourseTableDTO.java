package com.yourschool.campussystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalTime;

/**
 * 课程表创建/更新请求参数
 */
@Data
@Schema(description = "课程表创建/更新请求参数")
public class CourseTableDTO {

    @NotBlank(message = "课程名称不能为空")
    @Schema(description = "课程名称", example = "高等数学", requiredMode = Schema.RequiredMode.REQUIRED)
    private String courseName;

    @NotBlank(message = "星期不能为空")
    @Schema(description = "星期（周一、周二...周日）", example = "周一", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dayOfWeek;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间", example = "08:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间", example = "10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime endTime;

    @Schema(description = "上课地点", example = "教学楼A301")
    private String location;

    @Schema(description = "授课教师", example = "张教授")
    private String teacher;

    @Schema(description = "周次范围（如：1-16周）", example = "1-16周")
    private String weekRange;

    @Schema(description = "背景颜色", example = "#B3E5FC")
    private String backgroundColor;

    @Schema(description = "来源：AUTO（自动导入）、MANUAL（手动添加）", example = "MANUAL")
    private String source = "MANUAL";
}

