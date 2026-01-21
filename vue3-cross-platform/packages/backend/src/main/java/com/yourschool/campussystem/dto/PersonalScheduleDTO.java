package com.yourschool.campussystem.dto;

import com.yourschool.campussystem.enums.RemindTypeEnum;
import com.yourschool.campussystem.enums.ScheduleStatusEnum;
import com.yourschool.campussystem.enums.ScheduleTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "个人行程创建/更新请求参数")
public class PersonalScheduleDTO {

    @NotBlank(message = "行程标题不能为空")
    @Schema(description = "行程标题", example = "数学课", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "行程描述", example = "高等数学第三章内容")
    private String description;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间", example = "2024-03-15T08:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间", example = "2024-03-15T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime endTime;

    @Schema(description = "行程类型", example = "CLASS")
    private ScheduleTypeEnum type = ScheduleTypeEnum.OTHER;

    @Schema(description = "行程状态", example = "PENDING")
    private ScheduleStatusEnum status = ScheduleStatusEnum.PENDING;

    @Schema(description = "地点", example = "教学楼A301")
    private String location;

    @Schema(description = "是否为全天事件", example = "false")
    private Boolean isAllDay = false;

    @Schema(description = "提醒设置", example = "MINUTES_15")
    private RemindTypeEnum remindType = RemindTypeEnum.NONE;

    @Schema(description = "自定义提醒时间（分钟）", example = "30")
    private Integer customRemindMinutes;

    @Schema(description = "是否重复", example = "false")
    private Boolean isRepeat = false;

    @Schema(description = "重复规则（cron表达式）", example = "0 0 8 * * MON-FRI")
    private String repeatRule;

    @Schema(description = "标签", example = "学习")
    private String tag;

    // ==================== 行程系列（Route Series）扩展 ====================

    @Schema(description = "是否为行程系列", example = "false")
    private Boolean isRouteSeries = false;

    @Schema(description = "行程系列数据（当 isRouteSeries=true 时传）")
    private RouteSeriesDTO routeSeriesData;
}