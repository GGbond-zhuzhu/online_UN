package com.yourschool.campussystem.service;

import java.util.Map;

/**
 * 消息服务接口
 */
public interface MessageService {

    /**
     * 获取消息列表
     * @param userId 用户ID
     * @param type 消息类型（ALL/SYSTEM/PARTTIME/SECONDHAND）
     * @param page 页码
     * @param size 每页大小
     * @return 消息列表
     */
    Map<String, Object> getMessageList(Long userId, String type, Integer page, Integer size);

    /**
     * 标记消息为已读
     * @param userId 用户ID
     * @param messageId 消息ID
     */
    void markAsRead(Long userId, Long messageId);

    /**
     * 批量标记消息为已读
     * @param userId 用户ID
     * @param messageIds 消息ID数组
     */
    void batchMarkAsRead(Long userId, java.util.List<Long> messageIds);

    /**
     * 获取未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 通过WebSocket推送一次“未读消息状态变更”事件
     * @param userId 用户ID
     */
    void pushUnreadEvent(Long userId);
}
