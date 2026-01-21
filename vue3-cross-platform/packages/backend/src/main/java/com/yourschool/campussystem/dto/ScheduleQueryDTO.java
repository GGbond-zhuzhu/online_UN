package com.yourschool.campussystem.dto;

import com.yourschool.campussystem.enums.ScheduleStatusEnum;
import com.yourschool.campussystem.enums.ScheduleTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "行程查询参数")
public class ScheduleQueryDTO {

    @Schema(description = "开始日期（yyyy-MM-dd）", example = "2024-03-01")
    private LocalDate startDate;

    @Schema(description = "结束日期（yyyy-MM-dd）", example = "2024-03-31")
    private LocalDate endDate;

    @Schema(description = "行程类型", example = "CLASS")
    private ScheduleTypeEnum type;

    @Schema(description = "行程状态", example = "PENDING")
    private ScheduleStatusEnum status;

    @Schema(description = "关键词", example = "会议")
    private String keyword;

    @Schema(description = "是否团队行程", example = "false")
    private Boolean isTeamSchedule;

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    /**
     * 兼容前端参数名 pageSize（web端部分页面使用该字段）
     */
    @Schema(description = "每页大小（兼容字段：pageSize）", example = "10")
    private Integer pageSize;
}