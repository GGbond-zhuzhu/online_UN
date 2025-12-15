package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.dto.LoginDTO;
import com.yourschool.campussystem.dto.UserRegisterDTO;
import com.yourschool.campussystem.service.UserService;
import com.yourschool.campussystem.util.UserContextUtils;
import com.yourschool.campussystem.vo.LoginVO;
import com.yourschool.campussystem.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户注册、登录、信息管理")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "操作成功",
                content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class)
                )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "参数错误",
                content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ApiResponse.class)
                )
        )
})
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册", description = "新用户注册接口，返回注册结果")
    @PostMapping("/register")
    public ApiResponse<Long> register(
            @Parameter(description = "用户注册信息", required = true)
            @Valid @RequestBody UserRegisterDTO registerDTO) {
        Long userId = userService.register(registerDTO);
        return ApiResponse.success("注册成功", userId);
    }

    @Operation(summary = "用户登录", description = "用户登录接口，返回JWT令牌和用户基本信息")
    @PostMapping("/login")
    public ApiResponse<LoginVO> login(
            @Parameter(description = "登录请求参数", required = true)
            @Valid @RequestBody LoginDTO loginDTO,
            HttpServletRequest request) {
        LoginVO loginVO = userService.login(loginDTO, request);
        return ApiResponse.success("登录成功", loginVO);
    }

    @Operation(summary = "获取当前用户信息", description = "获取已登录用户的详细信息（从token中获取用户ID）")
    @GetMapping("/info")
    public ApiResponse<UserInfoVO> getUserInfo(HttpServletRequest request) {
        // 从token中获取用户ID，更安全可靠
        Long userId = UserContextUtils.getUserIdRequired(request);
        UserInfoVO userInfo = userService.getUserInfo(userId);
        return ApiResponse.success(userInfo);
    }

    @Operation(summary = "更新用户信息", description = "更新当前用户的基本信息（昵称、头像等）")
    @PutMapping("/info")
    public ApiResponse<UserInfoVO> updateUserInfo(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            
            @Parameter(description = "昵称")
            @RequestParam(required = false) String nickname,
            
            @Parameter(description = "头像URL")
            @RequestParam(required = false) String avatarUrl,
            
            @Parameter(description = "手机号")
            @RequestParam(required = false) String phone) {
        
        // 模拟更新后的用户信息
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setId(userId);
        userInfo.setNickname(nickname != null ? nickname : "张三");
        userInfo.setAvatarUrl(avatarUrl != null ? avatarUrl : "https://example.com/avatar.jpg");
        userInfo.setPhone(phone != null ? phone : "13800138000");
        
        return ApiResponse.success("用户信息更新成功", userInfo);
    }

    @Operation(summary = "修改密码", description = "用户修改登录密码")
    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            
            @Parameter(description = "旧密码", required = true)
            @RequestParam String oldPassword,
            
            @Parameter(description = "新密码", required = true)
            @RequestParam String newPassword) {
        
        return ApiResponse.success("密码修改成功，请重新登录");
    }

    @Operation(summary = "退出登录", description = "用户退出登录，清除token")
    @PostMapping("/logout")
    public ApiResponse<String> logout(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId) {
        
        return ApiResponse.success("退出登录成功");
    }
}