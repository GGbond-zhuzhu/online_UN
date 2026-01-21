package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.dto.ChatSendDTO;
import com.yourschool.campussystem.service.ChatService;
import com.yourschool.campussystem.util.UserContextUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天功能Controller
 * 处理用户之间的聊天会话和消息
 */
@RestController
@RequestMapping("/api/chat")
@Tag(name = "聊天功能", description = "用户聊天：会话列表、消息列表、发送消息等")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "获取聊天会话列表", description = "获取当前用户的聊天会话列表")
    @GetMapping("/conversations")
    public ApiResponse<Map<String, Object>> getConversations(
            HttpServletRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        List<Map<String, Object>> all = chatService.getConversations(userId);

        int total = all != null ? all.size() : 0;
        int p = page != null && page > 0 ? page : 1;
        int s = size != null && size > 0 ? size : 20;
        int from = Math.max(0, (p - 1) * s);
        int to = Math.min(total, from + s);
        List<Map<String, Object>> records = (from >= to || all == null) ? new ArrayList<>() : all.subList(from, to);

        Map<String, Object> response = new HashMap<>();
        response.put("records", records);
        response.put("total", total);
        response.put("page", p);
        response.put("size", s);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "获取聊天消息列表", description = "获取指定会话的聊天消息列表")
    @GetMapping("/messages")
    public ApiResponse<Map<String, Object>> getMessages(
            HttpServletRequest request,
            @Parameter(description = "会话ID", example = "1")
            @RequestParam(required = false) Long conversationId,
            @Parameter(description = "目标用户ID（如果没有会话ID）", example = "123")
            @RequestParam(required = false) Long targetUserId,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = chatService.getMessages(userId, conversationId, targetUserId, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "发送消息", description = "发送聊天消息")
    @PostMapping("/send")
    public ApiResponse<Map<String, Object>> sendMessage(
            HttpServletRequest request,
            @Parameter(description = "目标用户ID", required = true)
            @RequestParam Long targetUserId,
            @Parameter(description = "消息内容", required = true)
            @RequestParam String content,
            @Parameter(description = "消息类型（TEXT/IMAGE/FILE）", example = "TEXT")
            @RequestParam(defaultValue = "TEXT") String type,
            @Parameter(description = "图片URL（如果是图片消息）")
            @RequestParam(required = false) String imageUrl,
            @Parameter(description = "文件URL（如果是文件消息）")
            @RequestParam(required = false) String fileUrl) {
        Long senderId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = chatService.sendMessage(senderId, targetUserId, content, type, imageUrl, fileUrl);
        return ApiResponse.success("发送成功", response);
    }

    @Operation(summary = "发送消息（JSON）", description = "兼容Web端/messages页面：私聊或群聊发送消息")
    @PostMapping("/messages")
    public ApiResponse<Map<String, Object>> sendMessageJson(
            HttpServletRequest request,
            @Valid @RequestBody ChatSendDTO body) {
        Long senderId = UserContextUtils.getUserIdRequired(request);

        if (body.getGroupId() != null) {
            Map<String, Object> response = chatService.sendGroupMessage(
                    senderId,
                    body.getGroupId(),
                    body.getContent(),
                    body.getType(),
                    body.getImageUrl(),
                    body.getFileUrl());
            return ApiResponse.success("发送成功", response);
        }

        if (body.getTargetUserId() == null) {
            return ApiResponse.error(400, "targetUserId不能为空（私聊发送需要指定目标用户）");
        }

        Map<String, Object> response = chatService.sendMessage(
                senderId,
                body.getTargetUserId(),
                body.getContent(),
                body.getType(),
                body.getImageUrl(),
                body.getFileUrl());
        return ApiResponse.success("发送成功", response);
    }

    @Operation(summary = "标记会话消息为已读", description = "将指定会话的所有消息标记为已读")
    @PutMapping("/conversations/{conversationId}/read")
    public ApiResponse<String> markConversationAsRead(
            HttpServletRequest request,
            @Parameter(description = "会话ID", example = "1", required = true)
            @PathVariable Long conversationId) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        chatService.markConversationAsRead(userId, conversationId);
        return ApiResponse.success("标记成功");
    }
}
