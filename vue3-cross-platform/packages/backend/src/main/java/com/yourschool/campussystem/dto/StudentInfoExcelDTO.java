package com.yourschool.campussystem.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * 学生信息Excel导入导出DTO
 */
@Data
@HeadRowHeight(20)
@ContentRowHeight(15)
@ColumnWidth(20)
public class StudentInfoExcelDTO {

    @ExcelProperty(value = "学号", index = 0)
    @ColumnWidth(20)
    private String studentId;

    @ExcelProperty(value = "姓名", index = 1)
    @ColumnWidth(15)
    private String name;

    @ExcelProperty(value = "身份证号", index = 2)
    @ColumnWidth(20)
    private String idCard;

    @ExcelProperty(value = "专业", index = 3)
    @ColumnWidth(25)
    private String major;

    @ExcelProperty(value = "年级", index = 4)
    @ColumnWidth(12)
    private String grade;

    @ExcelProperty(value = "班级", index = 5)
    @ColumnWidth(15)
    private String className;

    @ExcelProperty(value = "状态", index = 6)
    @ColumnWidth(12)
    private String status;
}
