package com.yourschool.campussystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "游客校园卡申请请求参数")
public class VisitorCardApplyDTO {

    @NotBlank(message = "姓名不能为空")
    @Schema(description = "申请人姓名", example = "李四", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "身份证号格式不正确")
    @Schema(description = "身份证号", example = "110101199001011234", requiredMode = Schema.RequiredMode.REQUIRED)
    private String idCard;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "联系电话", example = "13800138000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @Schema(description = "来访事由", example = "参观校园")
    private String purpose;

    @Schema(description = "预计离开时间（yyyy-MM-dd）", example = "2024-12-31")
    private String expectedLeaveDate;

    @Schema(description = "经度", example = "116.397128")
    private Double longitude;

    @Schema(description = "纬度", example = "39.916527")
    private Double latitude;
}