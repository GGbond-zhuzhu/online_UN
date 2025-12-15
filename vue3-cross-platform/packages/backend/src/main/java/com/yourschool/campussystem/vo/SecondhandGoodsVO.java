package com.yourschool.campussystem.vo;

import com.yourschool.campussystem.enums.CategoryEnum;  // 【修改】导入枚举
import com.yourschool.campussystem.enums.GoodsStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "二手商品信息响应")
public class SecondhandGoodsVO {

    @Schema(description = "商品ID", example = "1001")
    private Long id;

    @Schema(description = "商品标题", example = "华为MateBook 14 2022款")
    private String title;

    @Schema(description = "商品描述", example = "2022年购入，95新，配置：i5-1240P/16G/512G")
    private String description;

    @Schema(description = "价格（元）", example = "4500.00")
    private BigDecimal price;

    @Schema(description = "原价（元）", example = "5999.00")
    private BigDecimal originalPrice;

    @Schema(description = "折扣率", example = "0.75")
    public BigDecimal getDiscount() {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE;
        }
        return price.divide(originalPrice, 2, BigDecimal.ROUND_HALF_UP);
    }

    @Schema(description = "商品分类", example = "ELECTRONICS")  // 【修改】使用枚举
    private CategoryEnum category;

    @Schema(description = "商品状态", example = "ON_SALE")
    private GoodsStatusEnum status;

    @Schema(description = "商品图片URL列表")
    private List<String> imageUrls;

    @Schema(description = "发布者ID", example = "1")
    private Long publisherId;

    @Schema(description = "发布者昵称", example = "张三")
    private String publisherName;

    @Schema(description = "发布者头像", example = "https://example.com/avatar.jpg")
    private String publisherAvatar;

    @Schema(description = "学校ID", example = "1")
    private Long schoolId;

    @Schema(description = "学校名称", example = "清华大学")
    private String schoolName;

    @Schema(description = "收藏次数", example = "15")
    private Integer favoriteCount = 0;

    @Schema(description = "浏览次数", example = "120")
    private Integer viewCount = 0;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}