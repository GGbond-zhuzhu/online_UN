package com.yourschool.campussystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "兼职报名请求参数")
public class ParttimeApplyDTO {

    @NotNull(message = "兼职ID不能为空")
    @Schema(description = "兼职ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long parttimeId;

    @NotBlank(message = "报名说明不能为空")
    @Schema(description = "报名说明/个人简介", example = "我有图书馆工作经验，每周可工作20小时", requiredMode = Schema.RequiredMode.REQUIRED)
    private String applicationNote;

    @Schema(description = "可工作时间", example = "周一至周五下午")
    private String availableTime;
}