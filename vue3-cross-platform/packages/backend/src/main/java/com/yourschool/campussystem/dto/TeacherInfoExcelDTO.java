package com.yourschool.campussystem.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * 教师信息Excel导入导出DTO
 */
@Data
@HeadRowHeight(20)
@ContentRowHeight(15)
@ColumnWidth(20)
public class TeacherInfoExcelDTO {

    @ExcelProperty(value = "工号", index = 0)
    @ColumnWidth(18)
    private String teacherId;

    @ExcelProperty(value = "姓名", index = 1)
    @ColumnWidth(15)
    private String name;

    @ExcelProperty(value = "身份证号", index = 2)
    @ColumnWidth(20)
    private String idCard;

    @ExcelProperty(value = "部门", index = 3)
    @ColumnWidth(25)
    private String department;

    @ExcelProperty(value = "职称", index = 4)
    @ColumnWidth(15)
    private String title;

    @ExcelProperty(value = "联系电话", index = 5)
    @ColumnWidth(18)
    private String phone;

    @ExcelProperty(value = "邮箱", index = 6)
    @ColumnWidth(25)
    private String email;

    @ExcelProperty(value = "状态", index = 7)
    @ColumnWidth(12)
    private String status;
}
