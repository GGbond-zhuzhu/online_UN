package com.yourschool.campussystem.dto;

import com.yourschool.campussystem.enums.ParttimeStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "兼职发布请求参数")
public class ParttimePublishDTO {

    @NotBlank(message = "兼职标题不能为空")
    @Size(min = 2, max = 100, message = "兼职标题长度必须在2-100个字符之间")
    @Schema(description = "兼职标题", example = "校园图书馆助理", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "兼职描述不能为空")
    @Size(max = 2000, message = "兼职描述长度不能超过2000个字符")
    @Schema(description = "兼职描述", example = "协助图书馆管理员整理书籍、借还书登记等工作", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @NotNull(message = "薪资不能为空")
    @DecimalMin(value = "0.01", message = "薪资必须大于0")
    @Schema(description = "薪资（元/小时）", example = "20.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal salaryPerHour;

    @NotNull(message = "招聘人数不能为空")
    @Min(value = 1, message = "招聘人数至少为1")
    @Schema(description = "招聘人数", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer recruitCount;

    @NotNull(message = "工作开始时间不能为空")
    @Schema(description = "工作开始时间", example = "2024-03-01T09:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime workStartTime;

    @NotNull(message = "工作结束时间不能为空")
    @Schema(description = "工作结束时间", example = "2024-06-30T18:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime workEndTime;

    @NotBlank(message = "工作地点不能为空")
    @Schema(description = "工作地点", example = "图书馆三楼", requiredMode = Schema.RequiredMode.REQUIRED)
    private String location;

    @Schema(description = "工作要求", example = "有责任心，每周至少工作10小时")
    private String requirements;

    @Schema(description = "兼职状态", example = "RECRUITING")
    private ParttimeStatusEnum status = ParttimeStatusEnum.RECRUITING;

    @Schema(description = "联系人姓名", example = "李老师")
    private String contactName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "联系电话", example = "13800138000")
    private String contactPhone;

    @Schema(description = "图片URL列表")
    private List<String> imageUrls;
}