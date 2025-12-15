package com.yourschool.campussystem.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "团队行程参会人员信息响应")
public class TeamScheduleAttendeeVO {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户姓名", example = "张三")
    private String userName;

    @Schema(description = "用户头像", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "确认状态", example = "PENDING", allowableValues = {"PENDING", "CONFIRMED", "DECLINED"})
    private String confirmStatus = "PENDING";

    @Schema(description = "是否创建者", example = "false")
    private Boolean isCreator = false;

    @Schema(description = "确认时间")
    private LocalDateTime confirmTime;

    @Schema(description = "确认备注", example = "我会准时参加")
    private String confirmNote;

    @Schema(description = "是否已读", example = "true")
    private Boolean hasRead = false;

    @Schema(description = "最后读取时间")
    private LocalDateTime lastReadTime;
}