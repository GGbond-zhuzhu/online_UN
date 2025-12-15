package com.yourschool.campussystem.vo;

import com.yourschool.campussystem.enums.ApplyStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "报名记录响应")
public class ApplyRecordVO {

    @Schema(description = "报名记录ID", example = "1")
    private Long id;

    @Schema(description = "兼职ID", example = "1")
    private Long parttimeId;

    @Schema(description = "兼职标题", example = "校园图书馆助理")
    private String parttimeTitle;

    @Schema(description = "报名人ID", example = "3")
    private Long applicantId;

    @Schema(description = "报名人姓名", example = "张三")
    private String applicantName;

    @Schema(description = "报名人头像", example = "https://example.com/avatar.jpg")
    private String applicantAvatar;

    @Schema(description = "报名人学校ID", example = "1")
    private Long applicantSchoolId;

    @Schema(description = "报名说明", example = "我有相关工作经验")
    private String applicationNote;

    @Schema(description = "可工作时间", example = "周一至周五下午")
    private String availableTime;

    @Schema(description = "报名状态", example = "PENDING")
    private ApplyStatusEnum status;

    @Schema(description = "处理人ID")
    private Long processorId;

    @Schema(description = "处理人姓名")
    private String processorName;

    @Schema(description = "处理备注")
    private String processNote;

    @Schema(description = "报名时间")
    private LocalDateTime applyTime;

    @Schema(description = "处理时间")
    private LocalDateTime processTime;
}