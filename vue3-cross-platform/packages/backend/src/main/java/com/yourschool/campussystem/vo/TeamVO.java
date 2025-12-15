package com.yourschool.campussystem.vo;

import com.yourschool.campussystem.enums.TeamRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "团队信息响应")
public class TeamVO {

    @Schema(description = "团队ID", example = "1")
    private Long id;

    @Schema(description = "团队名称", example = "学习小组")
    private String name;

    @Schema(description = "团队描述", example = "高等数学学习小组")
    private String description;

    @Schema(description = "团队头像", example = "https://example.com/team.jpg")
    private String avatar;

    @Schema(description = "创建人ID", example = "1")
    private Long creatorId;

    @Schema(description = "创建人姓名", example = "张三")
    private String creatorName;

    @Schema(description = "学校ID", example = "1")
    private Long schoolId;

    @Schema(description = "学校名称", example = "清华大学")
    private String schoolName;

    @Schema(description = "是否需要审批", example = "false")
    private Boolean needApprove = false;

    @Schema(description = "最大成员数", example = "20")
    private Integer maxMembers = 20;

    @Schema(description = "当前成员数", example = "5")
    private Integer memberCount = 0;

    @Schema(description = "用户在此团队中的角色", example = "CREATOR")
    private TeamRoleEnum userRole;

    @Schema(description = "团队邀请码", example = "ABC12345")
    private String inviteCode;

    @Schema(description = "成员列表")
    private List<TeamMemberVO> members;

    @Schema(description = "最近的团队行程")
    private TeamScheduleVO recentSchedule;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}