package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourschool.campussystem.dto.SecondhandPublishDTO;
import com.yourschool.campussystem.dto.SecondhandQueryDTO;
import com.yourschool.campussystem.entity.BrowseHistory;
import com.yourschool.campussystem.entity.Favorite;
import com.yourschool.campussystem.entity.SecondhandGoods;
import com.yourschool.campussystem.entity.User;
import com.yourschool.campussystem.entity.University;
import com.yourschool.campussystem.enums.CategoryEnum;
import com.yourschool.campussystem.enums.GoodsStatusEnum;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.BrowseHistoryMapper;
import com.yourschool.campussystem.mapper.FavoriteMapper;
import com.yourschool.campussystem.mapper.SecondhandGoodsMapper;
import com.yourschool.campussystem.mapper.UserMapper;
import com.yourschool.campussystem.mapper.UniversityMapper;
import com.yourschool.campussystem.service.SecondhandService;
import com.yourschool.campussystem.vo.SecondhandDetailVO;
import com.yourschool.campussystem.vo.SecondhandGoodsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 二手交易服务实现类
 */
@Service
@RequiredArgsConstructor
public class SecondhandServiceImpl extends ServiceImpl<SecondhandGoodsMapper, SecondhandGoods> implements SecondhandService {

    private final SecondhandGoodsMapper goodsMapper;
    private final FavoriteMapper favoriteMapper;
    private final BrowseHistoryMapper browseHistoryMapper;
    private final UserMapper userMapper;
    private final UniversityMapper universityMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public SecondhandGoodsVO publishGoods(Long userId, SecondhandPublishDTO publishDTO) {
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 创建商品
        SecondhandGoods goods = new SecondhandGoods();
        goods.setTitle(publishDTO.getTitle());
        goods.setDescription(publishDTO.getDescription());
        goods.setPrice(publishDTO.getPrice());
        goods.setOriginalPrice(publishDTO.getOriginalPrice());
        goods.setCategory(publishDTO.getCategory());
        goods.setStatus(publishDTO.getStatus() != null ? publishDTO.getStatus() : GoodsStatusEnum.ON_SALE);
        goods.setPublisherId(userId);
        goods.setSchoolId(user.getSchoolId());
        goods.setContactPhone(publishDTO.getContactPhone());
        goods.setContactWechat(publishDTO.getContactWechat());
        goods.setLocation(publishDTO.getLocation());
        goods.setFavoriteCount(0);
        goods.setViewCount(0);

        // 转换图片URL列表为JSON字符串
        if (publishDTO.getImageUrls() != null && !publishDTO.getImageUrls().isEmpty()) {
            try {
                goods.setImageUrls(objectMapper.writeValueAsString(publishDTO.getImageUrls()));
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.GOODS_IMAGE_ERROR);
            }
        }

        goodsMapper.insert(goods);

