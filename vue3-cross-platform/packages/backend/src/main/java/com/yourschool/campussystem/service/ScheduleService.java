package com.yourschool.campussystem.service;

import com.yourschool.campussystem.dto.*;
import com.yourschool.campussystem.enums.ScheduleStatusEnum;
import com.yourschool.campussystem.vo.PersonalScheduleVO;
import com.yourschool.campussystem.vo.TeamScheduleVO;
import com.yourschool.campussystem.vo.TeamVO;

import java.util.Map;

/**
 * 行程管理服务接口
 */
public interface ScheduleService {

    // ==================== 个人行程管理 ====================

    /**
     * 创建个人行程
     */
    PersonalScheduleVO createPersonalSchedule(Long userId, PersonalScheduleDTO scheduleDTO);

    /**
     * 获取个人行程列表
     */
    Map<String, Object> getPersonalSchedules(Long userId, ScheduleQueryDTO queryDTO);

    /**
     * 获取个人行程详情
     */
    PersonalScheduleVO getPersonalScheduleDetail(Long userId, Long scheduleId);

    /**
     * 修改个人行程
     */
    PersonalScheduleVO updatePersonalSchedule(Long userId, Long scheduleId, PersonalScheduleDTO updateDTO);

    /**
     * 删除个人行程
     */
    void deletePersonalSchedule(Long userId, Long scheduleId);

    /**
     * 更新行程状态
     */
    PersonalScheduleVO updatePersonalScheduleStatus(Long userId, Long scheduleId, ScheduleStatusEnum status);

    // ==================== 团队管理 ====================

    /**
     * 创建团队
     */
    TeamVO createTeam(Long userId, TeamCreateDTO teamDTO);

    /**
     * 获取我的团队列表
     */
    Map<String, Object> getMyTeams(Long userId, Integer page, Integer size);

    /**
     * 获取团队详情
     */
    TeamVO getTeamDetail(Long userId, Long teamId);

    /**
     * 邀请成员加入团队
     */
    void inviteTeamMember(Long userId, Long teamId, Long inviteUserId);

    /**
     * 处理团队邀请
     */
    void processTeamInvite(Long userId, Long inviteId, String action);

    /**
     * 通过邀请码加入团队
     */
    Map<String, Object> joinTeamByCode(Long userId, String inviteCode);

    /**
     * 获取团队邀请列表
     */
    Map<String, Object> getTeamInvitations(Long userId);

    /**
     * 移除团队成员
     */
    void removeTeamMember(Long userId, Long teamId, Long memberUserId);

    /**
     * 重新生成团队邀请码
     */
    String regenerateTeamInviteCode(Long userId, Long teamId);

    /**
     * 设置团队管理员（仅创建者可操作，最多4人）
     */
    TeamVO setTeamAdmins(Long userId, Long teamId, TeamAdminSetDTO dto);

    // ==================== 团队行程管理 ====================

    /**
     * 创建团队行程
     */
    TeamScheduleVO createTeamSchedule(Long userId, TeamScheduleDTO scheduleDTO);

    /**
     * 获取团队行程列表
     */
    Map<String, Object> getTeamSchedules(Long userId, Long teamId, ScheduleQueryDTO queryDTO);

    // ==================== 行程同步 ====================

    /**
     * 同步个人行程到团队
     */
    TeamScheduleVO syncPersonalToTeam(Long userId, Long personalScheduleId, Long teamId);

    /**
     * 同步团队行程到个人
     */
    PersonalScheduleVO syncTeamToPersonal(Long userId, Long teamScheduleId);

    /**
     * 批量同步团队行程到指定成员的个人行程表（创建者/管理员）
     */
    Map<String, Object> syncTeamScheduleToMembers(Long userId, Long teamId, Long teamScheduleId,
            TeamSyncMembersDTO dto);

    /**
     * 将某成员的课程表同步置入到指定成员（创建者/管理员）
     */
    Map<String, Object> syncCoursesToMembers(Long userId, Long teamId, TeamCourseSyncDTO dto);

    // ==================== 提醒与通知 ====================

    /**
     * 获取今日提醒
     */
    Map<String, Object> getTodayReminders(Long userId);

    /**
     * 标记为已提醒
     */
    void markAsReminded(Long userId, Long scheduleId);

    /**
     * 更新提醒状态
     */
    void updateReminderStatus(Long userId, Long scheduleId, Boolean enabled);

    /**
     * 删除提醒
     */
    void deleteReminder(Long userId, Long scheduleId);

    // ==================== 日历视图 ====================

    /**
     * 获取日历视图数据
     */
    Map<String, Object> getCalendarView(Long userId, Integer year, Integer month);

    // ==================== 课程表导入导出 ====================

    /**
     * 导入课程表（Excel）
     */
    Map<String, Object> importScheduleFromExcel(Long userId, org.springframework.web.multipart.MultipartFile file,
            String semester, Boolean overwrite);

    /**
     * 导出课程表（Excel）
     * 返回文件资源
     */
    org.springframework.core.io.Resource exportScheduleToExcel(Long userId, String semester);

    /**
     * 获取课程表模板
     * 返回模板文件资源
     */
    org.springframework.core.io.Resource getScheduleTemplate();

    /**
     * 手动录入导入课程表
     */
    Map<String, Object> importScheduleManual(Long userId, Map<String, Object> manualData);

    /**
     * 链接导入课程表
     */
    Map<String, Object> importScheduleFromLink(Long userId, String url, String username, String password);
}
