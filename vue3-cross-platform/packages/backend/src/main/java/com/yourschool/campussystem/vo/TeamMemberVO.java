package com.yourschool.campussystem.vo;

import com.yourschool.campussystem.enums.TeamRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "团队成员信息响应")
public class TeamMemberVO {

    @Schema(description = "成员ID", example = "1")
    private Long userId;

    @Schema(description = "成员姓名", example = "张三")
    private String userName;

    @Schema(description = "成员头像", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "成员角色", example = "CREATOR")
    private TeamRoleEnum role;

    @Schema(description = "加入时间")
    private LocalDateTime joinTime;

    @Schema(description = "是否在线", example = "true")
    private Boolean isOnline = false;
}