package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.entity.ChatGroup;
import com.yourschool.campussystem.entity.ChatGroupMember;
import com.yourschool.campussystem.entity.ChatGroupMessage;
import com.yourschool.campussystem.entity.ChatConversation; // 引入聊天会话实体类，用于操作会话表记录
import com.yourschool.campussystem.entity.ChatMessage;
import com.yourschool.campussystem.entity.User;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.ChatGroupMapper;
import com.yourschool.campussystem.mapper.ChatGroupMemberMapper;
import com.yourschool.campussystem.mapper.ChatGroupMessageMapper;
import com.yourschool.campussystem.mapper.ChatConversationMapper;
import com.yourschool.campussystem.mapper.ChatMessageMapper; // 引入聊天消息Mapper，用于对消息表进行CRUD操作
import com.yourschool.campussystem.mapper.UserMapper;
import com.yourschool.campussystem.service.ChatService;
import lombok.RequiredArgsConstructor; // 引入Lombok注解，自动生成包含所有final字段的构造方法
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate; // 引入SimpMessagingTemplate，用于通过WebSocket向前端推送消息
import org.springframework.stereotype.Service; // 引入Service注解，标记业务实现类
import org.springframework.transaction.annotation.Transactional; // 引入Transactional注解，用于事务管理

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

    private final ChatConversationMapper conversationMapper; // 注入聊天会话Mapper，用于查询和更新会话数据
    private final ChatMessageMapper messageMapper; // 注入聊天消息Mapper，用于存储和查询聊天消息
    private final UserMapper userMapper; // 注入用户Mapper，用于查询用户基础信息
    private final SimpMessagingTemplate messagingTemplate; // 注入消息发送模板，用于通过WebSocket将新消息推送给前端
    private final ChatGroupMapper chatGroupMapper;
    private final ChatGroupMemberMapper chatGroupMemberMapper;
    private final ChatGroupMessageMapper chatGroupMessageMapper;

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
        List<Map<String, Object>> privateList = conversations.stream()
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

                    // Web端messages页面使用id作为唯一标识
                    item.put("id", conversation.getId());
                    item.put("conversationId", conversation.getId());
                    item.put("targetUserId", targetUserId);
                    item.put("targetUserName",
                            targetUser.getNickname() != null ? targetUser.getNickname() : targetUser.getUsername());
                    item.put("targetUserAvatar", targetUser.getAvatarUrl());
                    item.put("lastMessage", lastMessage);
                    item.put("lastMessageTime", conversation.getLastMessageTime());
                    item.put("unreadCount", unreadCount);
                    item.put("type", "private");

                    return item;
                })
                .filter(item -> item != null)
                .collect(Collectors.toList());

        // 查询用户加入的群聊（团队群等）
        LambdaQueryWrapper<ChatGroupMember> gmQ = new LambdaQueryWrapper<>();
        gmQ.eq(ChatGroupMember::getUserId, userId);
        List<ChatGroupMember> groups = chatGroupMemberMapper.selectList(gmQ);

        List<Map<String, Object>> groupList = groups.stream().map(gm -> {
            ChatGroup group = chatGroupMapper.selectById(gm.getGroupId());
            if (group == null || (group.getIsDeleted() != null && group.getIsDeleted() == 1)) {
                return null;
            }

            // 最近一条群消息
            ChatGroupMessage last = chatGroupMessageMapper.selectOne(
                    new LambdaQueryWrapper<ChatGroupMessage>()
                            .eq(ChatGroupMessage::getGroupId, group.getId())
                            .orderByDesc(ChatGroupMessage::getCreateTime)
                            .last("LIMIT 1"));

            // 群成员数
            Long memberCount = chatGroupMemberMapper.selectCount(
                    new LambdaQueryWrapper<ChatGroupMember>()
                            .eq(ChatGroupMember::getGroupId, group.getId()));

            Map<String, Object> item = new HashMap<>();
            // 用负数id避免与私聊会话id冲突；前端会把该id当conversationId传回
            item.put("id", -group.getId());
            item.put("type", "group");
            item.put("groupId", group.getId());
            item.put("groupMemberCount", memberCount != null ? memberCount.intValue() : 0);
            item.put("chatType", "team");
            item.put("targetUserName", group.getName());
            item.put("lastMessage", last != null ? last.getContent() : "暂无消息");
            item.put("lastMessageTime", last != null ? last.getCreateTime()
                    : (group.getUpdateTime() != null ? group.getUpdateTime() : group.getCreateTime()));
            item.put("unreadCount", 0);
            item.put("isPinned", false);
            item.put("isMuted", false);
            return item;
        }).filter(x -> x != null).collect(Collectors.toList());

        // 合并：按时间倒序（null时间放最后）
        List<Map<String, Object>> all = new java.util.ArrayList<>();
        all.addAll(groupList);
        all.addAll(privateList);
        all.sort((a, b) -> {
            Object ta = a.get("lastMessageTime");
            Object tb = b.get("lastMessageTime");
            long ma = toMillis(ta);
            long mb = toMillis(tb);
            return Long.compare(mb, ma);
        });

        return all;
    }

    @Override
    public Map<String, Object> getMessages(Long userId, Long conversationId, Long targetUserId, Integer page,
            Integer size) {
        // 群聊：前端用负数conversationId表示群ID
        if (conversationId != null && conversationId < 0) {
            Long groupId = -conversationId;

            // 校验是否在群内
            ChatGroupMember gm = chatGroupMemberMapper.selectOne(
                    new LambdaQueryWrapper<ChatGroupMember>()
                            .eq(ChatGroupMember::getGroupId, groupId)
                            .eq(ChatGroupMember::getUserId, userId));
            if (gm == null) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "无权访问此群聊");
            }

            Page<ChatGroupMessage> pageParam = new Page<>(page, size);
            Page<ChatGroupMessage> result = chatGroupMessageMapper.selectPage(
                    pageParam,
                    new LambdaQueryWrapper<ChatGroupMessage>()
                            .eq(ChatGroupMessage::getGroupId, groupId)
                            .orderByDesc(ChatGroupMessage::getCreateTime));

            List<Map<String, Object>> list = result.getRecords().stream().map(msg -> {
                User sender = userMapper.selectById(msg.getSenderId());
                Map<String, Object> item = new HashMap<>();
                item.put("id", msg.getId());
                item.put("senderId", msg.getSenderId());
                item.put("senderName",
                        sender != null ? (sender.getNickname() != null ? sender.getNickname() : sender.getUsername())
                                : "未知用户");
                item.put("content", msg.getContent());
                item.put("type", msg.getType());
                item.put("imageUrl", msg.getImageUrl());
                item.put("fileUrl", msg.getFileUrl());
                item.put("createTime", msg.getCreateTime());
                return item;
            }).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("records", list);
            response.put("total", result.getTotal());
            response.put("page", page);
            response.put("size", size);
            response.put("conversationId", conversationId);
            response.put("groupId", groupId);
            return response;
        }

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
                    item.put("senderName",
                            sender != null
                                    ? (sender.getNickname() != null ? sender.getNickname() : sender.getUsername())
                                    : "未知用户");
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
    public Map<String, Object> sendMessage(Long senderId, Long receiverId, String content, String type, String imageUrl,
            String fileUrl) {
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
        ChatMessage message = new ChatMessage(); // 创建一个新的聊天消息实体对象
        message.setConversationId(conversationId); // 设置所属会话ID，方便后续按会话查询消息
        message.setSenderId(senderId); // 设置发送者用户ID
        message.setReceiverId(receiverId); // 设置接收者用户ID
        message.setContent(content); // 设置消息文本内容
        message.setType(type); // 设置消息类型（TEXT/IMAGE/FILE等）
        message.setImageUrl(imageUrl); // 设置图片消息的图片URL（如果有）
        message.setFileUrl(fileUrl); // 设置文件消息的文件URL（如果有）
        message.setIsRead(0); // 初始标记为未读消息
        message.setCreateTime(LocalDateTime.now()); // 设置消息创建时间为当前时间

        messageMapper.insert(message); // 将新消息插入数据库中

        // 更新会话的最后消息信息
        conversation.setLastMessageId(message.getId()); // 记录该会话最后一条消息的ID
        conversation.setLastMessageTime(LocalDateTime.now()); // 更新会话最后消息时间为当前时间

        // 更新未读消息数（接收者的未读消息数+1）
        if (conversation.getUser1Id().equals(receiverId)) { // 如果接收者是会话中的user1
            conversation.setUser1UnreadCount(
                    (conversation.getUser1UnreadCount() != null ? conversation.getUser1UnreadCount() : 0) + 1); // user1未读数加1（为空时按0处理）
        } else { // 否则接收者是user2
            conversation.setUser2UnreadCount(
                    (conversation.getUser2UnreadCount() != null ? conversation.getUser2UnreadCount() : 0) + 1); // user2未读数加1（为空时按0处理）
        }

        conversation.setUpdateTime(LocalDateTime.now()); // 更新会话更新时间为当前时间
        conversationMapper.updateById(conversation); // 将更新后的会话信息写回数据库

        log.info("发送聊天消息: senderId={}, receiverId={}, conversationId={}", senderId, receiverId, conversationId); // 打印日志，方便追踪聊天消息发送情况

        // 通过WebSocket将新消息推送给接收者，实现“即时消息提醒”
        try { // 使用try-catch保证即使推送失败也不会影响主业务逻辑
            Map<String, Object> wsPayload = new HashMap<>(); // 创建一个Map用于封装推送给前端的消息数据
            wsPayload.put("messageId", message.getId()); // 放入消息ID，前端可用于去重或定位消息
            wsPayload.put("conversationId", conversationId); // 放入会话ID，前端可判断是否为当前会话
            wsPayload.put("senderId", senderId); // 放入发送者用户ID
            wsPayload.put("receiverId", receiverId); // 放入接收者用户ID
            wsPayload.put("content", content); // 放入消息文本内容
            wsPayload.put("type", type); // 放入消息类型
            wsPayload.put("createTime", message.getCreateTime()); // 放入消息创建时间

            String destination = "/topic/chat/" + receiverId; // 构造接收者专属订阅目的地路径（/topic/chat/{userId}）
            messagingTemplate.convertAndSend(destination, wsPayload); // 通过SimpMessagingTemplate向对应目的地推送消息
        } catch (Exception e) { // 捕获推送过程中可能出现的异常
            log.warn("通过WebSocket推送聊天消息失败: senderId={}, receiverId={}, conversationId={}", senderId, receiverId,
                    conversationId, e); // 打印警告日志但不中断主流程
        }

        // 返回消息信息
        Map<String, Object> response = new HashMap<>(); // 创建一个Map用于封装返回给前端的基本信息
        response.put("messageId", message.getId()); // 返回消息ID
        response.put("conversationId", conversationId); // 返回会话ID
        response.put("createTime", message.getCreateTime()); // 返回消息创建时间

        return response; // 将结果Map返回给Controller，由Controller再包装成统一响应格式
    }

    @Override
    @Transactional
    public Map<String, Object> sendGroupMessage(Long senderId, Long groupId, String content, String type,
            String imageUrl, String fileUrl) {
        if (content == null || content.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "消息内容不能为空");
        }
        if (type == null) {
            type = "TEXT";
        }

        // 校验群成员
        ChatGroupMember gm = chatGroupMemberMapper.selectOne(
                new LambdaQueryWrapper<ChatGroupMember>()
                        .eq(ChatGroupMember::getGroupId, groupId)
                        .eq(ChatGroupMember::getUserId, senderId));
        if (gm == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权在此群聊发送消息");
        }

        ChatGroupMessage message = new ChatGroupMessage();
        message.setGroupId(groupId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setType(type);
        message.setImageUrl(imageUrl);
        message.setFileUrl(fileUrl);
        message.setCreateTime(LocalDateTime.now());
        chatGroupMessageMapper.insert(message);

        // 预留：WebSocket推送到群聊订阅地址（按需扩展）
        try {
            Map<String, Object> wsPayload = new HashMap<>();
            wsPayload.put("messageId", message.getId());
            wsPayload.put("groupId", groupId);
            wsPayload.put("senderId", senderId);
            wsPayload.put("content", content);
            wsPayload.put("type", type);
            wsPayload.put("createTime", message.getCreateTime());
            messagingTemplate.convertAndSend("/topic/chat-group/" + groupId, wsPayload);
        } catch (Exception e) {
            log.warn("通过WebSocket推送群聊消息失败: groupId={}, senderId={}", groupId, senderId, e);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("messageId", message.getId());
        response.put("groupId", groupId);
        response.put("createTime", message.getCreateTime());
        return response;
    }

    @Override
    @Transactional
    public void markConversationAsRead(Long userId, Long conversationId) {
        // 群聊：暂不维护未读数，直接返回成功（兼容前端调用）
        if (conversationId != null && conversationId < 0) {
            return;
        }

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

    private long toMillis(Object value) {
        if (value == null)
            return 0L;
        try {
            if (value instanceof java.time.LocalDateTime) {
                return ((java.time.LocalDateTime) value).atZone(java.time.ZoneId.systemDefault()).toInstant()
                        .toEpochMilli();
            }
            if (value instanceof java.util.Date) {
                return ((java.util.Date) value).getTime();
            }
            if (value instanceof String) {
                return java.time.Instant.parse((String) value).toEpochMilli();
            }
        } catch (Exception ignore) {
            // ignore
        }
        return 0L;
    }
}
