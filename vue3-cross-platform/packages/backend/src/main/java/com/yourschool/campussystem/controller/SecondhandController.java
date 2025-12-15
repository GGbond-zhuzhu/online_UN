package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.dto.SecondhandPublishDTO;
import com.yourschool.campussystem.dto.SecondhandQueryDTO;
import com.yourschool.campussystem.service.SecondhandService;
import com.yourschool.campussystem.util.UserContextUtils;
import com.yourschool.campussystem.vo.SecondhandDetailVO;
import com.yourschool.campussystem.vo.SecondhandGoodsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/secondhand")
@Tag(name = "二手交易平台", description = "校园二手商品交易功能：发布、浏览、搜索、收藏、管理等")
@RequiredArgsConstructor
public class SecondhandController {

    private final SecondhandService secondhandService;

    @Operation(summary = "发布二手商品", description = "学生/教师发布二手商品，需要登录且角色为学生或教师")
    @PostMapping("/publish")
    public ApiResponse<SecondhandGoodsVO> publishGoods(
            HttpServletRequest request,
            @Parameter(description = "商品信息", required = true)
            @Valid @RequestBody SecondhandPublishDTO publishDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        SecondhandGoodsVO goodsVO = secondhandService.publishGoods(userId, publishDTO);
        return ApiResponse.success("商品发布成功", goodsVO);
    }

    @Operation(summary = "商品列表（分页）", description = "分页获取二手商品列表，支持多种筛选条件")
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getGoodsList(
            HttpServletRequest request,
            @Parameter(description = "查询参数")
            @Valid SecondhandQueryDTO queryDTO) {
        Long currentUserId = UserContextUtils.getUserId(request);
        Map<String, Object> response = secondhandService.getGoodsList(queryDTO, currentUserId);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "商品详情", description = "获取商品详细信息，同校用户可见联系方式")
    @GetMapping("/detail/{id}")
    public ApiResponse<SecondhandDetailVO> getGoodsDetail(
            HttpServletRequest request,
            @Parameter(description = "商品ID", example = "1001", required = true)
            @PathVariable Long id) {
        Long currentUserId = UserContextUtils.getUserId(request);
        SecondhandDetailVO detailVO = secondhandService.getGoodsDetail(id, currentUserId);
        return ApiResponse.success("查询成功", detailVO);
    }

    @Operation(summary = "获取我的商品", description = "获取当前用户发布的商品列表")
    @GetMapping("/my-goods")
    public ApiResponse<Map<String, Object>> getMyGoods(
            HttpServletRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size,

            @Parameter(description = "商品状态筛选", example = "ON_SALE")
            @RequestParam(required = false) String status) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = secondhandService.getMyGoods(userId, page, size, status);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "修改商品信息", description = "修改已发布的商品信息")
    @PutMapping("/update/{id}")
    public ApiResponse<SecondhandGoodsVO> updateGoods(
            HttpServletRequest request,
            @Parameter(description = "商品ID", example = "1001", required = true)
            @PathVariable Long id,

            @Parameter(description = "修改的商品信息", required = true)
            @Valid @RequestBody SecondhandPublishDTO updateDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        SecondhandGoodsVO goodsVO = secondhandService.updateGoods(userId, id, updateDTO);
        return ApiResponse.success("商品修改成功", goodsVO);
    }

    @Operation(summary = "下架商品", description = "发布者下架自己的商品")
    @PutMapping("/off-shelf/{id}")
    public ApiResponse<String> offShelfGoods(
            HttpServletRequest request,
            @Parameter(description = "商品ID", example = "1001", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        secondhandService.offShelfGoods(userId, id);
        return ApiResponse.success("商品已下架");
    }

    @Operation(summary = "重新上架商品", description = "发布者重新上架已下架的商品")
    @PutMapping("/on-shelf/{id}")
    public ApiResponse<String> onShelfGoods(
            HttpServletRequest request,
            @Parameter(description = "商品ID", example = "1001", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        secondhandService.onShelfGoods(userId, id);
        return ApiResponse.success("商品已重新上架");
    }

    @Operation(summary = "删除商品", description = "发布者删除自己的商品（需商品已下架）")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteGoods(
            HttpServletRequest request,
            @Parameter(description = "商品ID", example = "1001", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        secondhandService.deleteGoods(userId, id);
        return ApiResponse.success("商品已删除");
    }

    @Operation(summary = "收藏商品", description = "用户收藏商品")
    @PostMapping("/favorite/{id}")
    public ApiResponse<String> favoriteGoods(
            HttpServletRequest request,
            @Parameter(description = "商品ID", example = "1001", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        secondhandService.favoriteGoods(userId, id);
        return ApiResponse.success("收藏成功");
    }

    @Operation(summary = "取消收藏", description = "用户取消收藏商品")
    @DeleteMapping("/favorite/{id}")
    public ApiResponse<String> cancelFavorite(
            HttpServletRequest request,
            @Parameter(description = "商品ID", example = "1001", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        secondhandService.cancelFavorite(userId, id);
        return ApiResponse.success("已取消收藏");
    }

    @Operation(summary = "获取收藏列表", description = "获取当前用户收藏的商品列表")
    @GetMapping("/favorites")
    public ApiResponse<Map<String, Object>> getFavorites(
            HttpServletRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = secondhandService.getFavorites(userId, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "获取商品分类", description = "获取所有商品分类列表")
    @GetMapping("/categories")
    public ApiResponse<List<Map<String, Object>>> getCategories() {
        List<Map<String, Object>> categories = secondhandService.getCategories();
        return ApiResponse.success("查询成功", categories);
    }

    @Operation(summary = "记录商品浏览行为", description = "记录用户浏览商品的行为，用于生成浏览记录")
    @PostMapping("/browse/{id}")
    public ApiResponse<String> recordBrowse(
            HttpServletRequest request,
            @Parameter(description = "商品ID", example = "1001", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        secondhandService.recordBrowse(userId, id);
        return ApiResponse.success("浏览记录已保存");
    }

    @Operation(summary = "获取浏览记录列表", description = "获取当前用户的二手商品浏览记录")
    @GetMapping("/browse-history")
    public ApiResponse<Map<String, Object>> getBrowseHistory(
            HttpServletRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        Map<String, Object> response = secondhandService.getBrowseHistory(userId, page, size);
        return ApiResponse.success("查询成功", response);
    }

    @Operation(summary = "删除单条浏览记录", description = "删除指定的浏览记录")
    @DeleteMapping("/browse-history/{id}")
    public ApiResponse<String> deleteBrowseHistory(
            HttpServletRequest request,
            @Parameter(description = "浏览记录ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        secondhandService.deleteBrowseHistory(userId, id);
        return ApiResponse.success("删除成功");
    }

    @Operation(summary = "清空浏览记录", description = "清空当前用户的所有浏览记录")
    @DeleteMapping("/browse-history/clear")
    public ApiResponse<String> clearBrowseHistory(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        secondhandService.clearBrowseHistory(userId);
        return ApiResponse.success("清空成功");
    }

}