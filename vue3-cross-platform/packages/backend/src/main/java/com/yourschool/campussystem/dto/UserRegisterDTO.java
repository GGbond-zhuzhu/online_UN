package com.yourschool.campussystem.dto;

import com.yourschool.campussystem.enums.UserRoleEnum;  // 【新增】导入枚举
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "用户注册请求参数")
public class UserRegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "确认密码", example = "123456")
    private String confirmPassword;

    @Size(max = 20, message = "昵称长度不能超过20个字符")
    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "用户角色", example = "STUDENT")
    private UserRoleEnum role;  // 【关键修改】String → UserRoleEnum，改为可选

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱", example = "user@example.com")
    private String email;

    @Schema(description = "学校ID", example = "1")
    private Long schoolId;
}