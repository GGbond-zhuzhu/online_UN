package com.yourschool.campussystem.vo;

import com.yourschool.campussystem.enums.ScheduleStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "团队行程信息响应")
public class TeamScheduleVO {

    @Schema(description = "团队行程ID", example = "1")
    private Long id;

    @Schema(description = "团队ID", example = "1")
    private Long teamId;

    @Schema(description = "团队名称", example = "学习小组")
    private String teamName;

    @Schema(description = "行程标题", example = "团队会议")
    private String title;

    @Schema(description = "行程描述", example = "项目进度讨论会")
    private String description;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "地点", example = "会议室B201")
    private String location;

    @Schema(description = "行程状态", example = "PENDING")
    private ScheduleStatusEnum status;

    @Schema(description = "是否需要确认", example = "true")
    private Boolean needConfirm = false;

    @Schema(description = "创建人ID", example = "1")
    private Long creatorId;

    @Schema(description = "创建人姓名", example = "张三")
    private String creatorName;

    @Schema(description = "参会人员列表")
    private List<TeamScheduleAttendeeVO> attendees;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "同步的个人行程数", example = "5")
    private Integer syncedCount = 0;
}