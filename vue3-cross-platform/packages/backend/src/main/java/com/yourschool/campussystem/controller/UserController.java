package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.dto.LoginDTO;
import com.yourschool.campussystem.dto.UserRegisterDTO;
import com.yourschool.campussystem.dto.UserUpdateDTO;
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

import java.util.Map;

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
            HttpServletRequest request,
            @Parameter(description = "用户更新信息", required = true)
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        UserInfoVO userInfo = userService.updateUserInfo(userId, updateDTO);
        return ApiResponse.success("用户信息更新成功", userInfo);
    }

    @Operation(summary = "获取用户统计数据", description = "获取当前用户的收藏数、历史记录数、发布数、积分等统计数据")
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getUserStats(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> stats = userService.getUserStats(userId);
        return ApiResponse.success("查询成功", stats);
    }

    @Operation(summary = "修改密码", description = "用户修改登录密码")
    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(
            HttpServletRequest request,
            @Parameter(description = "修改密码参数", required = true)
            @RequestBody Map<String, String> params) {
        
        Long userId = UserContextUtils.getUserIdRequired(request);
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        if (oldPassword == null || oldPassword.isEmpty()) {
            return ApiResponse.error(com.yourschool.campussystem.common.ErrorCode.BAD_REQUEST, "旧密码不能为空");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            return ApiResponse.error(com.yourschool.campussystem.common.ErrorCode.BAD_REQUEST, "新密码不能为空");
        }
        
        userService.changePassword(userId, oldPassword, newPassword);
        return ApiResponse.success("密码修改成功，请重新登录");
    }

    @Operation(summary = "退出登录", description = "用户退出登录，清除token")
    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest request) {
        // 从token中获取用户ID，更安全可靠
        Long userId = UserContextUtils.getUserId(request);
        if (userId != null) {
            // 可以在这里实现清除token的逻辑，比如将token加入黑名单
            // 当前JWT是无状态的，所以只需要前端清除token即可
        }
        return ApiResponse.success("退出登录成功");
    }
}