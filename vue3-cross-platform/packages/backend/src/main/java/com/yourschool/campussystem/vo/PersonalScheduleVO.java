package com.yourschool.campussystem.vo;

import com.yourschool.campussystem.enums.RemindTypeEnum;
import com.yourschool.campussystem.enums.ScheduleStatusEnum;
import com.yourschool.campussystem.enums.ScheduleTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "个人行程信息响应")
public class PersonalScheduleVO {

    @Schema(description = "行程ID", example = "1")
    private Long id;

    @Schema(description = "行程标题", example = "数学课")
    private String title;

    @Schema(description = "行程描述", example = "高等数学第三章内容")
    private String description;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "行程类型", example = "CLASS")
    private ScheduleTypeEnum type;

    @Schema(description = "行程状态", example = "PENDING")
    private ScheduleStatusEnum status;

    @Schema(description = "地点", example = "教学楼A301")
    private String location;

    @Schema(description = "是否为全天事件", example = "false")
    private Boolean isAllDay = false;

    @Schema(description = "提醒设置", example = "MINUTES_15")
    private RemindTypeEnum remindType = RemindTypeEnum.NONE;

    @Schema(description = "自定义提醒时间（分钟）", example = "30")
    private Integer customRemindMinutes;

    @Schema(description = "是否已提醒", example = "false")
    private Boolean isReminded = false;

    @Schema(description = "是否重复", example = "false")
    private Boolean isRepeat = false;

    @Schema(description = "重复规则（cron表达式）", example = "0 0 8 * * MON-FRI")
    private String repeatRule;

    @Schema(description = "标签", example = "学习")
    private String tag;

    @Schema(description = "是否为行程系列", example = "false")
    private Boolean isRouteSeries = false;

    @Schema(description = "行程系列主题", example = "做饭工作")
    private String routeSeriesTheme;

    @Schema(description = "创建人ID", example = "1")
    private Long creatorId;

    @Schema(description = "创建人姓名", example = "张三")
    private String creatorName;

    @Schema(description = "同步的团队行程ID", example = "10")
    private Long syncedTeamScheduleId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}