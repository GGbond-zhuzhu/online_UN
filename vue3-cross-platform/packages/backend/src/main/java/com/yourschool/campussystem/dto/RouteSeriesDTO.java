package com.yourschool.campussystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 行程系列DTO
 */
@Data
@Schema(description = "行程系列创建/更新请求参数")
public class RouteSeriesDTO {

    @NotBlank(message = "行程主题不能为空")
    @Schema(description = "行程主题", example = "做饭工作", requiredMode = Schema.RequiredMode.REQUIRED)
    private String theme;

    @NotEmpty(message = "地点列表不能为空")
    @Schema(description = "地点列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    private List<LocationDTO> locations;

    @Schema(description = "路线规划结果（可选）")
    private RouteResultDTO routeResult;

    /**
     * 地点DTO
     */
    @Data
    @Schema(description = "地点信息")
    public static class LocationDTO {
        @NotBlank(message = "地点名称不能为空")
        @Schema(description = "地点名称", example = "客户A家", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        @Schema(description = "地点地址", example = "北京市朝阳区xxx街道xxx号")
        private String address;

        @Schema(description = "经度", example = "116.397128", requiredMode = Schema.RequiredMode.REQUIRED)
        private BigDecimal longitude;

        @Schema(description = "纬度", example = "39.916527", requiredMode = Schema.RequiredMode.REQUIRED)
        private BigDecimal latitude;
    }

    /**
     * 路线结果DTO
     */
    @Data
    @Schema(description = "路线规划结果")
    public static class RouteResultDTO {
        @Schema(description = "总距离（公里）", example = "15.5")
        private BigDecimal totalDistance;

        @Schema(description = "总时长（分钟）", example = "45")
        private Integer totalDuration;

        @Schema(description = "路线步骤列表")
        private List<RouteStepDTO> steps;
    }

    /**
     * 路线步骤DTO
     */
    @Data
    @Schema(description = "路线步骤")
    public static class RouteStepDTO {
        @Schema(description = "步骤说明", example = "从起点出发，沿xxx路行驶")
        private String instruction;

        @Schema(description = "步骤距离（公里）", example = "2.5")
        private BigDecimal distance;
    }
}

