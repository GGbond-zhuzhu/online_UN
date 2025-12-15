package com.yourschool.campussystem.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "兼职详情响应")
public class ParttimeDetailVO extends ParttimeVO {

    @Schema(description = "是否同校", example = "true")
    private Boolean isSameSchool = false;

    @Schema(description = "是否为发布者", example = "false")
    private Boolean isPublisher = false;

    @Schema(description = "是否可报名", example = "true")
    public Boolean getCanApply() {
        // 现在可以直接访问父类的protected字段hasApplied
        return !isPublisher && !hasApplied && isSameSchool;
    }
}