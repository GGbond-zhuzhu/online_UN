package com.yourschool.campussystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 学生信息实体类
 * 用于学生身份认证，替代教务系统接口
 */
@Data
@TableName("student_info")
public class StudentInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("school_id")
    private Long schoolId;

    @TableField("student_id")
    private String studentId;

    @TableField("name")
    private String name;

    @TableField("id_card")
    private String idCard;

    @TableField("major")
    private String major;

    @TableField("grade")
    private String grade;

    @TableField("class_name")
    private String className;

    @TableField("status")
    private String status;  // ACTIVE/GRADUATED/SUSPENDED

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted = 0;
}
