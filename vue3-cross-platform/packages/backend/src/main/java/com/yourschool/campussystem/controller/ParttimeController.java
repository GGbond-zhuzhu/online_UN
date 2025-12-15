package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.dto.ParttimeApplyDTO;
import com.yourschool.campussystem.dto.ParttimePublishDTO;
import com.yourschool.campussystem.dto.ParttimeQueryDTO;
import com.yourschool.campussystem.enums.ApplyStatusEnum;
import com.yourschool.campussystem.enums.ParttimeStatusEnum;
import com.yourschool.campussystem.service.ParttimeService;
import com.yourschool.campussystem.util.UserContextUtils;
import com.yourschool.campussystem.vo.ParttimeDetailVO;
import com.yourschool.campussystem.vo.ParttimeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/parttime")
@Tag(name = "兼职管理", description = "校园兼职信息发布、报名、管理功能")
@RequiredArgsConstructor
public class ParttimeController {

    private final ParttimeService parttimeService;

    @Operation(summary = "发布兼职", description = "教师/高校角色发布兼职信息")
    @PostMapping("/publish")
    public ApiResponse<ParttimeVO> publishParttime(
            HttpServletRequest request,
            @Parameter(description = "兼职信息", required = true)
            @Valid @RequestBody ParttimePublishDTO publishDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        ParttimeVO parttimeVO = parttimeService.publishParttime(userId, publishDTO);
        return ApiResponse.success("兼职发布成功", parttimeVO);
    }

    @Operation(summary = "兼职列表（分页）", description = "分页获取兼职列表，支持多种筛选条件，游客只能看到同校兼职")
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getParttimeList(
            HttpServletRequest request,
            @Parameter(description = "查询参数")
            @Valid ParttimeQueryDTO queryDTO) {
        Long currentUserId = UserContextUtils.getUserId(request);
        Map<String, Object> response = parttimeService.getParttimeList(queryDTO, currentUserId);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "兼职详情", description = "获取兼职详细信息，同校用户可见联系方式")
    @GetMapping("/detail/{id}")
    public ApiResponse<ParttimeDetailVO> getParttimeDetail(
            HttpServletRequest request,
            @Parameter(description = "兼职ID", example = "1", required = true)
            @PathVariable Long id) {
        Long currentUserId = UserContextUtils.getUserId(request);
        ParttimeDetailVO detailVO = parttimeService.getParttimeDetail(id, currentUserId);
        return ApiResponse.success("查询成功", detailVO);
    }

    @Operation(summary = "报名兼职", description = "学生角色报名兼职，需同校且未报名")
    @PostMapping("/apply")
    public ApiResponse<String> applyParttime(
            HttpServletRequest request,
            @Parameter(description = "报名信息", required = true)
            @Valid @RequestBody ParttimeApplyDTO applyDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        parttimeService.applyParttime(userId, applyDTO);
        return ApiResponse.success("报名成功，等待审核");
    }

