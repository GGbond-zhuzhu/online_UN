package com.yourschool.campussystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 团队邀请请求体（兼容前端一次邀请多人）
 */
@Data
@Schema(description = "团队邀请请求参数")
public class TeamInviteDTO {
    @Schema(description = "被邀请用户ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> userIds;
}

