package com.yourschool.campussystem.service;

import java.util.List;
import java.util.Map;

/**
 * 聊天服务接口
 */
public interface ChatService {

    /**
     * 获取聊天会话列表
     * @param userId 用户ID
     * @return 会话列表
     */
    List<Map<String, Object>> getConversations(Long userId);

    /**
     * 获取聊天消息列表
     * @param userId 用户ID
     * @param conversationId 会话ID（可选）
     * @param targetUserId 目标用户ID（可选，如果没有会话ID）
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表
     */
    Map<String, Object> getMessages(Long userId, Long conversationId, Long targetUserId, Integer page, Integer size);

    /**
     * 发送聊天消息
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param content 消息内容
     * @param type 消息类型（TEXT/IMAGE/FILE）
     * @param imageUrl 图片URL（如果是图片消息）
     * @param fileUrl 文件URL（如果是文件消息）
     * @return 消息信息
     */
    Map<String, Object> sendMessage(Long senderId, Long receiverId, String content, String type, String imageUrl, String fileUrl);

    /**
     * 标记消息为已读
     * @param userId 用户ID
     * @param conversationId 会话ID
     */
    void markConversationAsRead(Long userId, Long conversationId);
}
