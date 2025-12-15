package com.yourschool.campussystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "团队创建请求参数")
public class TeamCreateDTO {

    @NotBlank(message = "团队名称不能为空")
    @Schema(description = "团队名称", example = "学习小组", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "团队描述", example = "高等数学学习小组")
    private String description;

    @Schema(description = "团队头像", example = "https://example.com/team.jpg")
    private String avatar;

    @Schema(description = "初始成员ID列表")
    private List<Long> memberIds;

    @Schema(description = "是否需要审批", example = "false")
    private Boolean needApprove = false;

    @Schema(description = "最大成员数", example = "20")
    private Integer maxMembers = 20;
}