        return convertToVO(goods, user, null);
    }

    @Override
    public Map<String, Object> getGoodsList(SecondhandQueryDTO queryDTO, Long currentUserId) {
        // 构建查询条件
        LambdaQueryWrapper<SecondhandGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SecondhandGoods::getIsDeleted, 0);

        // 学校筛选（同校可见）
        if (queryDTO.getSchoolId() != null) {
            queryWrapper.eq(SecondhandGoods::getSchoolId, queryDTO.getSchoolId());
        }

        // 分类筛选
        if (queryDTO.getCategory() != null) {
            queryWrapper.eq(SecondhandGoods::getCategory, queryDTO.getCategory());
        }

        // 状态筛选
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(SecondhandGoods::getStatus, queryDTO.getStatus());
        } else {
            // 默认只显示在售商品
            queryWrapper.eq(SecondhandGoods::getStatus, GoodsStatusEnum.ON_SALE);
        }

        // 关键词搜索
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(SecondhandGoods::getTitle, queryDTO.getKeyword())
                    .or()
                    .like(SecondhandGoods::getDescription, queryDTO.getKeyword())
            );
        }

        // 价格范围筛选
        if (queryDTO.getMinPrice() != null) {
            queryWrapper.ge(SecondhandGoods::getPrice, queryDTO.getMinPrice());
        }
        if (queryDTO.getMaxPrice() != null) {
            queryWrapper.le(SecondhandGoods::getPrice, queryDTO.getMaxPrice());
        }

        // 排序处理
        String sortBy = queryDTO.getSortBy() != null ? queryDTO.getSortBy() : "publishTime";
        String sortOrder = queryDTO.getSortOrder() != null ? queryDTO.getSortOrder() : "DESC";
        
        if ("price".equals(sortBy) || "price_asc".equals(sortBy)) {
            if ("ASC".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(SecondhandGoods::getPrice);
            } else {
                queryWrapper.orderByDesc(SecondhandGoods::getPrice);
            }
        } else if ("viewCount".equals(sortBy) || "hot".equals(sortBy)) {
            // 热度排序：按浏览量+收藏数综合排序
            if ("ASC".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(SecondhandGoods::getViewCount)
                           .orderByAsc(SecondhandGoods::getFavoriteCount);
            } else {
                queryWrapper.orderByDesc(SecondhandGoods::getViewCount)
                           .orderByDesc(SecondhandGoods::getFavoriteCount);
            }
        } else if ("favoriteCount".equals(sortBy)) {
            if ("ASC".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(SecondhandGoods::getFavoriteCount);
            } else {
                queryWrapper.orderByDesc(SecondhandGoods::getFavoriteCount);
            }
        } else {
            // 默认按发布时间排序
            if ("ASC".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(SecondhandGoods::getPublishTime);
            } else {
                queryWrapper.orderByDesc(SecondhandGoods::getPublishTime);
            }
        }

        // 分页查询
        Page<SecondhandGoods> pageObj = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        Page<SecondhandGoods> result = goodsMapper.selectPage(pageObj, queryWrapper);

        // 转换为VO
        List<SecondhandGoodsVO> voList = result.getRecords().stream()
                .map(goods -> {
                    User publisher = userMapper.selectById(goods.getPublisherId());
                    University university = goods.getSchoolId() != null ? 
                            universityMapper.selectById(goods.getSchoolId()) : null;
                    return convertToVO(goods, publisher, university);
                })
                .collect(Collectors.toList());

        // 构建返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("list", voList);
        response.put("page", queryDTO.getPage());
        response.put("size", queryDTO.getSize());
        response.put("total", result.getTotal());
        response.put("totalPages", result.getPages());

        return response;
    }

    @Override
    public SecondhandDetailVO getGoodsDetail(Long goodsId, Long currentUserId) {
        // 查询商品
        SecondhandGoods goods = goodsMapper.selectById(goodsId);
        if (goods == null || goods.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.GOODS_NOT_EXIST);
        }

        // 增加浏览次数
        goods.setViewCount(goods.getViewCount() + 1);
        goodsMapper.updateById(goods);

        // 查询发布者信息
        User publisher = userMapper.selectById(goods.getPublisherId());
        if (publisher == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 查询学校信息
        University university = goods.getSchoolId() != null ? 
                universityMapper.selectById(goods.getSchoolId()) : null;

        // 查询当前用户是否收藏
        boolean isFavorited = false;
        if (currentUserId != null) {
            LambdaQueryWrapper<Favorite> favoriteQuery = new LambdaQueryWrapper<>();
            favoriteQuery.eq(Favorite::getUserId, currentUserId)
                    .eq(Favorite::getTargetType, "SECONDHAND")
                    .eq(Favorite::getTargetId, goodsId);
            isFavorited = favoriteMapper.selectCount(favoriteQuery) > 0;
        }

        // 判断是否同校
        boolean isSameSchool = false;
        if (currentUserId != null) {
            User currentUser = userMapper.selectById(currentUserId);
            isSameSchool = currentUser != null && 
                    currentUser.getSchoolId() != null && 
                    currentUser.getSchoolId().equals(goods.getSchoolId());
        }

        // 解析图片URL列表
        List<String> imageUrls = new ArrayList<>();
        if (StringUtils.hasText(goods.getImageUrls())) {
            try {
                imageUrls = objectMapper.readValue(goods.getImageUrls(), new TypeReference<List<String>>() {});
            } catch (Exception e) {
                // 解析失败，使用空列表
            }
        }

        // 构建详情VO
        SecondhandDetailVO detailVO = new SecondhandDetailVO();
        detailVO.setId(goods.getId());
        detailVO.setTitle(goods.getTitle());
        detailVO.setDescription(goods.getDescription());
        detailVO.setPrice(goods.getPrice());
        detailVO.setOriginalPrice(goods.getOriginalPrice());
        detailVO.setCategory(goods.getCategory());
        detailVO.setStatus(goods.getStatus());
        detailVO.setImageUrls(imageUrls);
        detailVO.setPublisherId(goods.getPublisherId());
        detailVO.setPublisherName(publisher.getNickname() != null ? publisher.getNickname() : publisher.getUsername());
        detailVO.setPublisherAvatar(publisher.getAvatarUrl());
        detailVO.setSchoolId(goods.getSchoolId());
        detailVO.setSchoolName(university != null ? university.getName() : null);
        detailVO.setFavoriteCount(goods.getFavoriteCount());
        detailVO.setViewCount(goods.getViewCount());
        detailVO.setPublishTime(goods.getPublishTime());
        detailVO.setIsFavorited(isFavorited);
        detailVO.setIsPublisher(currentUserId != null && currentUserId.equals(goods.getPublisherId()));
        detailVO.setIsSameSchool(isSameSchool);

        // 同校可见联系方式
        if (isSameSchool || detailVO.getIsPublisher()) {
            detailVO.setContactPhone(goods.getContactPhone());
            detailVO.setContactWechat(goods.getContactWechat());
            detailVO.setLocation(goods.getLocation());
        }

        return detailVO;
    }

    @Override
    public Map<String, Object> getMyGoods(Long userId, Integer page, Integer size, String status) {
        LambdaQueryWrapper<SecondhandGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SecondhandGoods::getPublisherId, userId)
                .eq(SecondhandGoods::getIsDeleted, 0);

        if (status != null && !status.isEmpty()) {
            queryWrapper.eq(SecondhandGoods::getStatus, GoodsStatusEnum.valueOf(status));
        }

        queryWrapper.orderByDesc(SecondhandGoods::getPublishTime);

        Page<SecondhandGoods> pageObj = new Page<>(page, size);
        Page<SecondhandGoods> result = goodsMapper.selectPage(pageObj, queryWrapper);

        List<SecondhandGoodsVO> voList = result.getRecords().stream()
                .map(goods -> {
                    User publisher = userMapper.selectById(goods.getPublisherId());
                    University university = goods.getSchoolId() != null ? 
                            universityMapper.selectById(goods.getSchoolId()) : null;
                    return convertToVO(goods, publisher, university);
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", voList);
        response.put("total", result.getTotal());

        return response;
    }

    @Override
    @Transactional
    public SecondhandGoodsVO updateGoods(Long userId, Long goodsId, SecondhandPublishDTO updateDTO) {
        // 查询商品
        SecondhandGoods goods = goodsMapper.selectById(goodsId);
        if (goods == null || goods.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.GOODS_NOT_EXIST);
        }

        // 检查权限
        if (!goods.getPublisherId().equals(userId)) {
            throw new BusinessException(ErrorCode.GOODS_OPERATION_DENIED);
        }

        // 更新商品信息
        goods.setTitle(updateDTO.getTitle());
        goods.setDescription(updateDTO.getDescription());
        goods.setPrice(updateDTO.getPrice());
        goods.setOriginalPrice(updateDTO.getOriginalPrice());
        goods.setCategory(updateDTO.getCategory());
        goods.setContactPhone(updateDTO.getContactPhone());
        goods.setContactWechat(updateDTO.getContactWechat());
        goods.setLocation(updateDTO.getLocation());

        // 更新图片
        if (updateDTO.getImageUrls() != null) {
            try {
                goods.setImageUrls(objectMapper.writeValueAsString(updateDTO.getImageUrls()));
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.GOODS_IMAGE_ERROR);
            }
        }

        goodsMapper.updateById(goods);

        User publisher = userMapper.selectById(userId);
        University university = goods.getSchoolId() != null ? 
                universityMapper.selectById(goods.getSchoolId()) : null;
        return convertToVO(goods, publisher, university);
    }

    @Override
    @Transactional
    public void offShelfGoods(Long userId, Long goodsId) {
        SecondhandGoods goods = goodsMapper.selectById(goodsId);
        if (goods == null || goods.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.GOODS_NOT_EXIST);
        }

        if (!goods.getPublisherId().equals(userId)) {
            throw new BusinessException(ErrorCode.GOODS_OPERATION_DENIED);
        }

        goods.setStatus(GoodsStatusEnum.OFF_SHELF);
        goodsMapper.updateById(goods);
    }

    @Override
    @Transactional
    public void onShelfGoods(Long userId, Long goodsId) {
        SecondhandGoods goods = goodsMapper.selectById(goodsId);
        if (goods == null || goods.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.GOODS_NOT_EXIST);
        }

        if (!goods.getPublisherId().equals(userId)) {
            throw new BusinessException(ErrorCode.GOODS_OPERATION_DENIED);
        }

        goods.setStatus(GoodsStatusEnum.ON_SALE);
        goodsMapper.updateById(goods);
    }

    @Override
    @Transactional
    public void deleteGoods(Long userId, Long goodsId) {
        SecondhandGoods goods = goodsMapper.selectById(goodsId);
        if (goods == null || goods.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.GOODS_NOT_EXIST);
        }

        if (!goods.getPublisherId().equals(userId)) {
            throw new BusinessException(ErrorCode.GOODS_OPERATION_DENIED);
        }

        // 只能删除已下架的商品
        if (goods.getStatus() != GoodsStatusEnum.OFF_SHELF) {
            throw new BusinessException(ErrorCode.GOODS_STATUS_ERROR);
        }

        // 逻辑删除
        goods.setIsDeleted(1);
        goodsMapper.updateById(goods);
    }

    @Override
    @Transactional
    public void favoriteGoods(Long userId, Long goodsId) {
        // 检查商品是否存在
        SecondhandGoods goods = goodsMapper.selectById(goodsId);
        if (goods == null || goods.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.GOODS_NOT_EXIST);
        }

        // 检查是否已收藏
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, "SECONDHAND")
                .eq(Favorite::getTargetId, goodsId);
        if (favoriteMapper.selectCount(queryWrapper) > 0) {
            return; // 已收藏，直接返回
        }

        // 添加收藏
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setTargetType("SECONDHAND");
        favorite.setTargetId(goodsId);
        favoriteMapper.insert(favorite);

        // 更新商品收藏数
        goods.setFavoriteCount(goods.getFavoriteCount() + 1);
        goodsMapper.updateById(goods);
    }

    @Override
    @Transactional
    public void cancelFavorite(Long userId, Long goodsId) {
        // 删除收藏记录
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, "SECONDHAND")
                .eq(Favorite::getTargetId, goodsId);
        favoriteMapper.delete(queryWrapper);

        // 更新商品收藏数
        SecondhandGoods goods = goodsMapper.selectById(goodsId);
        if (goods != null && goods.getFavoriteCount() > 0) {
            goods.setFavoriteCount(goods.getFavoriteCount() - 1);
            goodsMapper.updateById(goods);
        }
    }

    @Override
    public Map<String, Object> getFavorites(Long userId, Integer page, Integer size) {
        // 查询收藏记录
        LambdaQueryWrapper<Favorite> favoriteQuery = new LambdaQueryWrapper<>();
        favoriteQuery.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, "SECONDHAND")
                .orderByDesc(Favorite::getCreateTime);

        // 注意：这里需要手动实现分页，因为Favorite表没有直接关联商品表
        List<Favorite> favorites = favoriteMapper.selectList(favoriteQuery);
        long total = favorites.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, favorites.size());
        List<Favorite> pageFavorites = favorites.subList(start, end);

        // 查询对应的商品
        List<SecondhandGoodsVO> voList = pageFavorites.stream()
                .map(favorite -> {
                    SecondhandGoods goods = goodsMapper.selectById(favorite.getTargetId());
                    if (goods != null && goods.getIsDeleted() == 0) {
                        User publisher = userMapper.selectById(goods.getPublisherId());
                        University university = goods.getSchoolId() != null ? 
                                universityMapper.selectById(goods.getSchoolId()) : null;
                        return convertToVO(goods, publisher, university);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", voList);
        response.put("total", total);

        return response;
    }

    @Override
    public List<Map<String, Object>> getCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();
        for (CategoryEnum category : CategoryEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("value", category.name());
            item.put("label", category.getDescription());
            categories.add(item);
        }
        return categories;
    }

    @Override
    @Transactional
    public void recordBrowse(Long userId, Long goodsId) {
        // 检查商品是否存在
        SecondhandGoods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "商品不存在");
        }

        // 检查是否已有浏览记录（同一天内不重复记录）
        LambdaQueryWrapper<BrowseHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BrowseHistory::getUserId, userId)
                .eq(BrowseHistory::getContentType, "SECONDHAND")
                .eq(BrowseHistory::getContentId, goodsId)
                .ge(BrowseHistory::getViewTime, LocalDateTime.now().toLocalDate().atStartOfDay())
                .orderByDesc(BrowseHistory::getViewTime)
                .last("LIMIT 1");
        
        BrowseHistory existingHistory = browseHistoryMapper.selectOne(queryWrapper);
        
        if (existingHistory == null) {
            // 创建新的浏览记录
            BrowseHistory history = new BrowseHistory();
            history.setUserId(userId);
            history.setContentType("SECONDHAND");
            history.setContentId(goodsId);
            browseHistoryMapper.insert(history);
        } else {
            // 更新浏览时间
            existingHistory.setViewTime(LocalDateTime.now());
            browseHistoryMapper.updateById(existingHistory);
        }

        // 更新商品浏览量
        goods.setViewCount((goods.getViewCount() != null ? goods.getViewCount() : 0) + 1);
        goodsMapper.updateById(goods);
    }

    @Override
    public Map<String, Object> getBrowseHistory(Long userId, Integer page, Integer size) {
        // 查询浏览记录
        LambdaQueryWrapper<BrowseHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BrowseHistory::getUserId, userId)
                .eq(BrowseHistory::getContentType, "SECONDHAND")
                .orderByDesc(BrowseHistory::getViewTime);

        Page<BrowseHistory> pageObj = new Page<>(page, size);
        Page<BrowseHistory> result = browseHistoryMapper.selectPage(pageObj, queryWrapper);

        // 获取商品ID列表
        List<Long> goodsIds = result.getRecords().stream()
                .map(BrowseHistory::getContentId)
                .distinct()
                .collect(Collectors.toList());

        if (goodsIds.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("records", new ArrayList<>());
            response.put("total", 0L);
            response.put("page", page);
            response.put("size", size);
            return response;
        }

        // 批量查询商品信息（使用in查询替代已弃用的selectBatchIds）
        LambdaQueryWrapper<SecondhandGoods> goodsQuery = new LambdaQueryWrapper<>();
        goodsQuery.in(SecondhandGoods::getId, goodsIds);
        List<SecondhandGoods> goodsList = goodsMapper.selectList(goodsQuery);
        Map<Long, SecondhandGoods> goodsMap = goodsList.stream()
                .collect(Collectors.toMap(SecondhandGoods::getId, g -> g));

        // 构建返回数据
        List<Map<String, Object>> records = new ArrayList<>();
        for (BrowseHistory history : result.getRecords()) {
            SecondhandGoods goods = goodsMap.get(history.getContentId());
            if (goods != null) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", history.getId());
                record.put("goodsId", goods.getId());
                record.put("goodsTitle", goods.getTitle());
                
                // 解析第一张图片
                if (StringUtils.hasText(goods.getImageUrls())) {
                    try {
                        List<String> imageUrls = objectMapper.readValue(goods.getImageUrls(), 
                                new TypeReference<List<String>>() {});
                        record.put("goodsImage", imageUrls.isEmpty() ? null : imageUrls.get(0));
                    } catch (Exception e) {
                        record.put("goodsImage", null);
                    }
                } else {
                    record.put("goodsImage", null);
                }
                
                record.put("price", goods.getPrice());
                record.put("viewTime", history.getViewTime());
                records.add(record);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("records", records);
        response.put("total", result.getTotal());
        response.put("page", page);
        response.put("size", size);

        return response;
    }

    @Override
    @Transactional
    public void deleteBrowseHistory(Long userId, Long historyId) {
        // 验证浏览记录是否属于当前用户
        BrowseHistory history = browseHistoryMapper.selectById(historyId);
        if (history == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "浏览记录不存在");
        }
        if (!history.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权删除此浏览记录");
        }
        browseHistoryMapper.deleteById(historyId);
    }

    @Override
    @Transactional
    public void clearBrowseHistory(Long userId) {
        LambdaQueryWrapper<BrowseHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BrowseHistory::getUserId, userId)
                .eq(BrowseHistory::getContentType, "SECONDHAND");
        browseHistoryMapper.delete(queryWrapper);
    }

    /**
     * 转换商品实体为VO
     */
    private SecondhandGoodsVO convertToVO(SecondhandGoods goods, User publisher, University university) {
        SecondhandGoodsVO vo = new SecondhandGoodsVO();
        vo.setId(goods.getId());
        vo.setTitle(goods.getTitle());
        vo.setDescription(goods.getDescription());
        vo.setPrice(goods.getPrice());
        vo.setOriginalPrice(goods.getOriginalPrice());
        vo.setCategory(goods.getCategory());
        vo.setStatus(goods.getStatus());

        // 解析图片URL列表
        if (StringUtils.hasText(goods.getImageUrls())) {
            try {
                vo.setImageUrls(objectMapper.readValue(goods.getImageUrls(), new TypeReference<List<String>>() {}));
            } catch (Exception e) {
                vo.setImageUrls(new ArrayList<>());
            }
        } else {
            vo.setImageUrls(new ArrayList<>());
        }

        vo.setPublisherId(goods.getPublisherId());
        vo.setPublisherName(publisher != null ? 
                (publisher.getNickname() != null ? publisher.getNickname() : publisher.getUsername()) : null);
        vo.setPublisherAvatar(publisher != null ? publisher.getAvatarUrl() : null);
        vo.setSchoolId(goods.getSchoolId());
        vo.setSchoolName(university != null ? university.getName() : null);
        vo.setFavoriteCount(goods.getFavoriteCount());
        vo.setViewCount(goods.getViewCount());
        vo.setPublishTime(goods.getPublishTime());
        vo.setUpdateTime(goods.getUpdateTime());

        return vo;
    }
}
