package com.yourschool.campussystem.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourschool.campussystem.dto.*;
import com.yourschool.campussystem.entity.*;
import com.yourschool.campussystem.enums.RemindTypeEnum;
import com.yourschool.campussystem.enums.ScheduleStatusEnum;
import com.yourschool.campussystem.enums.ScheduleTypeEnum;
import com.yourschool.campussystem.enums.TeamRoleEnum;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.*;
import com.yourschool.campussystem.service.ScheduleService;
import com.yourschool.campussystem.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 行程管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl extends ServiceImpl<PersonalScheduleMapper, PersonalSchedule> implements ScheduleService {

    private final PersonalScheduleMapper personalScheduleMapper;
    private final TeamMapper teamMapper;
    private final TeamMemberMapper teamMemberMapper;
    private final TeamScheduleMapper teamScheduleMapper;
    private final TeamScheduleAttendeeMapper attendeeMapper;
    private final UserMapper userMapper;
    private final UniversityMapper universityMapper;

    // ==================== 个人行程管理 ====================

    @Override
    @Transactional
    public PersonalScheduleVO createPersonalSchedule(Long userId, PersonalScheduleDTO scheduleDTO) {
        PersonalSchedule schedule = new PersonalSchedule();
        schedule.setTitle(scheduleDTO.getTitle());
        schedule.setDescription(scheduleDTO.getDescription());
        schedule.setStartTime(scheduleDTO.getStartTime());
        schedule.setEndTime(scheduleDTO.getEndTime());
        schedule.setType(scheduleDTO.getType() != null ? scheduleDTO.getType() : ScheduleTypeEnum.OTHER);
        schedule.setStatus(scheduleDTO.getStatus() != null ? scheduleDTO.getStatus() : ScheduleStatusEnum.PENDING);
        schedule.setLocation(scheduleDTO.getLocation());
        schedule.setIsAllDay(scheduleDTO.getIsAllDay() != null ? scheduleDTO.getIsAllDay() : false);
        schedule.setRemindType(scheduleDTO.getRemindType());
        schedule.setCustomRemindMinutes(scheduleDTO.getCustomRemindMinutes());
        schedule.setIsRepeat(scheduleDTO.getIsRepeat() != null ? scheduleDTO.getIsRepeat() : false);
        schedule.setRepeatRule(scheduleDTO.getRepeatRule());
        schedule.setTag(scheduleDTO.getTag());
        schedule.setCreatorId(userId);

        personalScheduleMapper.insert(schedule);

        User creator = userMapper.selectById(userId);
        return convertToPersonalVO(schedule, creator);
    }

    @Override
    public Map<String, Object> getPersonalSchedules(Long userId, ScheduleQueryDTO queryDTO) {
        LambdaQueryWrapper<PersonalSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PersonalSchedule::getCreatorId, userId)
                .eq(PersonalSchedule::getIsDeleted, 0);

        // 日期范围筛选
        if (queryDTO.getStartDate() != null) {
            queryWrapper.ge(PersonalSchedule::getStartTime, queryDTO.getStartDate().atStartOfDay());
        }
        if (queryDTO.getEndDate() != null) {
            queryWrapper.le(PersonalSchedule::getEndTime, queryDTO.getEndDate().atTime(23, 59, 59));
        }

        // 类型筛选
        if (queryDTO.getType() != null) {
            queryWrapper.eq(PersonalSchedule::getType, queryDTO.getType());
        }

        // 状态筛选
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(PersonalSchedule::getStatus, queryDTO.getStatus());
        }

        // 关键词搜索
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(PersonalSchedule::getTitle, queryDTO.getKeyword())
                    .or()
                    .like(PersonalSchedule::getDescription, queryDTO.getKeyword())
            );
        }

        queryWrapper.orderByDesc(PersonalSchedule::getStartTime);

        Page<PersonalSchedule> pageObj = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        Page<PersonalSchedule> result = personalScheduleMapper.selectPage(pageObj, queryWrapper);

        List<PersonalScheduleVO> voList = result.getRecords().stream()
                .map(schedule -> {
                    User creator = userMapper.selectById(schedule.getCreatorId());
                    return convertToPersonalVO(schedule, creator);
                })
                .collect(Collectors.toList());

        // 统计今日和待处理数量
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<PersonalSchedule> todayQuery = new LambdaQueryWrapper<>();
        todayQuery.eq(PersonalSchedule::getCreatorId, userId)
                .eq(PersonalSchedule::getIsDeleted, 0)
                .ge(PersonalSchedule::getStartTime, today.atStartOfDay())
                .lt(PersonalSchedule::getStartTime, today.plusDays(1).atStartOfDay());
        int todayCount = personalScheduleMapper.selectCount(todayQuery).intValue();

        LambdaQueryWrapper<PersonalSchedule> pendingQuery = new LambdaQueryWrapper<>();
        pendingQuery.eq(PersonalSchedule::getCreatorId, userId)
                .eq(PersonalSchedule::getIsDeleted, 0)
                .eq(PersonalSchedule::getStatus, ScheduleStatusEnum.PENDING);
        int pendingCount = personalScheduleMapper.selectCount(pendingQuery).intValue();

        Map<String, Object> response = new HashMap<>();
        response.put("list", voList);
        response.put("page", queryDTO.getPage());
        response.put("size", queryDTO.getSize());
        response.put("total", result.getTotal());
        response.put("todayCount", todayCount);
        response.put("pendingCount", pendingCount);

        return response;
    }

    @Override
    public PersonalScheduleVO getPersonalScheduleDetail(Long userId, Long scheduleId) {
        PersonalSchedule schedule = personalScheduleMapper.selectById(scheduleId);
        if (schedule == null || schedule.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_EXIST);
        }

        if (!schedule.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        User creator = userMapper.selectById(userId);
        return convertToPersonalVO(schedule, creator);
    }

    @Override
    @Transactional
    public PersonalScheduleVO updatePersonalSchedule(Long userId, Long scheduleId, PersonalScheduleDTO updateDTO) {
        PersonalSchedule schedule = personalScheduleMapper.selectById(scheduleId);
        if (schedule == null || schedule.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_EXIST);
        }

        if (!schedule.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        schedule.setTitle(updateDTO.getTitle());
        schedule.setDescription(updateDTO.getDescription());
        schedule.setStartTime(updateDTO.getStartTime());
        schedule.setEndTime(updateDTO.getEndTime());
        schedule.setType(updateDTO.getType());
        schedule.setStatus(updateDTO.getStatus());
        schedule.setLocation(updateDTO.getLocation());
        schedule.setIsAllDay(updateDTO.getIsAllDay());
        schedule.setRemindType(updateDTO.getRemindType());
        schedule.setCustomRemindMinutes(updateDTO.getCustomRemindMinutes());
        schedule.setIsRepeat(updateDTO.getIsRepeat());
        schedule.setRepeatRule(updateDTO.getRepeatRule());
        schedule.setTag(updateDTO.getTag());

        personalScheduleMapper.updateById(schedule);

        User creator = userMapper.selectById(userId);
        return convertToPersonalVO(schedule, creator);
    }

    @Override
    @Transactional
    public void deletePersonalSchedule(Long userId, Long scheduleId) {
        PersonalSchedule schedule = personalScheduleMapper.selectById(scheduleId);
        if (schedule == null || schedule.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_EXIST);
        }

        if (!schedule.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        schedule.setIsDeleted(1);
        personalScheduleMapper.updateById(schedule);
    }

    @Override
    @Transactional
    public PersonalScheduleVO updatePersonalScheduleStatus(Long userId, Long scheduleId, ScheduleStatusEnum status) {
        PersonalSchedule schedule = personalScheduleMapper.selectById(scheduleId);
        if (schedule == null || schedule.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_EXIST);
        }

        if (!schedule.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        schedule.setStatus(status);
        personalScheduleMapper.updateById(schedule);

        User creator = userMapper.selectById(userId);
        return convertToPersonalVO(schedule, creator);
    }

    // ==================== 团队管理 ====================

    @Override
    @Transactional
    public TeamVO createTeam(Long userId, TeamCreateDTO teamDTO) {
        User creator = userMapper.selectById(userId);
        if (creator == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        Team team = new Team();
        team.setName(teamDTO.getName());
        team.setDescription(teamDTO.getDescription());
        team.setAvatar(teamDTO.getAvatar());
        team.setCreatorId(userId);
        team.setSchoolId(creator.getSchoolId());
        team.setNeedApprove(teamDTO.getNeedApprove() != null ? teamDTO.getNeedApprove() : true);
        team.setMaxMembers(teamDTO.getMaxMembers() != null ? teamDTO.getMaxMembers() : 50);
        team.setMemberCount(1); // 创建者自己
        // 生成邀请码（8位随机字符串，包含数字和大写字母）
        team.setInviteCode(generateInviteCode());

        teamMapper.insert(team);

        // 添加创建者为成员
        TeamMember creatorMember = new TeamMember();
        creatorMember.setTeamId(team.getId());
        creatorMember.setUserId(userId);
        creatorMember.setRole(TeamRoleEnum.CREATOR);
        teamMemberMapper.insert(creatorMember);

        // 添加初始成员
        if (teamDTO.getMemberIds() != null && !teamDTO.getMemberIds().isEmpty()) {
            for (Long memberId : teamDTO.getMemberIds()) {
                if (!memberId.equals(userId)) {
                    User member = userMapper.selectById(memberId);
                    if (member != null && member.getSchoolId() != null && 
                        member.getSchoolId().equals(creator.getSchoolId())) {
                        TeamMember teamMember = new TeamMember();
                        teamMember.setTeamId(team.getId());
                        teamMember.setUserId(memberId);
                        teamMember.setRole(TeamRoleEnum.MEMBER);
                        teamMemberMapper.insert(teamMember);
                        team.setMemberCount(team.getMemberCount() + 1);
                    }
                }
            }
            teamMapper.updateById(team);
        }

        return convertToTeamVO(team, creator, userId);
    }

    @Override
    public Map<String, Object> getMyTeams(Long userId, Integer page, Integer size) {
        // 查询用户加入的团队
        LambdaQueryWrapper<TeamMember> memberQuery = new LambdaQueryWrapper<>();
        memberQuery.eq(TeamMember::getUserId, userId);
        List<TeamMember> members = teamMemberMapper.selectList(memberQuery);
        List<Long> teamIds = members.stream().map(TeamMember::getTeamId).collect(Collectors.toList());

        if (teamIds.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("list", new ArrayList<>());
            response.put("total", 0L);
            return response;
        }

        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Team::getId, teamIds)
                .eq(Team::getIsDeleted, 0)
                .orderByDesc(Team::getCreateTime);

        Page<Team> pageObj = new Page<>(page, size);
        Page<Team> result = teamMapper.selectPage(pageObj, queryWrapper);

        List<TeamVO> voList = result.getRecords().stream()
                .map(team -> {
                    User creator = userMapper.selectById(team.getCreatorId());
                    // 查询用户在团队中的角色
                    TeamMember member = teamMemberMapper.selectOne(
                            new LambdaQueryWrapper<TeamMember>()
                                    .eq(TeamMember::getTeamId, team.getId())
                                    .eq(TeamMember::getUserId, userId)
                    );
                    TeamRoleEnum userRole = member != null ? member.getRole() : null;
                    return convertToTeamVO(team, creator, userId, userRole);
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", voList);
        response.put("total", result.getTotal());

        return response;
    }

    @Override
    public TeamVO getTeamDetail(Long userId, Long teamId) {
        Team team = teamMapper.selectById(teamId);
        if (team == null || team.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.TEAM_NOT_EXIST);
        }

        // 检查用户是否是团队成员
        LambdaQueryWrapper<TeamMember> memberQuery = new LambdaQueryWrapper<>();
        memberQuery.eq(TeamMember::getTeamId, teamId)
                .eq(TeamMember::getUserId, userId);
        TeamMember member = teamMemberMapper.selectOne(memberQuery);
        if (member == null) {
            throw new BusinessException(ErrorCode.TEAM_MEMBER_NOT_EXIST);
        }

        User creator = userMapper.selectById(team.getCreatorId());
        TeamVO teamVO = convertToTeamVO(team, creator, userId, member.getRole());

        // 查询成员列表
        LambdaQueryWrapper<TeamMember> membersQuery = new LambdaQueryWrapper<>();
        membersQuery.eq(TeamMember::getTeamId, teamId);
        List<TeamMember> members = teamMemberMapper.selectList(membersQuery);
        List<TeamMemberVO> memberVOList = members.stream()
                .map(m -> {
                    User user = userMapper.selectById(m.getUserId());
                    TeamMemberVO memberVO = new TeamMemberVO();
                    memberVO.setUserId(m.getUserId());
                    memberVO.setUserName(user != null ? 
                            (user.getNickname() != null ? user.getNickname() : user.getUsername()) : null);
                    memberVO.setAvatar(user != null ? user.getAvatarUrl() : null);
                    memberVO.setRole(m.getRole());
                    memberVO.setJoinTime(m.getJoinTime());
                    memberVO.setIsOnline(false); // 实际应该从在线状态服务获取
                    return memberVO;
                })
                .collect(Collectors.toList());
        teamVO.setMembers(memberVOList);

        // 查询最近的团队行程
        LambdaQueryWrapper<TeamSchedule> scheduleQuery = new LambdaQueryWrapper<>();
        scheduleQuery.eq(TeamSchedule::getTeamId, teamId)
                .eq(TeamSchedule::getIsDeleted, 0)
                .ge(TeamSchedule::getStartTime, LocalDateTime.now())
                .orderByAsc(TeamSchedule::getStartTime)
                .last("LIMIT 1");
        TeamSchedule recentSchedule = teamScheduleMapper.selectOne(scheduleQuery);
        if (recentSchedule != null) {
            teamVO.setRecentSchedule(convertToTeamScheduleVO(recentSchedule, team));
        }

        return teamVO;
    }

    @Override
    @Transactional
    public void inviteTeamMember(Long userId, Long teamId, Long inviteUserId) {
        Team team = teamMapper.selectById(teamId);
        if (team == null || team.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.TEAM_NOT_EXIST);
        }

        // 检查权限（创建者或管理员）
        TeamMember inviter = teamMemberMapper.selectOne(
                new LambdaQueryWrapper<TeamMember>()
                        .eq(TeamMember::getTeamId, teamId)
                        .eq(TeamMember::getUserId, userId)
        );
        if (inviter == null || 
            (inviter.getRole() != TeamRoleEnum.CREATOR && inviter.getRole() != TeamRoleEnum.ADMIN)) {
            throw new BusinessException(ErrorCode.TEAM_ROLE_DENIED);
        }

        // 检查被邀请用户是否同校
        User inviteUser = userMapper.selectById(inviteUserId);
        if (inviteUser == null || inviteUser.getSchoolId() == null || 
            !inviteUser.getSchoolId().equals(team.getSchoolId())) {
            throw new BusinessException(ErrorCode.PARTTIME_NOT_SAME_SCHOOL);
        }

        // 检查是否已是成员
        LambdaQueryWrapper<TeamMember> existQuery = new LambdaQueryWrapper<>();
        existQuery.eq(TeamMember::getTeamId, teamId)
                .eq(TeamMember::getUserId, inviteUserId);
        if (teamMemberMapper.selectCount(existQuery) > 0) {
            throw new BusinessException(ErrorCode.TEAM_MEMBER_EXIST);
        }

        // 检查是否已满员
        if (team.getMemberCount() >= team.getMaxMembers()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        // 添加成员
        TeamMember newMember = new TeamMember();
        newMember.setTeamId(teamId);
        newMember.setUserId(inviteUserId);
        newMember.setRole(TeamRoleEnum.MEMBER);
        teamMemberMapper.insert(newMember);

        team.setMemberCount(team.getMemberCount() + 1);
        teamMapper.updateById(team);
    }

    @Override
    @Transactional
    public void processTeamInvite(Long userId, Long inviteId, String action) {
        // 这里简化处理，实际应该有邀请表
        // 暂时直接添加成员
        if ("accept".equals(action)) {
            // 接受邀请的逻辑
        } else {
            // 拒绝邀请的逻辑
        }
    }

    @Override
    @Transactional
    public void removeTeamMember(Long userId, Long teamId, Long memberUserId) {
        Team team = teamMapper.selectById(teamId);
        if (team == null || team.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.TEAM_NOT_EXIST);
        }

        // 检查权限
        TeamMember operator = teamMemberMapper.selectOne(
                new LambdaQueryWrapper<TeamMember>()
                        .eq(TeamMember::getTeamId, teamId)
                        .eq(TeamMember::getUserId, userId)
        );
        if (operator == null || 
            (operator.getRole() != TeamRoleEnum.CREATOR && operator.getRole() != TeamRoleEnum.ADMIN)) {
            throw new BusinessException(ErrorCode.TEAM_ROLE_DENIED);
        }

        // 不能移除创建者
        if (memberUserId.equals(team.getCreatorId())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        // 删除成员
        LambdaQueryWrapper<TeamMember> deleteQuery = new LambdaQueryWrapper<>();
        deleteQuery.eq(TeamMember::getTeamId, teamId)
                .eq(TeamMember::getUserId, memberUserId);
        teamMemberMapper.delete(deleteQuery);

        team.setMemberCount(team.getMemberCount() - 1);
        teamMapper.updateById(team);
    }

    // ==================== 团队行程管理 ====================

    @Override
    @Transactional
    public TeamScheduleVO createTeamSchedule(Long userId, TeamScheduleDTO scheduleDTO) {
        // 检查用户是否是团队成员
        TeamMember member = teamMemberMapper.selectOne(
                new LambdaQueryWrapper<TeamMember>()
                        .eq(TeamMember::getTeamId, scheduleDTO.getTeamId())
                        .eq(TeamMember::getUserId, userId)
        );
        if (member == null) {
            throw new BusinessException(ErrorCode.TEAM_MEMBER_NOT_EXIST);
        }

        Team team = teamMapper.selectById(scheduleDTO.getTeamId());
        if (team == null || team.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.TEAM_NOT_EXIST);
        }

        TeamSchedule schedule = new TeamSchedule();
        schedule.setTeamId(scheduleDTO.getTeamId());
        schedule.setTitle(scheduleDTO.getTitle());
        schedule.setDescription(scheduleDTO.getDescription());
        schedule.setStartTime(scheduleDTO.getStartTime());
        schedule.setEndTime(scheduleDTO.getEndTime());
        schedule.setLocation(scheduleDTO.getLocation());
        schedule.setNeedConfirm(scheduleDTO.getNeedConfirm() != null ? scheduleDTO.getNeedConfirm() : false);
        schedule.setStatus(ScheduleStatusEnum.PENDING);
        schedule.setCreatorId(userId);

        teamScheduleMapper.insert(schedule);

        // 获取所有团队成员（用于添加参会人员和同步个人行程）
        LambdaQueryWrapper<TeamMember> membersQuery = new LambdaQueryWrapper<>();
        membersQuery.eq(TeamMember::getTeamId, scheduleDTO.getTeamId());
        List<TeamMember> allMembers = teamMemberMapper.selectList(membersQuery);
        List<Long> memberUserIds = allMembers.stream()
                .map(TeamMember::getUserId)
                .collect(Collectors.toList());

        // 添加参会人员
        if (scheduleDTO.getAttendeeIds() != null && !scheduleDTO.getAttendeeIds().isEmpty()) {
            for (Long attendeeId : scheduleDTO.getAttendeeIds()) {
                TeamScheduleAttendee attendee = new TeamScheduleAttendee();
                attendee.setScheduleId(schedule.getId());
                attendee.setUserId(attendeeId);
                attendee.setStatus("PENDING");
                attendeeMapper.insert(attendee);
            }
        } else {
            // 如果没有指定，默认添加所有团队成员
            for (TeamMember m : allMembers) {
                TeamScheduleAttendee attendee = new TeamScheduleAttendee();
                attendee.setScheduleId(schedule.getId());
                attendee.setUserId(m.getUserId());
                attendee.setStatus("PENDING");
                attendeeMapper.insert(attendee);
            }
        }

        // 自动为所有团队成员创建个人行程
        int syncedCount = 0;
        for (Long memberUserId : memberUserIds) {
            try {
                PersonalSchedule personalSchedule = new PersonalSchedule();
                personalSchedule.setTitle(schedule.getTitle());
                personalSchedule.setDescription(schedule.getDescription());
                personalSchedule.setStartTime(schedule.getStartTime());
                personalSchedule.setEndTime(schedule.getEndTime());
                personalSchedule.setType(ScheduleTypeEnum.TEAM); // 标记为团队事务
                personalSchedule.setStatus(schedule.getStatus());
                personalSchedule.setLocation(schedule.getLocation());
                personalSchedule.setIsAllDay(false);
                personalSchedule.setCreatorId(memberUserId); // 每个成员的个人行程
                personalSchedule.setSyncedTeamScheduleId(schedule.getId()); // 关联团队行程ID
                personalScheduleMapper.insert(personalSchedule);
                syncedCount++;
                log.debug("已为用户 {} 同步团队行程 {} 到个人行程表", memberUserId, schedule.getId());
            } catch (Exception e) {
                log.error("为用户 {} 同步团队行程 {} 失败: {}", memberUserId, schedule.getId(), e.getMessage(), e);
                // 继续处理其他成员，不中断整个流程
            }
        }
        log.info("团队行程 {} 已创建，并同步到 {} 个成员的个人行程表", schedule.getId(), syncedCount);

        return convertToTeamScheduleVO(schedule, team);
    }

    @Override
    public Map<String, Object> getTeamSchedules(Long userId, Long teamId, ScheduleQueryDTO queryDTO) {
        // 检查用户是否是团队成员
        TeamMember member = teamMemberMapper.selectOne(
                new LambdaQueryWrapper<TeamMember>()
                        .eq(TeamMember::getTeamId, teamId)
                        .eq(TeamMember::getUserId, userId)
        );
        if (member == null) {
            throw new BusinessException(ErrorCode.TEAM_MEMBER_NOT_EXIST);
        }

        LambdaQueryWrapper<TeamSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeamSchedule::getTeamId, teamId)
                .eq(TeamSchedule::getIsDeleted, 0);

        if (queryDTO.getStartDate() != null) {
            queryWrapper.ge(TeamSchedule::getStartTime, queryDTO.getStartDate().atStartOfDay());
        }
        if (queryDTO.getEndDate() != null) {
            queryWrapper.le(TeamSchedule::getEndTime, queryDTO.getEndDate().atTime(23, 59, 59));
        }

        queryWrapper.orderByDesc(TeamSchedule::getStartTime);

        Page<TeamSchedule> pageObj = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        Page<TeamSchedule> result = teamScheduleMapper.selectPage(pageObj, queryWrapper);

        Team team = teamMapper.selectById(teamId);
        List<TeamScheduleVO> voList = result.getRecords().stream()
                .map(schedule -> convertToTeamScheduleVO(schedule, team))
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", voList);
        response.put("total", result.getTotal());
        response.put("teamId", teamId);

        return response;
    }

    // ==================== 行程同步 ====================

    @Override
    @Transactional
    public TeamScheduleVO syncPersonalToTeam(Long userId, Long personalScheduleId, Long teamId) {
        PersonalSchedule personalSchedule = personalScheduleMapper.selectById(personalScheduleId);
        if (personalSchedule == null || personalSchedule.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_EXIST);
        }

        if (!personalSchedule.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Team team = teamMapper.selectById(teamId);
        if (team == null || team.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.TEAM_NOT_EXIST);
        }

        // 创建团队行程
        TeamSchedule teamSchedule = new TeamSchedule();
        teamSchedule.setTeamId(teamId);
        teamSchedule.setTitle(personalSchedule.getTitle());
        teamSchedule.setDescription(personalSchedule.getDescription());
        teamSchedule.setStartTime(personalSchedule.getStartTime());
        teamSchedule.setEndTime(personalSchedule.getEndTime());
        teamSchedule.setLocation(personalSchedule.getLocation());
        teamSchedule.setStatus(personalSchedule.getStatus());
        teamSchedule.setCreatorId(userId);

        teamScheduleMapper.insert(teamSchedule);

        return convertToTeamScheduleVO(teamSchedule, team);
    }

    @Override
    @Transactional
    public PersonalScheduleVO syncTeamToPersonal(Long userId, Long teamScheduleId) {
        TeamSchedule teamSchedule = teamScheduleMapper.selectById(teamScheduleId);
        if (teamSchedule == null || teamSchedule.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_EXIST);
        }

        // 检查用户是否是团队成员
        TeamMember member = teamMemberMapper.selectOne(
                new LambdaQueryWrapper<TeamMember>()
                        .eq(TeamMember::getTeamId, teamSchedule.getTeamId())
                        .eq(TeamMember::getUserId, userId)
        );
        if (member == null) {
            throw new BusinessException(ErrorCode.TEAM_MEMBER_NOT_EXIST);
        }

        // 创建个人行程
        PersonalSchedule personalSchedule = new PersonalSchedule();
        personalSchedule.setTitle(teamSchedule.getTitle());
        personalSchedule.setDescription(teamSchedule.getDescription());
        personalSchedule.setStartTime(teamSchedule.getStartTime());
        personalSchedule.setEndTime(teamSchedule.getEndTime());
        personalSchedule.setLocation(teamSchedule.getLocation());
        personalSchedule.setType(ScheduleTypeEnum.MEETING);
        personalSchedule.setStatus(teamSchedule.getStatus());
        personalSchedule.setCreatorId(userId);
        personalSchedule.setSyncedTeamScheduleId(teamScheduleId);

        personalScheduleMapper.insert(personalSchedule);

        User creator = userMapper.selectById(userId);
        return convertToPersonalVO(personalSchedule, creator);
    }

    // ==================== 提醒与通知 ====================

    @Override
    public Map<String, Object> getTodayReminders(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();

        LambdaQueryWrapper<PersonalSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PersonalSchedule::getCreatorId, userId)
                .eq(PersonalSchedule::getIsDeleted, 0)
                .eq(PersonalSchedule::getStatus, ScheduleStatusEnum.PENDING)
                .ge(PersonalSchedule::getStartTime, todayStart)
                .lt(PersonalSchedule::getStartTime, todayEnd)
                .orderByAsc(PersonalSchedule::getStartTime);

        List<PersonalSchedule> schedules = personalScheduleMapper.selectList(queryWrapper);
        List<PersonalScheduleVO> reminders = schedules.stream()
                .map(schedule -> {
                    User creator = userMapper.selectById(schedule.getCreatorId());
                    return convertToPersonalVO(schedule, creator);
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("reminders", reminders);
        response.put("count", reminders.size());
        response.put("hasReminders", !reminders.isEmpty());

        return response;
    }

    @Override
    @Transactional
    public void markAsReminded(Long userId, Long scheduleId) {
        PersonalSchedule schedule = personalScheduleMapper.selectById(scheduleId);
        if (schedule == null || schedule.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_EXIST);
        }

        if (!schedule.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 这里可以添加已提醒标记，暂时简化处理
    }

    // ==================== 日历视图 ====================

    @Override
    public Map<String, Object> getCalendarView(Long userId, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        LambdaQueryWrapper<PersonalSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PersonalSchedule::getCreatorId, userId)
                .eq(PersonalSchedule::getIsDeleted, 0)
                .ge(PersonalSchedule::getStartTime, start)
                .le(PersonalSchedule::getStartTime, end);

        List<PersonalSchedule> schedules = personalScheduleMapper.selectList(queryWrapper);

        // 按日期分组
        Map<Integer, List<PersonalSchedule>> schedulesByDay = schedules.stream()
                .collect(Collectors.groupingBy(s -> s.getStartTime().getDayOfMonth()));

        List<Map<String, Object>> calendarData = new ArrayList<>();
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("day", day);
            List<PersonalSchedule> daySchedules = schedulesByDay.getOrDefault(day, new ArrayList<>());
            dayData.put("personalScheduleCount", daySchedules.size());
            dayData.put("teamScheduleCount", 0); // 简化处理
            dayData.put("hasSchedules", !daySchedules.isEmpty());
            calendarData.add(dayData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("year", year);
        response.put("month", month);
        response.put("days", calendarData);
        response.put("totalSchedules", schedules.size());

        return response;
    }

    // ==================== 辅助方法 ====================

    private PersonalScheduleVO convertToPersonalVO(PersonalSchedule schedule, User creator) {
        PersonalScheduleVO vo = new PersonalScheduleVO();
        vo.setId(schedule.getId());
        vo.setTitle(schedule.getTitle());
        vo.setDescription(schedule.getDescription());
        vo.setStartTime(schedule.getStartTime());
        vo.setEndTime(schedule.getEndTime());
        vo.setType(schedule.getType());
        vo.setStatus(schedule.getStatus());
        vo.setLocation(schedule.getLocation());
        vo.setIsAllDay(schedule.getIsAllDay());
        vo.setRemindType(schedule.getRemindType());
        vo.setCustomRemindMinutes(schedule.getCustomRemindMinutes());
        vo.setIsRepeat(schedule.getIsRepeat());
        vo.setRepeatRule(schedule.getRepeatRule());
        vo.setTag(schedule.getTag());
        vo.setCreatorId(schedule.getCreatorId());
        vo.setCreatorName(creator != null ? 
                (creator.getNickname() != null ? creator.getNickname() : creator.getUsername()) : null);
        vo.setSyncedTeamScheduleId(schedule.getSyncedTeamScheduleId());
        vo.setCreateTime(schedule.getCreateTime());
        vo.setUpdateTime(schedule.getUpdateTime());
        return vo;
    }

    private TeamVO convertToTeamVO(Team team, User creator, Long currentUserId) {
        TeamMember member = teamMemberMapper.selectOne(
                new LambdaQueryWrapper<TeamMember>()
                        .eq(TeamMember::getTeamId, team.getId())
                        .eq(TeamMember::getUserId, currentUserId)
        );
        TeamRoleEnum userRole = member != null ? member.getRole() : null;
        return convertToTeamVO(team, creator, currentUserId, userRole);
    }

    private TeamVO convertToTeamVO(Team team, User creator, Long currentUserId, TeamRoleEnum userRole) {
        TeamVO vo = new TeamVO();
        vo.setId(team.getId());
        vo.setName(team.getName());
        vo.setDescription(team.getDescription());
        vo.setAvatar(team.getAvatar());
        vo.setCreatorId(team.getCreatorId());
        vo.setCreatorName(creator != null ? 
                (creator.getNickname() != null ? creator.getNickname() : creator.getUsername()) : null);
        vo.setSchoolId(team.getSchoolId());
        University university = team.getSchoolId() != null ? 
                universityMapper.selectById(team.getSchoolId()) : null;
        vo.setSchoolName(university != null ? university.getName() : null);
        vo.setNeedApprove(team.getNeedApprove());
        vo.setMaxMembers(team.getMaxMembers());
        vo.setMemberCount(team.getMemberCount());
        vo.setUserRole(userRole);
        vo.setInviteCode(team.getInviteCode());
        vo.setCreateTime(team.getCreateTime());
        return vo;
    }

    private TeamScheduleVO convertToTeamScheduleVO(TeamSchedule schedule, Team team) {
        TeamScheduleVO vo = new TeamScheduleVO();
        vo.setId(schedule.getId());
        vo.setTeamId(schedule.getTeamId());
        vo.setTeamName(team != null ? team.getName() : null);
        vo.setTitle(schedule.getTitle());
        vo.setDescription(schedule.getDescription());
        vo.setStartTime(schedule.getStartTime());
        vo.setEndTime(schedule.getEndTime());
        vo.setLocation(schedule.getLocation());
        vo.setStatus(schedule.getStatus());
        vo.setNeedConfirm(schedule.getNeedConfirm());
        vo.setCreatorId(schedule.getCreatorId());
        User creator = userMapper.selectById(schedule.getCreatorId());
        vo.setCreatorName(creator != null ? 
                (creator.getNickname() != null ? creator.getNickname() : creator.getUsername()) : null);
        vo.setCreateTime(schedule.getCreateTime());

        // 查询参会人员
        LambdaQueryWrapper<TeamScheduleAttendee> attendeeQuery = new LambdaQueryWrapper<>();
        attendeeQuery.eq(TeamScheduleAttendee::getScheduleId, schedule.getId());
        List<TeamScheduleAttendee> attendees = attendeeMapper.selectList(attendeeQuery);
        List<TeamScheduleAttendeeVO> attendeeVOList = attendees.stream()
                .map(a -> {
                    User user = userMapper.selectById(a.getUserId());
                    TeamScheduleAttendeeVO attendeeVO = new TeamScheduleAttendeeVO();
                    attendeeVO.setUserId(a.getUserId());
                    attendeeVO.setUserName(user != null ? 
                            (user.getNickname() != null ? user.getNickname() : user.getUsername()) : null);
                    attendeeVO.setAvatar(user != null ? user.getAvatarUrl() : null);
                    attendeeVO.setConfirmStatus(a.getStatus());
                    attendeeVO.setIsCreator(a.getUserId().equals(schedule.getCreatorId()));
                    attendeeVO.setConfirmTime(a.getConfirmTime());
                    return attendeeVO;
                })
                .collect(Collectors.toList());
        vo.setAttendees(attendeeVOList);

        return vo;
    }

    // ==================== 课程表导入导出 ====================

    @Override
    @Transactional
    public Map<String, Object> importScheduleFromExcel(Long userId, org.springframework.web.multipart.MultipartFile file, String semester, Boolean overwrite) {
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }

        // 如果覆盖模式，先删除该学期的旧数据
        if (Boolean.TRUE.equals(overwrite) && StringUtils.hasText(semester)) {
            // 这里可以根据semester删除旧数据，简化处理暂时不删除
            log.info("覆盖模式：将保留旧数据，新数据会追加");
        }

        // 使用EasyExcel读取Excel文件
        List<ScheduleExcelDTO> excelDataList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        
        try {
            EasyExcel.read(file.getInputStream(), ScheduleExcelDTO.class, new ScheduleExcelListener(excelDataList, errorMessages))
                    .sheet()
                    .headRowNumber(1) // 跳过表头
                    .doRead();
        } catch (IOException e) {
            log.error("读取Excel文件失败", e);
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Excel文件读取失败：" + e.getMessage());
        }

        // 转换并保存数据
        int importedCount = 0;
        int failedCount = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (ScheduleExcelDTO excelData : excelDataList) {
            try {
                // 跳过空行
                if (excelData.getTitle() == null || excelData.getTitle().trim().isEmpty()) {
                    continue;
                }

                PersonalSchedule schedule = new PersonalSchedule();
                schedule.setTitle(excelData.getTitle().trim());
                schedule.setDescription(excelData.getDescription() != null ? excelData.getDescription().trim() : "");
                
                // 解析时间
                try {
                    if (excelData.getStartTime() != null && !excelData.getStartTime().trim().isEmpty()) {
                        LocalDateTime startTime = parseDateTime(excelData.getStartTime().trim(), formatter, dateTimeFormatter);
                        schedule.setStartTime(startTime);
                    } else {
                        throw new IllegalArgumentException("开始时间不能为空");
                    }
                    
                    if (excelData.getEndTime() != null && !excelData.getEndTime().trim().isEmpty()) {
                        LocalDateTime endTime = parseDateTime(excelData.getEndTime().trim(), formatter, dateTimeFormatter);
                        schedule.setEndTime(endTime);
                    } else {
                        // 如果没有结束时间，默认设置为开始时间后1小时
                        schedule.setEndTime(schedule.getStartTime().plusHours(1));
                    }
                } catch (Exception e) {
                    errorMessages.add("课程《" + excelData.getTitle() + "》时间解析失败：" + e.getMessage());
                    failedCount++;
                    continue;
                }

                // 解析类型
                schedule.setType(parseScheduleType(excelData.getType()));
                schedule.setStatus(ScheduleStatusEnum.PENDING);
                schedule.setLocation(excelData.getLocation() != null ? excelData.getLocation().trim() : "");
                
                // 解析是否全天
                schedule.setIsAllDay(parseBoolean(excelData.getIsAllDay(), false));
                
                // 解析提醒类型
                schedule.setRemindType(parseRemindType(excelData.getRemindType()));
                schedule.setCustomRemindMinutes(excelData.getCustomRemindMinutes());
                
                // 解析是否重复
                schedule.setIsRepeat(parseBoolean(excelData.getIsRepeat(), false));
                schedule.setRepeatRule(excelData.getRepeatRule() != null ? excelData.getRepeatRule().trim() : null);
                schedule.setTag(excelData.getTag() != null ? excelData.getTag().trim() : null);
                
                schedule.setCreatorId(userId);

                personalScheduleMapper.insert(schedule);
                importedCount++;
            } catch (Exception e) {
                log.error("导入课程失败：{}", excelData.getTitle(), e);
                errorMessages.add("课程《" + excelData.getTitle() + "》导入失败：" + e.getMessage());
                failedCount++;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("importedCount", importedCount);
        response.put("failedCount", failedCount);
        response.put("semester", semester);
        response.put("errorMessages", errorMessages);
        response.put("message", String.format("课程表导入完成，成功导入%d条，失败%d条", importedCount, failedCount));
        response.put("importTime", LocalDateTime.now());
        
        return response;
    }

    @Override
    public Resource exportScheduleToExcel(Long userId, String semester) {
        // 查询用户的行程数据
        LambdaQueryWrapper<PersonalSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PersonalSchedule::getCreatorId, userId)
                .eq(PersonalSchedule::getIsDeleted, 0)
                .orderByAsc(PersonalSchedule::getStartTime);

        // 如果指定了学期，可以根据学期筛选（这里简化处理，暂时不筛选）
        List<PersonalSchedule> schedules = personalScheduleMapper.selectList(queryWrapper);

        // 转换为Excel DTO
        List<ScheduleExcelDTO> excelDataList = schedules.stream()
                .map(schedule -> convertToExcelDTO(schedule, semester))
                .collect(Collectors.toList());

        // 使用EasyExcel生成Excel文件
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            EasyExcel.write(outputStream, ScheduleExcelDTO.class)
                    .sheet("课程表")
                    .doWrite(excelDataList);

            byte[] bytes = outputStream.toByteArray();
            // ByteArrayOutputStream.toByteArray() 不会返回null
            @SuppressWarnings("null")
            ByteArrayResource resource = new ByteArrayResource(bytes);
            return resource;
        } catch (IOException e) {
            log.error("生成Excel文件失败", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Excel文件生成失败：" + e.getMessage());
        }
    }

    @Override
    public Resource getScheduleTemplate() {
        // 创建空的Excel模板（只包含表头）
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            EasyExcel.write(outputStream, ScheduleExcelDTO.class)
                    .sheet("课程表模板")
                    .doWrite(new ArrayList<>()); // 空数据，只生成表头

            byte[] bytes = outputStream.toByteArray();
            // ByteArrayOutputStream.toByteArray() 不会返回null
            @SuppressWarnings("null")
            ByteArrayResource resource = new ByteArrayResource(bytes);
            return resource;
        } catch (IOException e) {
            log.error("生成Excel模板失败", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Excel模板生成失败：" + e.getMessage());
        }
    }

    // ==================== Excel处理辅助方法 ====================

    /**
     * Excel读取监听器
     */
    private static class ScheduleExcelListener extends AnalysisEventListener<ScheduleExcelDTO> {
        private final List<ScheduleExcelDTO> cachedDataList;
        @SuppressWarnings("unused")
        private final List<String> errorMessages; // 保留用于未来扩展，错误处理在外部进行

        public ScheduleExcelListener(List<ScheduleExcelDTO> cachedDataList, List<String> errorMessages) {
            this.cachedDataList = cachedDataList;
            this.errorMessages = errorMessages;
        }

        @Override
        public void invoke(ScheduleExcelDTO data, AnalysisContext context) {
            cachedDataList.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // 读取完成后的处理
        }
    }

    /**
     * 解析日期时间字符串
     */
    private LocalDateTime parseDateTime(String dateTimeStr, DateTimeFormatter... formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(dateTimeStr, formatter);
            } catch (DateTimeParseException e) {
                // 尝试下一个格式
            }
        }
        // 如果都不匹配，尝试只解析日期（默认时间为00:00:00）
        try {
            LocalDate date = LocalDate.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return date.atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("时间格式不正确：" + dateTimeStr);
        }
    }

    /**
     * 解析行程类型
     */
    private ScheduleTypeEnum parseScheduleType(String typeStr) {
        if (typeStr == null || typeStr.trim().isEmpty()) {
            return ScheduleTypeEnum.OTHER;
        }
        
        String type = typeStr.trim();
        for (ScheduleTypeEnum typeEnum : ScheduleTypeEnum.values()) {
            if (typeEnum.getDescription().equals(type) || typeEnum.name().equals(type)) {
                return typeEnum;
            }
        }
        return ScheduleTypeEnum.OTHER;
    }

    /**
     * 解析提醒类型
     */
    private RemindTypeEnum parseRemindType(String remindTypeStr) {
        if (remindTypeStr == null || remindTypeStr.trim().isEmpty()) {
            return RemindTypeEnum.NONE;
        }
        
        String remindType = remindTypeStr.trim();
        for (RemindTypeEnum typeEnum : RemindTypeEnum.values()) {
            if (typeEnum.getDescription().equals(remindType) || typeEnum.name().equals(remindType)) {
                return typeEnum;
            }
        }
        return RemindTypeEnum.NONE;
    }

    /**
     * 解析布尔值
     */
    private Boolean parseBoolean(String boolStr, Boolean defaultValue) {
        if (boolStr == null || boolStr.trim().isEmpty()) {
            return defaultValue;
        }
        
        String bool = boolStr.trim().toLowerCase();
        return "是".equals(bool) || "true".equals(bool) || "1".equals(bool) || "yes".equals(bool);
    }

    /**
     * 将PersonalSchedule转换为Excel DTO
     */
    private ScheduleExcelDTO convertToExcelDTO(PersonalSchedule schedule, String semester) {
        ScheduleExcelDTO dto = new ScheduleExcelDTO();
        dto.setTitle(schedule.getTitle());
        dto.setDescription(schedule.getDescription());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dto.setStartTime(schedule.getStartTime() != null ? schedule.getStartTime().format(formatter) : "");
        dto.setEndTime(schedule.getEndTime() != null ? schedule.getEndTime().format(formatter) : "");
        
        dto.setType(schedule.getType() != null ? schedule.getType().getDescription() : "");
        dto.setLocation(schedule.getLocation());
        dto.setIsAllDay(schedule.getIsAllDay() != null && schedule.getIsAllDay() ? "是" : "否");
        dto.setRemindType(schedule.getRemindType() != null ? schedule.getRemindType().getDescription() : "");
        dto.setCustomRemindMinutes(schedule.getCustomRemindMinutes());
        dto.setIsRepeat(schedule.getIsRepeat() != null && schedule.getIsRepeat() ? "是" : "否");
        dto.setRepeatRule(schedule.getRepeatRule());
        dto.setTag(schedule.getTag());
        dto.setSemester(semester);
        
        return dto;
    }

    @Override
    public Map<String, Object> joinTeamByCode(Long userId, String inviteCode) {
        // TODO: 实现通过邀请码加入团队的逻辑
        // 1. 根据邀请码查找团队
        // 2. 验证邀请码是否有效
        // 3. 将用户添加到团队
        throw new BusinessException(ErrorCode.BAD_REQUEST, "功能待实现");
    }

    @Override
    public Map<String, Object> getTeamInvitations(Long userId) {
        // TODO: 实现获取团队邀请列表的逻辑
        // 查询用户收到的所有团队邀请
        Map<String, Object> response = new HashMap<>();
        response.put("invitations", new ArrayList<>());
        return response;
    }

    @Override
    public void updateReminderStatus(Long userId, Long scheduleId, Boolean enabled) {
        // 验证行程是否属于当前用户
        PersonalSchedule schedule = personalScheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_EXIST);
        }
        if (!schedule.getCreatorId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        
        // 更新提醒状态（通过设置remindType来实现）
        if (!enabled) {
            schedule.setRemindType(null); // 禁用提醒
        } else if (schedule.getRemindType() == null) {
            schedule.setRemindType(RemindTypeEnum.MINUTES_5); // 默认5分钟提醒
        }
        personalScheduleMapper.updateById(schedule);
    }

    @Override
    public void deleteReminder(Long userId, Long scheduleId) {
        // 删除提醒实际上就是删除行程
        deletePersonalSchedule(userId, scheduleId);
    }

    @Override
    public Map<String, Object> importScheduleManual(Long userId, Map<String, Object> manualData) {
        // TODO: 实现手动录入导入课程表的逻辑
        // 1. 解析manualData中的课程信息
        // 2. 创建个人行程
        // 3. 返回导入结果
        Map<String, Object> response = new HashMap<>();
        response.put("importedCount", 0);
        response.put("scheduleIds", new ArrayList<>());
        return response;
    }

    @Override
    public Map<String, Object> importScheduleFromLink(Long userId, String url, String username, String password) {
        // TODO: 实现链接导入课程表的逻辑
        // 1. 访问教务系统链接
        // 2. 如果需要登录，使用username和password
        // 3. 解析课程表数据
        // 4. 创建个人行程
        // 5. 返回导入结果
        Map<String, Object> response = new HashMap<>();
        response.put("importedCount", 0);
        response.put("scheduleIds", new ArrayList<>());
        return response;
    }

    @Override
    @Transactional
    public String regenerateTeamInviteCode(Long userId, Long teamId) {
        Team team = teamMapper.selectById(teamId);
        if (team == null || team.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.TEAM_NOT_EXIST);
        }

        // 检查权限（只有创建者或管理员可以重新生成邀请码）
        TeamMember member = teamMemberMapper.selectOne(
                new LambdaQueryWrapper<TeamMember>()
                        .eq(TeamMember::getTeamId, teamId)
                        .eq(TeamMember::getUserId, userId)
        );
        if (member == null || 
            (member.getRole() != TeamRoleEnum.CREATOR && member.getRole() != TeamRoleEnum.ADMIN)) {
            throw new BusinessException(ErrorCode.TEAM_ROLE_DENIED);
        }

        // 生成新的邀请码
        String newInviteCode = generateInviteCode();
        team.setInviteCode(newInviteCode);
        teamMapper.updateById(team);

        return newInviteCode;
    }

    /**
     * 生成团队邀请码（8位随机字符串，包含数字和大写字母）
     */
    private String generateInviteCode() {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder code = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
}
