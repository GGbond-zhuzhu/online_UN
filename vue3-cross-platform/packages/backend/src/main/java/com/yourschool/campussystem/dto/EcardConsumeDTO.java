package com.yourschool.campussystem.dto;

import com.yourschool.campussystem.enums.ConsumeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "校园卡消费请求参数")
public class EcardConsumeDTO {

    @NotNull(message = "消费金额不能为空")
    @DecimalMin(value = "0.01", message = "消费金额必须大于0")
    @Schema(description = "消费金额（元）", example = "15.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @NotBlank(message = "商户编号不能为空")
    @Schema(description = "商户编号", example = "MERCHANT_1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String merchantId;

    @NotBlank(message = "商户名称不能为空")
    @Schema(description = "商户名称", example = "第一食堂", requiredMode = Schema.RequiredMode.REQUIRED)
    private String merchantName;

    @NotNull(message = "消费类型不能为空")
    @Schema(description = "消费类型", example = "CANTEEN", requiredMode = Schema.RequiredMode.REQUIRED)
    private ConsumeTypeEnum consumeType;

    @Schema(description = "消费描述", example = "午餐套餐")
    private String description;

    @Schema(description = "经度（用于定位校验）", example = "116.397128")
    private Double longitude;

    @Schema(description = "纬度（用于定位校验）", example = "39.916527")
    private Double latitude;
}