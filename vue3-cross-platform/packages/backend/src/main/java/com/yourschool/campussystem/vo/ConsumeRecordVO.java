package com.yourschool.campussystem.vo;

import com.yourschool.campussystem.enums.ConsumeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "消费记录响应")
public class ConsumeRecordVO {

    @Schema(description = "记录ID", example = "10001")
    private Long id;

    @Schema(description = "校园卡号", example = "20230001")
    private String cardNo;

    @Schema(description = "消费金额（元）", example = "15.50")
    private BigDecimal amount;

    @Schema(description = "消费后余额（元）", example = "341.28")
    private BigDecimal balanceAfter;

    @Schema(description = "商户编号", example = "MERCHANT_1001")
    private String merchantId;

    @Schema(description = "商户名称", example = "第一食堂")
    private String merchantName;

    @Schema(description = "消费类型", example = "CANTEEN")
    private ConsumeTypeEnum consumeType;

    @Schema(description = "消费描述", example = "午餐套餐")
    private String description;

    @Schema(description = "消费地点经度", example = "116.397128")
    private Double longitude;

    @Schema(description = "消费地点纬度", example = "39.916527")
    private Double latitude;

    @Schema(description = "是否在校内消费", example = "true")
    private Boolean isInCampus = true;

    @Schema(description = "消费时间")
    private LocalDateTime consumeTime;

    @Schema(description = "支付方式（CARD/FACE）", example = "CARD")
    private String payMethod = "CARD";
}