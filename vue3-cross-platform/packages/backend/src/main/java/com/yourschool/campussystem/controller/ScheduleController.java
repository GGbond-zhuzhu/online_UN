package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.dto.*;
import com.yourschool.campussystem.enums.ScheduleStatusEnum;
import com.yourschool.campussystem.service.ScheduleService;
import com.yourschool.campussystem.util.UserContextUtils;
import com.yourschool.campussystem.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedule")
@Tag(name = "行程管理", description = "个人/团队行程管理、团队协作、行程同步等功能")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // ==================== 个人行程管理 ====================

    @Operation(summary = "创建个人行程", description = "创建个人行程，支持设置提醒、重复等")
    @PostMapping("/personal/create")
    public ApiResponse<PersonalScheduleVO> createPersonalSchedule(
            HttpServletRequest request,
            @Parameter(description = "个人行程信息", required = true)
            @Valid @RequestBody PersonalScheduleDTO scheduleDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        PersonalScheduleVO scheduleVO = scheduleService.createPersonalSchedule(userId, scheduleDTO);
        return ApiResponse.success("个人行程创建成功", scheduleVO);
    }

    @Operation(summary = "获取个人行程列表", description = "分页获取个人行程，支持日期范围、类型、状态筛选")
    @GetMapping("/personal/list")
    public ApiResponse<Map<String, Object>> getPersonalSchedules(
            HttpServletRequest request,
            @Parameter(description = "查询参数")
            @Valid ScheduleQueryDTO queryDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = scheduleService.getPersonalSchedules(userId, queryDTO);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "获取个人行程详情", description = "获取个人行程详细信息")
    @GetMapping("/personal/detail/{id}")
    public ApiResponse<PersonalScheduleVO> getPersonalScheduleDetail(
            HttpServletRequest request,
            @Parameter(description = "行程ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        PersonalScheduleVO scheduleVO = scheduleService.getPersonalScheduleDetail(userId, id);
        return ApiResponse.success("查询成功", scheduleVO);
    }

    @Operation(summary = "修改个人行程", description = "修改个人行程信息")
    @PutMapping("/personal/update/{id}")
    public ApiResponse<PersonalScheduleVO> updatePersonalSchedule(
            HttpServletRequest request,
            @Parameter(description = "行程ID", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(description = "修改的行程信息", required = true)
            @Valid @RequestBody PersonalScheduleDTO updateDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        PersonalScheduleVO scheduleVO = scheduleService.updatePersonalSchedule(userId, id, updateDTO);
        return ApiResponse.success("个人行程修改成功", scheduleVO);
    }

    @Operation(summary = "删除个人行程", description = "删除个人行程")
    @DeleteMapping("/personal/delete/{id}")
    public ApiResponse<String> deletePersonalSchedule(
            HttpServletRequest request,
            @Parameter(description = "行程ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        scheduleService.deletePersonalSchedule(userId, id);
        return ApiResponse.success("个人行程删除成功");
    }

    @Operation(summary = "更新行程状态", description = "更新个人行程状态（如标记为已完成）")
    @PutMapping("/personal/update-status/{id}")
    public ApiResponse<PersonalScheduleVO> updatePersonalScheduleStatus(
            HttpServletRequest request,
            @Parameter(description = "行程ID", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(description = "新的状态", example = "COMPLETED", required = true)
            @RequestParam ScheduleStatusEnum status) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        PersonalScheduleVO scheduleVO = scheduleService.updatePersonalScheduleStatus(userId, id, status);
        return ApiResponse.success("行程状态已更新", scheduleVO);
    }

    // ==================== 团队管理 ====================

    @Operation(summary = "创建团队", description = "创建新的团队，只有同校用户可加入")
    @PostMapping("/team/create")
    public ApiResponse<TeamVO> createTeam(
            HttpServletRequest request,
            @Parameter(description = "团队信息", required = true)
            @Valid @RequestBody TeamCreateDTO teamDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        TeamVO teamVO = scheduleService.createTeam(userId, teamDTO);
        return ApiResponse.success("团队创建成功", teamVO);
    }

    @Operation(summary = "获取我的团队列表", description = "获取当前用户加入的团队列表")
    @GetMapping("/team/my-teams")
    public ApiResponse<Map<String, Object>> getMyTeams(
            HttpServletRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = scheduleService.getMyTeams(userId, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "获取团队详情", description = "获取团队详细信息，包括成员列表")
    @GetMapping("/team/detail/{id}")
    public ApiResponse<TeamVO> getTeamDetail(
            HttpServletRequest request,
            @Parameter(description = "团队ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        TeamVO teamVO = scheduleService.getTeamDetail(userId, id);
        return ApiResponse.success("查询成功", teamVO);
    }

    @Operation(summary = "邀请成员加入团队", description = "邀请用户加入团队，需同校")
    @PostMapping("/team/{teamId}/invite")
    public ApiResponse<String> inviteTeamMember(
            HttpServletRequest request,
            @Parameter(description = "团队ID", example = "1", required = true)
            @PathVariable Long teamId,
            @Parameter(description = "被邀请用户ID（兼容旧方式：单个userId）", example = "3")
            @RequestParam(required = false) Long userId,
            @Parameter(description = "被邀请用户ID列表（推荐：一次邀请多人）")
            @RequestBody(required = false) TeamInviteDTO body) {
        Long currentUserId = UserContextUtils.getUserIdRequired(request);

        List<Long> inviteIds = new ArrayList<>();
        if (body != null && body.getUserIds() != null) {
            inviteIds.addAll(body.getUserIds().stream().filter(Objects::nonNull).collect(Collectors.toList()));
        }
        if (userId != null) {
            inviteIds.add(userId);
        }

        if (inviteIds.isEmpty()) {
            return ApiResponse.error(400, "请提供被邀请用户ID");
        }

        // 逐个邀请（内部会做权限/同校等校验）
        for (Long inviteUserId : inviteIds) {
            scheduleService.inviteTeamMember(currentUserId, teamId, inviteUserId);
        }
        return ApiResponse.success("邀请已发送");
    }

    @Operation(summary = "处理团队邀请", description = "处理团队邀请（接受/拒绝）")
    @PostMapping("/team/invite/{inviteId}/process")
    public ApiResponse<String> processTeamInvite(
            HttpServletRequest request,
            @Parameter(description = "邀请ID", example = "1", required = true)
            @PathVariable Long inviteId,

            @Parameter(description = "处理结果（accept/reject）", example = "accept", required = true)
            @RequestParam String action) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        scheduleService.processTeamInvite(userId, inviteId, action);
        String result = "accept".equals(action) ? "接受" : "拒绝";
        return ApiResponse.success("邀请已" + result);
    }

    @Operation(summary = "通过邀请码加入团队", description = "通过团队邀请码加入团队")
    @PostMapping("/team/join-by-code")
    public ApiResponse<Map<String, Object>> joinTeamByCode(
            HttpServletRequest request,
            @Parameter(description = "团队邀请码", required = true)
            @RequestParam String inviteCode) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = scheduleService.joinTeamByCode(userId, inviteCode);
        return ApiResponse.success("加入团队成功", response);
    }

    @Operation(summary = "获取团队邀请列表", description = "获取当前用户收到的团队邀请列表")
    @GetMapping("/team/invitations")
    public ApiResponse<Map<String, Object>> getTeamInvitations(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = scheduleService.getTeamInvitations(userId);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "处理团队邀请（新接口）", description = "处理团队邀请（接受/拒绝），使用邀请ID")
    @PostMapping("/team/invitation/{id}/process")
    public ApiResponse<String> processTeamInvitation(
            HttpServletRequest request,
            @Parameter(description = "邀请ID", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "处理动作（accept/reject）", example = "accept", required = true)
            @RequestParam String action) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        scheduleService.processTeamInvite(userId, id, action);
        String resultMsg = "accept".equals(action) ? "已接受" : "已拒绝";
        return ApiResponse.success("团队邀请" + resultMsg);
    }

    @Operation(summary = "移除团队成员", description = "从团队中移除成员（仅创建者/管理员可操作）")
    @DeleteMapping("/team/{teamId}/member/{userId}")
    public ApiResponse<String> removeTeamMember(
            HttpServletRequest request,
            @Parameter(description = "团队ID", example = "1", required = true)
            @PathVariable Long teamId,

            @Parameter(description = "成员ID", example = "3", required = true)
            @PathVariable Long userId) {
        Long currentUserId = UserContextUtils.getUserIdRequired(request);
        scheduleService.removeTeamMember(currentUserId, teamId, userId);
        return ApiResponse.success("成员已从团队中移除");
    }

    @Operation(summary = "重新生成团队邀请码", description = "重新生成团队邀请码（仅创建者/管理员可操作）")
    @PostMapping("/team/{teamId}/regenerate-invite-code")
    public ApiResponse<Map<String, String>> regenerateTeamInviteCode(
            HttpServletRequest request,
            @Parameter(description = "团队ID", example = "1", required = true)
            @PathVariable Long teamId) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        String newInviteCode = scheduleService.regenerateTeamInviteCode(userId, teamId);
        Map<String, String> response = new HashMap<>();
        response.put("inviteCode", newInviteCode);
        return ApiResponse.success("邀请码已重新生成", response);
    }

    @Operation(summary = "设置团队管理员", description = "创建者设置团队管理员（最多4人，默认空）")
    @PutMapping("/team/{teamId}/admins")
    public ApiResponse<TeamVO> setTeamAdmins(
            HttpServletRequest request,
            @Parameter(description = "团队ID", example = "1", required = true)
            @PathVariable Long teamId,
            @Valid @RequestBody TeamAdminSetDTO dto) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        TeamVO teamVO = scheduleService.setTeamAdmins(userId, teamId, dto);
        return ApiResponse.success("管理员设置成功", teamVO);
    }

    // ==================== 团队行程管理 ====================

    @Operation(summary = "创建团队行程", description = "创建团队行程，可指定参会人员")
    @PostMapping("/team/create-schedule")
    public ApiResponse<TeamScheduleVO> createTeamSchedule(
            HttpServletRequest request,
            @Parameter(description = "团队行程信息", required = true)
            @Valid @RequestBody TeamScheduleDTO scheduleDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        TeamScheduleVO scheduleVO = scheduleService.createTeamSchedule(userId, scheduleDTO);
        return ApiResponse.success("团队行程创建成功", scheduleVO);
    }

    @Operation(summary = "获取团队行程列表", description = "获取指定团队的行程列表")
    @GetMapping("/team/{teamId}/schedules")
    public ApiResponse<Map<String, Object>> getTeamSchedules(
            HttpServletRequest request,
            @Parameter(description = "团队ID", example = "1", required = true)
            @PathVariable Long teamId,

            @Parameter(description = "查询参数")
            @Valid ScheduleQueryDTO queryDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = scheduleService.getTeamSchedules(userId, teamId, queryDTO);
        return ApiResponse.success("查询成功", response);
    }

    // ==================== 行程同步 ====================

    @Operation(summary = "同步个人行程到团队", description = "将个人行程同步到指定团队")
    @PostMapping("/sync/personal-to-team")
    public ApiResponse<TeamScheduleVO> syncPersonalToTeam(
            HttpServletRequest request,
            @Parameter(description = "个人行程ID", example = "1", required = true)
            @RequestParam Long personalScheduleId,

            @Parameter(description = "团队ID", example = "1", required = true)
            @RequestParam Long teamId) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        TeamScheduleVO scheduleVO = scheduleService.syncPersonalToTeam(userId, personalScheduleId, teamId);
        return ApiResponse.success("个人行程已同步到团队", scheduleVO);
    }

    @Operation(summary = "同步团队行程到个人", description = "将团队行程同步到个人日历")
    @PostMapping("/sync/team-to-personal")
    public ApiResponse<PersonalScheduleVO> syncTeamToPersonal(
            HttpServletRequest request,
            @Parameter(description = "团队行程ID", example = "1", required = true)
            @RequestParam Long teamScheduleId) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        PersonalScheduleVO scheduleVO = scheduleService.syncTeamToPersonal(userId, teamScheduleId);
        return ApiResponse.success("团队行程已同步到个人", scheduleVO);
    }

    @Operation(summary = "同步团队行程到个人（路径参数版本）", description = "将团队行程同步到个人日历，使用路径参数")
    @PostMapping("/team/sync/{scheduleId}")
    public ApiResponse<PersonalScheduleVO> syncTeamScheduleToPersonal(
            HttpServletRequest request,
            @Parameter(description = "团队行程ID", example = "1", required = true)
            @PathVariable Long scheduleId) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        PersonalScheduleVO scheduleVO = scheduleService.syncTeamToPersonal(userId, scheduleId);
        return ApiResponse.success("团队行程已同步到个人", scheduleVO);
    }

    @Operation(summary = "同步团队行程到指定成员（批量）", description = "创建者/管理员选择成员，将团队行程写入成员个人行程表")
    @PostMapping("/team/{teamId}/sync/team-schedule/{scheduleId}")
    public ApiResponse<Map<String, Object>> syncTeamScheduleToMembers(
            HttpServletRequest request,
            @PathVariable Long teamId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody TeamSyncMembersDTO dto) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> res = scheduleService.syncTeamScheduleToMembers(userId, teamId, scheduleId, dto);
        return ApiResponse.success("同步完成", res);
    }

    @Operation(summary = "同步课程表到指定成员（批量）", description = "创建者/管理员选择成员，将某成员课程表复制置入到目标成员")
    @PostMapping("/team/{teamId}/sync/courses")
    public ApiResponse<Map<String, Object>> syncCoursesToMembers(
            HttpServletRequest request,
            @PathVariable Long teamId,
            @Valid @RequestBody TeamCourseSyncDTO dto) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> res = scheduleService.syncCoursesToMembers(userId, teamId, dto);
        return ApiResponse.success("同步完成", res);
    }

    // ==================== 提醒与通知 ====================

    @Operation(summary = "获取今日提醒", description = "获取今日需要提醒的行程")
    @GetMapping("/reminders/today")
    public ApiResponse<Map<String, Object>> getTodayReminders(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = scheduleService.getTodayReminders(userId);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "标记为已提醒", description = "将行程标记为已提醒")
    @PutMapping("/reminders/mark-reminded/{id}")
    public ApiResponse<String> markAsReminded(
            HttpServletRequest request,
            @Parameter(description = "行程ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        scheduleService.markAsReminded(userId, id);
        return ApiResponse.success("行程已标记为已提醒");
    }

    @Operation(summary = "更新提醒状态", description = "更新行程提醒的启用/禁用状态")
    @PutMapping("/reminder/{id}/status")
    public ApiResponse<String> updateReminderStatus(
            HttpServletRequest request,
            @Parameter(description = "行程ID", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "是否启用提醒", example = "true", required = true)
            @RequestParam Boolean enabled) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        scheduleService.updateReminderStatus(userId, id, enabled);
        return ApiResponse.success(enabled ? "提醒已启用" : "提醒已禁用");
    }

    @Operation(summary = "删除提醒", description = "删除行程提醒")
    @DeleteMapping("/reminder/{id}")
    public ApiResponse<String> deleteReminder(
            HttpServletRequest request,
            @Parameter(description = "提醒ID（行程ID）", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        scheduleService.deleteReminder(userId, id);
        return ApiResponse.success("提醒已删除");
    }

    // ==================== 日历视图 ====================

    @Operation(summary = "获取日历视图数据", description = "获取指定月份的行程日历视图数据")
    @GetMapping("/calendar/{year}/{month}")
    public ApiResponse<Map<String, Object>> getCalendarView(
            HttpServletRequest request,
            @Parameter(description = "年份", example = "2024", required = true)
            @PathVariable Integer year,

            @Parameter(description = "月份", example = "3", required = true)
            @PathVariable Integer month) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = scheduleService.getCalendarView(userId, year, month);
        return ApiResponse.success("查询成功", response);
    }

    // ==================== 课程表导入 ====================

    @Operation(summary = "导入课程表（Excel）", description = "从Excel文件导入课程表，自动创建个人行程")
    @PostMapping("/import/schedule-excel")
    public ApiResponse<Map<String, Object>> importScheduleFromExcel(
            HttpServletRequest request,
            @Parameter(description = "Excel文件", required = true)
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "学期（如：2024-2025-1）", required = true)
            @RequestParam String semester,
            
            @Parameter(description = "是否覆盖已有课程", example = "false")
            @RequestParam(defaultValue = "false") Boolean overwrite) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = scheduleService.importScheduleFromExcel(userId, file, semester, overwrite);
        return ApiResponse.success("课程表导入完成", response);
    }

    @Operation(summary = "导出课程表（Excel）", description = "导出个人课程表为Excel文件")
    @GetMapping("/export/schedule-excel")
    public ResponseEntity<Resource> exportScheduleToExcel(
            HttpServletRequest request,
            @Parameter(description = "学期（如：2024-2025-1）")
            @RequestParam(required = false) String semester) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Resource resource = scheduleService.exportScheduleToExcel(userId, semester);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"schedule_" + semester + ".xlsx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Operation(summary = "获取课程表模板", description = "下载课程表导入模板Excel文件")
    @GetMapping("/import/template")
    public ResponseEntity<Resource> getScheduleTemplate() {
        Resource resource = scheduleService.getScheduleTemplate();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"schedule_template.xlsx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Operation(
            summary = "手动录入导入课程表", 
            description = "手动录入课程信息并导入。请求体格式：{\"semester\": \"2024-2025-1\", \"courses\": [{\"courseName\": \"高等数学\", \"dayOfWeek\": 1, \"timeSlot\": \"1-2节\", \"location\": \"教学楼A101\", \"teacher\": \"张老师\"}]}",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "课程信息，包含学期和课程列表",
                    required = true
            )
    )
    @PostMapping("/import/manual")
    public ApiResponse<Map<String, Object>> importScheduleManual(
            HttpServletRequest request,
            @RequestBody Map<String, Object> manualData) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = scheduleService.importScheduleManual(userId, manualData);
        return ApiResponse.success("导入成功", response);
    }

    @Operation(summary = "链接导入课程表", description = "通过教务系统链接导入课程表")
    @PostMapping("/import/from-link")
    public ApiResponse<Map<String, Object>> importScheduleFromLink(
            HttpServletRequest request,
            @Parameter(description = "课程表链接", required = true)
            @RequestParam String url,
            @Parameter(description = "账号（如果需要登录）")
            @RequestParam(required = false) String username,
            @Parameter(description = "密码（如果需要登录）")
            @RequestParam(required = false) String password) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = scheduleService.importScheduleFromLink(userId, url, username, password);
        return ApiResponse.success("导入成功", response);
    }

}