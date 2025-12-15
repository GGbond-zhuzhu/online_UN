package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.yourschool.campussystem.enums.ApplyStatusEnum;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 兼职报名实体类
 */
@Data
@TableName("parttime_apply")
public class ParttimeApply {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("parttime_id")
    private Long parttimeId;

    @TableField("applicant_id")
    private Long applicantId;

    @TableField("application_note")
    private String applicationNote;

    @TableField("available_time")
    private String availableTime;

    @TableField("status")
    private ApplyStatusEnum status;

    @TableField("processor_id")
    private Long processorId;

    @TableField("process_note")
    private String processNote;

    @TableField(value = "apply_time", fill = FieldFill.INSERT)
    private LocalDateTime applyTime;

    @TableField("process_time")
    private LocalDateTime processTime;
}
