package com.yourschool.campussystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "团队行程创建/更新请求参数")
public class TeamScheduleDTO {

    @NotNull(message = "团队ID不能为空")
    @Schema(description = "团队ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long teamId;

    @NotBlank(message = "行程标题不能为空")
    @Schema(description = "行程标题", example = "团队会议", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "行程描述", example = "项目进度讨论会")
    private String description;

    @NotNull(message = "开始时间不能为空")
    @Schema(description = "开始时间", example = "2024-03-15T14:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间", example = "2024-03-15T16:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime endTime;

    @Schema(description = "地点", example = "会议室B201")
    private String location;

    @Schema(description = "参会人员ID列表")
    private List<Long> attendeeIds;

    @Schema(description = "同步置入成员ID列表（将团队行程写入这些成员的个人行程表；为空则默认使用attendeeIds；两者都为空则兼容旧行为：默认全员）")
    private List<Long> syncInUserIds;

    @Schema(description = "是否需要确认", example = "true")
    private Boolean needConfirm = false;
}