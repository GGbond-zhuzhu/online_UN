package com.yourschool.campussystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "团队课程同步置入请求")
public class TeamCourseSyncDTO {

    @NotNull(message = "来源用户ID不能为空")
    @Schema(description = "来源用户ID（从该成员的课程表复制）", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long sourceUserId;

    @NotNull(message = "目标成员ID列表不能为空")
    @Schema(description = "目标成员用户ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> targetUserIds;

    @Schema(description = "是否覆盖目标成员已有课程（默认false）", example = "false")
    private Boolean overwrite = false;
}

