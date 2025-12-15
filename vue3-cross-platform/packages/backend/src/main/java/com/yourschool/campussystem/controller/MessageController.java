package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.service.MessageService;
import com.yourschool.campussystem.util.UserContextUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息中心Controller
 * 处理系统消息、兼职消息、二手交易消息等
 */
@RestController
@RequestMapping("/api/messages")
@Tag(name = "消息中心", description = "系统消息管理：消息列表、标记已读、批量操作等")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "获取消息列表", description = "获取当前用户的消息列表，支持分类筛选")
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getMessageList(
            HttpServletRequest request,
            @Parameter(description = "消息类型（ALL/SYSTEM/PARTTIME/SECONDHAND）", example = "ALL")
            @RequestParam(required = false) String type,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = messageService.getMessageList(userId, type, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "标记消息为已读", description = "将单条消息标记为已读")
    @PutMapping("/{id}/read")
    public ApiResponse<String> markAsRead(
            HttpServletRequest request,
            @Parameter(description = "消息ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        messageService.markAsRead(userId, id);
        return ApiResponse.success("标记成功");
    }

    @Operation(
            summary = "批量标记已读", 
            description = "批量标记消息为已读",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "消息ID数组",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "示例",
                                    value = "{\"messageIds\": [1, 2, 3]}"
                            )
                    )
            )
    )
    @PutMapping("/batch-read")
    public ApiResponse<String> batchMarkAsRead(
            HttpServletRequest request,
            @RequestBody Map<String, List<Long>> requestBody) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        List<Long> messageIds = requestBody.get("messageIds");
        if (messageIds == null || messageIds.isEmpty()) {
            return ApiResponse.success("无需处理");
        }
        messageService.batchMarkAsRead(userId, messageIds);
        return ApiResponse.success("批量标记成功");
    }

    @Operation(summary = "获取未读消息数量", description = "获取当前用户的未读消息数量")
    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Object>> getUnreadCount(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Long count = messageService.getUnreadCount(userId);
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("unreadCount", count);
        return ApiResponse.success("查询成功", response);
    }
}
