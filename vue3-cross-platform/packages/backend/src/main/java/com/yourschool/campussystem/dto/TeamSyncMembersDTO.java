package com.yourschool.campussystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "选择成员同步置入请求")
public class TeamSyncMembersDTO {

    @NotNull(message = "成员ID列表不能为空")
    @Schema(description = "目标成员用户ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> userIds;
}

