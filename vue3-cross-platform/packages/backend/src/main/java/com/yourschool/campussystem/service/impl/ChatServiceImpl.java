package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.entity.ChatConversation;
import com.yourschool.campussystem.entity.ChatMessage;
import com.yourschool.campussystem.entity.User;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.ChatConversationMapper;
import com.yourschool.campussystem.mapper.ChatMessageMapper;
import com.yourschool.campussystem.mapper.UserMapper;
import com.yourschool.campussystem.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 聊天服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatConversationMapper conversationMapper;
    private final ChatMessageMapper messageMapper;
    private final UserMapper userMapper;

    @Override
    public List<Map<String, Object>> getConversations(Long userId) {
        // 查询用户参与的所有会话（user1_id或user2_id等于userId）
        LambdaQueryWrapper<ChatConversation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(ChatConversation::getUser1Id, userId)
                        .or()
                        .eq(ChatConversation::getUser2Id, userId))
                .orderByDesc(ChatConversation::getLastMessageTime);

        List<ChatConversation> conversations = conversationMapper.selectList(queryWrapper);

        // 转换为VO
        return conversations.stream()
                .map(conversation -> {
                    Map<String, Object> item = new HashMap<>();
                    
                    // 确定目标用户ID
                    Long targetUserId = conversation.getUser1Id().equals(userId) 
                            ? conversation.getUser2Id() 
                            : conversation.getUser1Id();
                    
                    // 查询目标用户信息
                    User targetUser = userMapper.selectById(targetUserId);
                    if (targetUser == null) {
                        log.warn("目标用户不存在: userId={}", targetUserId);
                        return null;
                    }

                    // 获取未读消息数
                    int unreadCount = conversation.getUser1Id().equals(userId) 
                            ? (conversation.getUser1UnreadCount() != null ? conversation.getUser1UnreadCount() : 0)
                            : (conversation.getUser2UnreadCount() != null ? conversation.getUser2UnreadCount() : 0);

                    // 获取最后一条消息
                    String lastMessage = null;
                    if (conversation.getLastMessageId() != null) {
                        ChatMessage lastMsg = messageMapper.selectById(conversation.getLastMessageId());
                        if (lastMsg != null) {
                            lastMessage = lastMsg.getContent();
                        }
                    }

                    item.put("conversationId", conversation.getId());
                    item.put("targetUserId", targetUserId);
                    item.put("targetUserName", targetUser.getNickname() != null ? targetUser.getNickname() : targetUser.getUsername());
                    item.put("targetUserAvatar", targetUser.getAvatarUrl());
                    item.put("lastMessage", lastMessage);
                    item.put("lastMessageTime", conversation.getLastMessageTime());
                    item.put("unreadCount", unreadCount);

                    return item;
                })
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getMessages(Long userId, Long conversationId, Long targetUserId, Integer page, Integer size) {
        // 如果没有会话ID，需要先查找或创建会话
        if (conversationId == null) {
            if (targetUserId == null) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "会话ID和目标用户ID至少需要一个");
            }
            conversationId = findOrCreateConversation(userId, targetUserId);
        }

        // 验证会话是否属于当前用户
        ChatConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "会话不存在");
        }
        if (!conversation.getUser1Id().equals(userId) && !conversation.getUser2Id().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权访问此会话");
        }

        // 查询消息列表
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId)
                .orderByDesc(ChatMessage::getCreateTime);

        Page<ChatMessage> pageParam = new Page<>(page, size);
        Page<ChatMessage> result = messageMapper.selectPage(pageParam, queryWrapper);

        // 转换为VO
        List<Map<String, Object>> list = result.getRecords().stream()
                .map(message -> {
                    // 查询发送者信息
                    User sender = userMapper.selectById(message.getSenderId());
                    
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", message.getId());
                    item.put("senderId", message.getSenderId());
                    item.put("senderName", sender != null ? (sender.getNickname() != null ? sender.getNickname() : sender.getUsername()) : "未知用户");
                    item.put("content", message.getContent());
                    item.put("type", message.getType());
                    item.put("imageUrl", message.getImageUrl());
                    item.put("fileUrl", message.getFileUrl());
                    item.put("createTime", message.getCreateTime());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("records", list);
        response.put("total", result.getTotal());
        response.put("page", page);
        response.put("size", size);
        response.put("conversationId", conversationId);

        return response;
    }

    @Override
    @Transactional
    public Map<String, Object> sendMessage(Long senderId, Long receiverId, String content, String type, String imageUrl, String fileUrl) {
        // 验证参数
        if (content == null || content.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "消息内容不能为空");
        }
        if (type == null) {
            type = "TEXT";
        }

        // 查找或创建会话
        Long conversationId = findOrCreateConversation(senderId, receiverId);
        ChatConversation conversation = conversationMapper.selectById(conversationId);

        // 创建消息
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setType(type);
        message.setImageUrl(imageUrl);
        message.setFileUrl(fileUrl);
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());

        messageMapper.insert(message);

        // 更新会话的最后消息信息
        conversation.setLastMessageId(message.getId());
        conversation.setLastMessageTime(LocalDateTime.now());
        
        // 更新未读消息数（接收者的未读消息数+1）
        if (conversation.getUser1Id().equals(receiverId)) {
            conversation.setUser1UnreadCount((conversation.getUser1UnreadCount() != null ? conversation.getUser1UnreadCount() : 0) + 1);
        } else {
            conversation.setUser2UnreadCount((conversation.getUser2UnreadCount() != null ? conversation.getUser2UnreadCount() : 0) + 1);
        }
        
        conversation.setUpdateTime(LocalDateTime.now());
        conversationMapper.updateById(conversation);

        log.info("发送聊天消息: senderId={}, receiverId={}, conversationId={}", senderId, receiverId, conversationId);

        // 返回消息信息
        Map<String, Object> response = new HashMap<>();
        response.put("messageId", message.getId());
        response.put("conversationId", conversationId);
        response.put("createTime", message.getCreateTime());

        return response;
    }

    @Override
    @Transactional
    public void markConversationAsRead(Long userId, Long conversationId) {
        // 查询会话
        ChatConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "会话不存在");
        }

        // 验证会话是否属于当前用户
        if (!conversation.getUser1Id().equals(userId) && !conversation.getUser2Id().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权访问此会话");
        }

        // 将当前用户在此会话中的未读消息数清零
        if (conversation.getUser1Id().equals(userId)) {
            conversation.setUser1UnreadCount(0);
        } else {
            conversation.setUser2UnreadCount(0);
        }
        conversationMapper.updateById(conversation);

        // 将所有未读消息标记为已读
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId)
                .eq(ChatMessage::getReceiverId, userId)
                .and(wrapper -> wrapper.eq(ChatMessage::getIsRead, 0).or().isNull(ChatMessage::getIsRead));

        List<ChatMessage> unreadMessages = messageMapper.selectList(queryWrapper);
        LocalDateTime now = LocalDateTime.now();
        for (ChatMessage message : unreadMessages) {
            message.setIsRead(1);
            message.setReadTime(now);
            messageMapper.updateById(message);
        }

        log.info("标记会话消息为已读: userId={}, conversationId={}, count={}", userId, conversationId, unreadMessages.size());
    }

    /**
     * 查找或创建会话
     */
    private Long findOrCreateConversation(Long user1Id, Long user2Id) {
        // 确保user1Id < user2Id，以便唯一性
        if (user1Id > user2Id) {
            Long temp = user1Id;
            user1Id = user2Id;
            user2Id = temp;
        }

        // 查找是否已存在会话
        LambdaQueryWrapper<ChatConversation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatConversation::getUser1Id, user1Id)
                .eq(ChatConversation::getUser2Id, user2Id);

        ChatConversation conversation = conversationMapper.selectOne(queryWrapper);

        if (conversation == null) {
            // 创建新会话
            conversation = new ChatConversation();
            conversation.setUser1Id(user1Id);
            conversation.setUser2Id(user2Id);
            conversation.setUser1UnreadCount(0);
            conversation.setUser2UnreadCount(0);
            conversation.setCreateTime(LocalDateTime.now());
            conversation.setUpdateTime(LocalDateTime.now());
            conversationMapper.insert(conversation);
            log.info("创建新会话: user1Id={}, user2Id={}, conversationId={}", user1Id, user2Id, conversation.getId());
        }

        return conversation.getId();
    }
}
