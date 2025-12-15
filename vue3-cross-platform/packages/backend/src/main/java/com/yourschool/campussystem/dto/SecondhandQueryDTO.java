package com.yourschool.campussystem.dto;

import com.yourschool.campussystem.enums.CategoryEnum;  // 【修改】导入枚举
import com.yourschool.campussystem.enums.GoodsStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "二手商品查询参数")
public class SecondhandQueryDTO {

    @Schema(description = "关键词", example = "华为")
    private String keyword;

    @Schema(description = "商品分类", example = "ELECTRONICS")  // 【修改】使用枚举
    private CategoryEnum category;

    @Schema(description = "商品状态", example = "ON_SALE")
    private GoodsStatusEnum status;

    @Schema(description = "价格最小值", example = "0")
    private BigDecimal minPrice;

    @Schema(description = "价格最大值", example = "10000")
    private BigDecimal maxPrice;

    @Schema(description = "学校ID", example = "1")
    private Long schoolId;

    @Schema(description = "排序字段", example = "publishTime", allowableValues = {"publishTime", "price", "viewCount", "favoriteCount"})
    private String sortBy = "publishTime";

    @Schema(description = "排序方向", example = "DESC", allowableValues = {"ASC", "DESC"})
    private String sortOrder = "DESC";

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
}