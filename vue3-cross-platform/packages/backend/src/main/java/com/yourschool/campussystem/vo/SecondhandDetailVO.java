package com.yourschool.campussystem.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "二手商品详情响应（包含联系信息）")
public class SecondhandDetailVO extends SecondhandGoodsVO {

    @Schema(description = "联系电话（仅同校用户可见）", example = "13800138000")
    private String contactPhone;

    @Schema(description = "微信联系方式（仅同校用户可见）", example = "wx123456")
    private String contactWechat;

    @Schema(description = "交易地点", example = "图书馆一楼")
    private String location;

    @Schema(description = "是否已收藏", example = "true")
    private Boolean isFavorited = false;

    @Schema(description = "是否为发布者", example = "false")
    private Boolean isPublisher = false;

    @Schema(description = "是否同校", example = "true")
    private Boolean isSameSchool = false;
}