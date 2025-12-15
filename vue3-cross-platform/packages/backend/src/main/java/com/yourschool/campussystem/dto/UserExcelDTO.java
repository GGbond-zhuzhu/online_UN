package com.yourschool.campussystem.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

/**
 * 用户Excel导入导出DTO
 * 用于Excel文件的读写
 */
@Data
@HeadRowHeight(20)
@ContentRowHeight(15)
@ColumnWidth(20)
public class UserExcelDTO {

    @ExcelProperty(value = "用户名", index = 0)
    @ColumnWidth(20)
    private String username;

    @ExcelProperty(value = "密码", index = 1)
    @ColumnWidth(20)
    private String password;

    @ExcelProperty(value = "昵称", index = 2)
    @ColumnWidth(20)
    private String nickname;

    @ExcelProperty(value = "角色", index = 3)
    @ColumnWidth(15)
    private String role;

    @ExcelProperty(value = "邮箱", index = 4)
    @ColumnWidth(25)
    private String email;

    @ExcelProperty(value = "手机号", index = 5)
    @ColumnWidth(18)
    private String phone;

    @ExcelProperty(value = "学校ID", index = 6)
    @ColumnWidth(15)
    private String schoolId;
}