    @Operation(summary = "取消报名", description = "取消已报名的兼职")
    @DeleteMapping("/apply/{id}")
    public ApiResponse<String> cancelApply(
            HttpServletRequest request,
            @Parameter(description = "报名记录ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        parttimeService.cancelApply(userId, id);
        return ApiResponse.success("报名已取消");
    }

    @Operation(summary = "获取我的报名记录", description = "获取当前用户的兼职报名记录")
    @GetMapping("/my-applications")
    public ApiResponse<Map<String, Object>> getMyApplications(
            HttpServletRequest request,
            @Parameter(description = "报名状态筛选", example = "PENDING")
            @RequestParam(required = false) ApplyStatusEnum status,

            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = parttimeService.getMyApplications(userId, status, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "获取我发布的兼职", description = "获取当前用户发布的兼职列表")
    @GetMapping("/my-published")
    public ApiResponse<Map<String, Object>> getMyPublished(
            HttpServletRequest request,
            @Parameter(description = "兼职状态筛选", example = "RECRUITING")
            @RequestParam(required = false) ParttimeStatusEnum status,

            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = parttimeService.getMyPublished(userId, status, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "获取兼职报名列表", description = "获取指定兼职的报名记录列表（仅发布者可见）")
    @GetMapping("/{id}/applications")
    public ApiResponse<Map<String, Object>> getParttimeApplications(
            HttpServletRequest request,
            @Parameter(description = "兼职ID", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(description = "报名状态筛选", example = "PENDING")
            @RequestParam(required = false) ApplyStatusEnum status,

            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = parttimeService.getParttimeApplications(userId, id, status, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "处理报名申请", description = "兼职发布者处理报名申请（通过/拒绝）")
    @PutMapping("/applications/{applicationId}/process")
    public ApiResponse<String> processApplication(
            HttpServletRequest request,
            @Parameter(description = "报名记录ID", example = "1", required = true)
            @PathVariable Long applicationId,

            @Parameter(description = "处理结果（APPROVED/REJECTED）", example = "APPROVED", required = true)
            @RequestParam ApplyStatusEnum result,

            @Parameter(description = "处理备注")
            @RequestParam(required = false) String processNote) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        parttimeService.processApplication(userId, applicationId, result, processNote);
        String resultMsg = result == ApplyStatusEnum.APPROVED ? "通过" : "拒绝";
        return ApiResponse.success("报名申请已" + resultMsg);
    }

    @Operation(summary = "修改兼职信息", description = "兼职发布者修改兼职信息")
    @PutMapping("/update/{id}")
    public ApiResponse<ParttimeVO> updateParttime(
            HttpServletRequest request,
            @Parameter(description = "兼职ID", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(description = "修改的兼职信息", required = true)
            @Valid @RequestBody ParttimePublishDTO updateDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        ParttimeVO parttimeVO = parttimeService.updateParttime(userId, id, updateDTO);
        return ApiResponse.success("兼职信息修改成功", parttimeVO);
    }

    @Operation(summary = "更新兼职状态", description = "兼职发布者更新兼职状态")
    @PutMapping("/update-status/{id}")
    public ApiResponse<String> updateParttimeStatus(
            HttpServletRequest request,
            @Parameter(description = "兼职ID", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(description = "新的状态", example = "CLOSED", required = true)
            @RequestParam ParttimeStatusEnum status) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        parttimeService.updateParttimeStatus(userId, id, status);
        return ApiResponse.success("兼职状态已更新为：" + status.getDescription());
    }

    @Operation(summary = "删除兼职", description = "兼职发布者删除兼职（需无进行中的报名）")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteParttime(
            HttpServletRequest request,
            @Parameter(description = "兼职ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        parttimeService.deleteParttime(userId, id);
        return ApiResponse.success("兼职删除成功");
    }

    @Operation(summary = "收藏兼职岗位", description = "用户收藏兼职岗位")
    @PostMapping("/favorite/{id}")
    public ApiResponse<String> favoriteParttime(
            HttpServletRequest request,
            @Parameter(description = "兼职ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        parttimeService.favoriteParttime(userId, id);
        return ApiResponse.success("收藏成功");
    }

    @Operation(summary = "取消收藏兼职岗位", description = "用户取消收藏兼职岗位")
    @DeleteMapping("/favorite/{id}")
    public ApiResponse<String> cancelFavoriteParttime(
            HttpServletRequest request,
            @Parameter(description = "兼职ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        parttimeService.cancelFavoriteParttime(userId, id);
        return ApiResponse.success("已取消收藏");
    }

    @Operation(summary = "获取收藏列表", description = "获取当前用户收藏的兼职岗位列表")
    @GetMapping("/favorites")
    public ApiResponse<Map<String, Object>> getParttimeFavorites(
            HttpServletRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = parttimeService.getParttimeFavorites(userId, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "清空收藏", description = "清空当前用户的所有兼职收藏")
    @DeleteMapping("/favorites/clear")
    public ApiResponse<String> clearParttimeFavorites(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        parttimeService.clearParttimeFavorites(userId);
        return ApiResponse.success("清空成功");
    }

    @Operation(summary = "记录岗位浏览行为", description = "记录用户浏览兼职岗位的行为")
    @PostMapping("/browse/{id}")
    public ApiResponse<String> recordBrowse(
            HttpServletRequest request,
            @Parameter(description = "兼职ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        parttimeService.recordBrowse(userId, id);
        return ApiResponse.success("浏览记录已保存");
    }

    @Operation(summary = "获取浏览记录列表", description = "获取当前用户的兼职岗位浏览记录")
    @GetMapping("/browse-history")
    public ApiResponse<Map<String, Object>> getBrowseHistory(
            HttpServletRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = parttimeService.getBrowseHistory(userId, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "删除单条浏览记录", description = "删除指定的浏览记录")
    @DeleteMapping("/browse-history/{id}")
    public ApiResponse<String> deleteBrowseHistory(
            HttpServletRequest request,
            @Parameter(description = "浏览记录ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        parttimeService.deleteBrowseHistory(userId, id);
        return ApiResponse.success("删除成功");
    }

    @Operation(summary = "清空浏览记录", description = "清空当前用户的所有浏览记录")
    @DeleteMapping("/browse-history/clear")
    public ApiResponse<String> clearBrowseHistory(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        parttimeService.clearBrowseHistory(userId);
        return ApiResponse.success("清空成功");
    }
}