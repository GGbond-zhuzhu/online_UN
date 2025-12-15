package com.yourschool.campussystem.vo;

import com.yourschool.campussystem.enums.UserRoleEnum;  // 添加这行
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户信息响应")
public class UserInfoVO {
    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "用户角色", example = "STUDENT")
    private UserRoleEnum role;  // 修改这里：String -> UserRoleEnum

    @Schema(description = "学校ID", example = "1")
    private Long schoolId;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}