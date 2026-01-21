package com.yourschool.campussystem.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * 课程表信息响应
 */
@Data
@Schema(description = "课程表信息响应")
public class CourseTableVO {

    @Schema(description = "课程ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "课程名称", example = "高等数学")
    private String courseName;

    @Schema(description = "星期（周一、周二...周日）", example = "周一")
    private String dayOfWeek;

    @Schema(description = "开始时间", example = "08:00:00")
    private LocalTime startTime;

    @Schema(description = "结束时间", example = "10:00:00")
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
    private String source;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

