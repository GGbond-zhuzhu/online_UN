package com.yourschool.campussystem.vo;

import com.yourschool.campussystem.enums.ParttimeStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "兼职信息响应")
public class ParttimeVO {

    @Schema(description = "兼职ID", example = "1")
    private Long id;

    @Schema(description = "兼职标题", example = "校园图书馆助理")
    private String title;

    @Schema(description = "兼职描述", example = "协助图书馆管理员整理书籍、借还书登记等工作")
    private String description;

    @Schema(description = "薪资（元/小时）", example = "20.00")
    private BigDecimal salaryPerHour;

    @Schema(description = "招聘人数", example = "5")
    private Integer recruitCount;

    @Schema(description = "已报名人数", example = "3")
    private Integer appliedCount = 0;

    @Schema(description = "工作开始时间")
    private LocalDateTime workStartTime;

    @Schema(description = "工作结束时间")
    private LocalDateTime workEndTime;

    @Schema(description = "工作地点", example = "图书馆三楼")
    private String location;

    @Schema(description = "工作要求", example = "有责任心，每周至少工作10小时")
    private String requirements;

    @Schema(description = "兼职状态", example = "RECRUITING")
    private ParttimeStatusEnum status;

    @Schema(description = "发布者ID", example = "2")
    private Long publisherId;

    @Schema(description = "发布者姓名", example = "李老师")
    private String publisherName;

    @Schema(description = "发布者头像", example = "https://example.com/avatar.jpg")
    private String publisherAvatar;

    @Schema(description = "学校ID", example = "1")
    private Long schoolId;

    @Schema(description = "学校名称", example = "清华大学")
    private String schoolName;

    @Schema(description = "联系人姓名", example = "李老师")
    private String contactName;

    @Schema(description = "联系电话（仅同校用户可见）", example = "13800138000")
    private String contactPhone;

    @Schema(description = "图片URL列表")
    private List<String> imageUrls;

    @Schema(description = "是否已报名", example = "false")
    protected Boolean hasApplied = false;  // 【修改这里】private -> protected

    @Schema(description = "报名状态（如果已报名）", example = "PENDING")
    private String applyStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}