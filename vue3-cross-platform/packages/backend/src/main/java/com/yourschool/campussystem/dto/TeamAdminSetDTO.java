package com.yourschool.campussystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "设置团队管理员请求")
public class TeamAdminSetDTO {

    @NotNull(message = "管理员ID列表不能为空")
    @Schema(description = "管理员用户ID列表（最多4人，不包含创建者）", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> adminUserIds;
}

