package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yourschool.campussystem.enums.CategoryEnum;
import com.yourschool.campussystem.enums.GoodsStatusEnum;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("secondhand_goods")
public class SecondhandGoods {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("price")
    private BigDecimal price;

    @TableField("original_price")
    private BigDecimal originalPrice;

    @TableField("category")
    private CategoryEnum category;

    @TableField("status")
    private GoodsStatusEnum status;

    @TableField("image_urls")
    private String imageUrls;  // JSON字符串存储多个图片URL

    @TableField("publisher_id")
    private Long publisherId;

    @TableField("school_id")
    private Long schoolId;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("contact_wechat")
    private String contactWechat;

    @TableField("location")
    private String location;

    @TableField("favorite_count")
    private Integer favoriteCount = 0;

    @TableField("view_count")
    private Integer viewCount = 0;

    @TableField(value = "publish_time", fill = FieldFill.INSERT)
    private LocalDateTime publishTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted = 0;
}