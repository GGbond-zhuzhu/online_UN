package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.entity.Message;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.MessageMapper;
import com.yourschool.campussystem.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate; // 引入SimpMessagingTemplate，用于通过WebSocket向前端推送消息中心事件
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // 引入LocalDateTime，用于记录时间戳
import java.util.HashMap; // 引入HashMap，用于构建返回和推送的数据结构
import java.util.List; // 引入List接口，用于保存消息列表
import java.util.Map; // 引入Map接口，用于封装键值对数据
import java.util.stream.Collectors; // 引入Collectors，用于将流转换为列表

/**
 * 消息服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper; // 注入消息Mapper，用于对message表进行增删改查
    private final SimpMessagingTemplate messagingTemplate; // 注入SimpMessagingTemplate，用于通过WebSocket推送“未读消息变化”事件到前端

    @Override
    public Map<String, Object> getMessageList(Long userId, String type, Integer page, Integer size) {
        // 构建查询条件
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getUserId, userId);

        // 如果指定了消息类型，则过滤
        if (type != null && !type.isEmpty() && !"ALL".equalsIgnoreCase(type)) {
            queryWrapper.eq(Message::getType, type);
        }

        // 按创建时间倒序排列
        queryWrapper.orderByDesc(Message::getCreateTime);

        // 分页查询
        Page<Message> pageParam = new Page<>(page, size);
        Page<Message> result = messageMapper.selectPage(pageParam, queryWrapper);

        // 转换为VO
        List<Map<String, Object>> list = result.getRecords().stream()
                .map(message -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", message.getId());
                    item.put("type", message.getType());
                    item.put("title", message.getTitle());
                    item.put("content", message.getContent());
                    item.put("relatedId", message.getRelatedId());
                    item.put("read", message.getIsRead() != null && message.getIsRead() == 1);
                    item.put("createTime", message.getCreateTime());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("records", list);
        response.put("total", result.getTotal());
        response.put("page", page);
        response.put("size", size);

        return response;
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long messageId) {
        // 查询消息
        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "消息不存在");
        }

        // 验证消息是否属于当前用户
        if (!message.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此消息");
        }

        // 标记为已读
        if (message.getIsRead() == null || message.getIsRead() == 0) {
            message.setIsRead(1);
            message.setReadTime(LocalDateTime.now());
            messageMapper.updateById(message);
            log.info("消息标记为已读: userId={}, messageId={}", userId, messageId);
            // 已读状态发生变化后，通过WebSocket通知前端刷新未读消息数量
            pushUnreadEvent(userId); // 调用封装方法，向当前用户推送一次“未读消息状态变更”事件
        }
    }

    @Override
    @Transactional
    public void batchMarkAsRead(Long userId, List<Long> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) {
            return;
        }

        // 批量查询消息（使用in查询替代已弃用的selectBatchIds）
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Message::getId, messageIds);
        List<Message> messages = messageMapper.selectList(queryWrapper);

        // 验证所有消息都属于当前用户，并标记为已读
        LocalDateTime now = LocalDateTime.now();
        for (Message message : messages) {
            if (!message.getUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "无权操作此消息");
            }
            if (message.getIsRead() == null || message.getIsRead() == 0) {
                message.setIsRead(1);
                message.setReadTime(now);
                messageMapper.updateById(message);
            }
        }

        log.info("批量标记消息为已读: userId={}, count={}", userId, messages.size());
        // 批量操作完成后，通过WebSocket通知前端刷新未读消息数量
        pushUnreadEvent(userId); // 调用封装方法，向当前用户推送一次事件，提示前端刷新未读数
    }

    @Override
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getUserId, userId)
                .and(wrapper -> wrapper.eq(Message::getIsRead, 0).or().isNull(Message::getIsRead));

        return messageMapper.selectCount(queryWrapper);
    }

    @Override
    public void pushUnreadEvent(Long userId) {
        // 使用try-catch保证即使WebSocket推送失败，也不会影响主业务流程
        try {
            Long unreadCount = getUnreadCount(userId); // 调用内部方法获取当前用户的未读消息数量

            Map<String, Object> payload = new HashMap<>(); // 创建一个Map用于封装推送给前端的数据
            payload.put("event", "UNREAD_CHANGED"); // 标记事件类型为“未读数量发生变化”
            payload.put("unreadCount", unreadCount); // 放入当前未读消息数量
            payload.put("timestamp", LocalDateTime.now()); // 放入当前时间戳，方便前端调试或展示

            String destination = "/topic/message/" + userId; // 构造该用户专属的消息中心订阅通道路径
            messagingTemplate.convertAndSend(destination, payload); // 通过WebSocket将数据推送给订阅该通道的前端
        } catch (Exception e) {
            log.warn("通过WebSocket推送消息中心未读状态失败: userId={}", userId, e); // 打印警告日志，但不中断正常业务
        }
    }
}
