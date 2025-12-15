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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;

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
    }

    @Override
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getUserId, userId)
                .and(wrapper -> wrapper.eq(Message::getIsRead, 0).or().isNull(Message::getIsRead));

        return messageMapper.selectCount(queryWrapper);
    }
}
