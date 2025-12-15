package com.yourschool.campussystem.service;

import com.yourschool.campussystem.dto.ParttimeApplyDTO;
import com.yourschool.campussystem.dto.ParttimePublishDTO;
import com.yourschool.campussystem.dto.ParttimeQueryDTO;
import com.yourschool.campussystem.enums.ApplyStatusEnum;
import com.yourschool.campussystem.enums.ParttimeStatusEnum;
import com.yourschool.campussystem.vo.ApplyRecordVO;
import com.yourschool.campussystem.vo.ParttimeDetailVO;
import com.yourschool.campussystem.vo.ParttimeVO;

import java.util.List;
import java.util.Map;

/**
 * 兼职服务接口
 */
public interface ParttimeService {

    /**
     * 发布兼职
     */
    ParttimeVO publishParttime(Long userId, ParttimePublishDTO publishDTO);

    /**
     * 查询兼职列表（分页）
     */
    Map<String, Object> getParttimeList(ParttimeQueryDTO queryDTO, Long currentUserId);

    /**
     * 获取兼职详情
     */
    ParttimeDetailVO getParttimeDetail(Long parttimeId, Long currentUserId);

    /**
     * 报名兼职
     */
    void applyParttime(Long userId, ParttimeApplyDTO applyDTO);

    /**
     * 取消报名
     */
    void cancelApply(Long userId, Long applyId);

    /**
     * 获取我的报名记录
     */
    Map<String, Object> getMyApplications(Long userId, ApplyStatusEnum status, Integer page, Integer size);

    /**
     * 获取我发布的兼职
     */
    Map<String, Object> getMyPublished(Long userId, ParttimeStatusEnum status, Integer page, Integer size);

    /**
     * 获取兼职报名列表（仅发布者可见）
     */
    Map<String, Object> getParttimeApplications(Long userId, Long parttimeId, ApplyStatusEnum status, Integer page, Integer size);

    /**
     * 处理报名申请
     */
    void processApplication(Long userId, Long applicationId, ApplyStatusEnum result, String processNote);

    /**
     * 修改兼职信息
     */
    ParttimeVO updateParttime(Long userId, Long parttimeId, ParttimePublishDTO updateDTO);

    /**
     * 更新兼职状态
     */
    void updateParttimeStatus(Long userId, Long parttimeId, ParttimeStatusEnum status);

    /**
     * 删除兼职
     */
    void deleteParttime(Long userId, Long parttimeId);

    /**
     * 收藏兼职岗位
     */
    void favoriteParttime(Long userId, Long parttimeId);

    /**
     * 取消收藏兼职岗位
     */
    void cancelFavoriteParttime(Long userId, Long parttimeId);

    /**
     * 获取收藏列表
     */
    Map<String, Object> getParttimeFavorites(Long userId, Integer page, Integer size);

    /**
     * 清空收藏
     */
    void clearParttimeFavorites(Long userId);

    /**
     * 记录岗位浏览行为
     */
    void recordBrowse(Long userId, Long parttimeId);

    /**
     * 获取浏览记录列表
     */
    Map<String, Object> getBrowseHistory(Long userId, Integer page, Integer size);

    /**
     * 删除单条浏览记录
     */
    void deleteBrowseHistory(Long userId, Long historyId);

    /**
     * 清空浏览记录
     */
    void clearBrowseHistory(Long userId);
}
