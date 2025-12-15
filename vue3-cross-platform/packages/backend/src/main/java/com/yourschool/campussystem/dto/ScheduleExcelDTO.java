package com.yourschool.campussystem.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * 课程表Excel导入导出DTO
 * 用于Excel文件的读写
 */
@Data
@HeadRowHeight(20)
@ContentRowHeight(15)
@ColumnWidth(20)
public class ScheduleExcelDTO {

    @ExcelProperty(value = "课程名称", index = 0)
    @ColumnWidth(25)
    private String title;

    @ExcelProperty(value = "课程描述", index = 1)
    @ColumnWidth(30)
    private String description;

    @ExcelProperty(value = "开始时间", index = 2)
    @ColumnWidth(20)
    private String startTime;

    @ExcelProperty(value = "结束时间", index = 3)
    @ColumnWidth(20)
    private String endTime;

    @ExcelProperty(value = "课程类型", index = 4)
    @ColumnWidth(15)
    private String type;

    @ExcelProperty(value = "上课地点", index = 5)
    @ColumnWidth(20)
    private String location;

    @ExcelProperty(value = "是否全天", index = 6)
    @ColumnWidth(12)
    private String isAllDay;

    @ExcelProperty(value = "提醒类型", index = 7)
    @ColumnWidth(15)
    private String remindType;

    @ExcelProperty(value = "提醒分钟数", index = 8)
    @ColumnWidth(15)
    private Integer customRemindMinutes;

    @ExcelProperty(value = "是否重复", index = 9)
    @ColumnWidth(12)
    private String isRepeat;

    @ExcelProperty(value = "重复规则", index = 10)
    @ColumnWidth(20)
    private String repeatRule;

    @ExcelProperty(value = "标签", index = 11)
    @ColumnWidth(15)
    private String tag;

    @ExcelProperty(value = "学期", index = 12)
    @ColumnWidth(15)
    private String semester;
}
