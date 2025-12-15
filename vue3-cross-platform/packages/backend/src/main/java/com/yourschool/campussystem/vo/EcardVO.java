package com.yourschool.campussystem.vo;

import com.yourschool.campussystem.enums.CardStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "校园卡信息响应")
public class EcardVO {

    @Schema(description = "校园卡号", example = "20230001")
    private String cardNo;

    @Schema(description = "持卡人ID", example = "1")
    private Long userId;

    @Schema(description = "持卡人姓名", example = "张三")
    private String userName;

    @Schema(description = "余额（元）", example = "356.78")
    private BigDecimal balance;

    @Schema(description = "卡状态", example = "NORMAL")
    private CardStatusEnum status;

    @Schema(description = "是否为游客卡", example = "false")
    private Boolean isVisitorCard = false;

    @Schema(description = "游客卡有效期至")
    private LocalDateTime visitorExpireTime;

    @Schema(description = "今日消费次数", example = "3")
    private Integer todayConsumeCount = 0;

    @Schema(description = "今日消费总额", example = "45.50")
    private BigDecimal todayConsumeAmount = BigDecimal.ZERO;

    @Schema(description = "最近消费时间")
    private LocalDateTime lastConsumeTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}