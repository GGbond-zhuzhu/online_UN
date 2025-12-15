package com.yourschool.campussystem.dto;

import com.yourschool.campussystem.enums.CategoryEnum;  // 【修改】导入枚举
import com.yourschool.campussystem.enums.GoodsStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "二手商品发布请求参数")
public class SecondhandPublishDTO {

    @NotBlank(message = "商品标题不能为空")
    @Size(min = 2, max = 50, message = "商品标题长度必须在2-50个字符之间")
    @Schema(description = "商品标题", example = "华为MateBook 14 2022款", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "商品描述不能为空")
    @Size(max = 1000, message = "商品描述长度不能超过1000个字符")
    @Schema(description = "商品描述", example = "2022年购入，95新，配置：i5-1240P/16G/512G，有原装充电器和电脑包", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @Schema(description = "价格（元）", example = "4500.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;

    @Schema(description = "原价（元）", example = "5999.00")
    private BigDecimal originalPrice;

    @NotNull(message = "商品分类不能为空")  // 【修改】使用枚举
    @Schema(description = "商品分类", example = "ELECTRONICS", requiredMode = Schema.RequiredMode.REQUIRED)
    private CategoryEnum category;  // 【修改】String -> CategoryEnum

    @Schema(description = "商品状态", example = "ON_SALE", requiredMode = Schema.RequiredMode.REQUIRED)
    private GoodsStatusEnum status = GoodsStatusEnum.ON_SALE;

    @Schema(description = "商品图片URL列表", example = "[\"https://example.com/img1.jpg\", \"https://example.com/img2.jpg\"]")
    private List<String> imageUrls;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "联系电话", example = "13800138000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contactPhone;

    @Schema(description = "微信联系方式", example = "wx123456")
    private String contactWechat;

    @Schema(description = "交易地点", example = "图书馆一楼")
    private String location;
}