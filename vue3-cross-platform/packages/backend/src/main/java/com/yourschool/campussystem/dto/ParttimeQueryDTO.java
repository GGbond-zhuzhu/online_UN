package com.yourschool.campussystem.dto;

import com.yourschool.campussystem.enums.ParttimeStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "兼职查询参数")
public class ParttimeQueryDTO {

    @Schema(description = "关键词", example = "图书馆")
    private String keyword;

    @Schema(description = "兼职状态", example = "RECRUITING")
    private ParttimeStatusEnum status;

    @Schema(description = "薪资最小值", example = "15")
    private BigDecimal minSalary;

    @Schema(description = "薪资最大值", example = "50")
    private BigDecimal maxSalary;

    @Schema(description = "工作开始时间之后", example = "2024-03-01T00:00:00")
    private LocalDateTime workStartAfter;

    @Schema(description = "工作结束时间之前", example = "2024-12-31T23:59:59")
    private LocalDateTime workEndBefore;

    @Schema(description = "学校ID", example = "1")
    private Long schoolId;

    @Schema(description = "排序字段", example = "createTime", allowableValues = {"createTime", "salaryPerHour", "workStartTime"})
    private String sortBy = "createTime";

    @Schema(description = "排序方向", example = "DESC", allowableValues = {"ASC", "DESC"})
    private String sortOrder = "DESC";

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
}