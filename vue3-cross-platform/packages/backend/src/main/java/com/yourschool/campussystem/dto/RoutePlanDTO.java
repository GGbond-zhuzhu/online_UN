package com.yourschool.campussystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 路径规划DTO
 */
@Data
@Schema(description = "路径规划请求参数")
public class RoutePlanDTO {

    @NotEmpty(message = "地点列表不能为空")
    @Size(min = 2, message = "至少需要2个地点")
    @Schema(description = "地点坐标列表（至少2个）", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<PointDTO> points;

    @Schema(description = "路径规划策略（0:速度优先/时间最短, 1:费用优先, 2:距离最短, 3:不走高速）", example = "0")
    private Integer strategy = 0;

    /**
     * 坐标点DTO
     */
    @Data
    @Schema(description = "坐标点")
    public static class PointDTO {
        @Schema(description = "经度", example = "116.397128", requiredMode = Schema.RequiredMode.REQUIRED)
        private BigDecimal longitude;

        @Schema(description = "纬度", example = "39.916527", requiredMode = Schema.RequiredMode.REQUIRED)
        private BigDecimal latitude;
    }
}

