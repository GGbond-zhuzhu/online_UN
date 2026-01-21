package com.yourschool.campussystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 地图搜索DTO
 */
@Data
@Schema(description = "地图POI搜索请求参数")
public class MapSearchDTO {

    @NotBlank(message = "搜索关键词不能为空")
    @Schema(description = "搜索关键词", example = "北京大学", requiredMode = Schema.RequiredMode.REQUIRED)
    private String keyword;

    @Schema(description = "城市名称（可选，为空则全国搜索）", example = "北京")
    private String city;

    @Schema(description = "返回结果数量", example = "20")
    private Integer pageSize = 20;

    @Schema(description = "页码", example = "1")
    private Integer page = 1;
}

