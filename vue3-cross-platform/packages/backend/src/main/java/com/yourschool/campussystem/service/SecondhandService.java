package com.yourschool.campussystem.service;

import com.yourschool.campussystem.dto.SecondhandPublishDTO;
import com.yourschool.campussystem.dto.SecondhandQueryDTO;
import com.yourschool.campussystem.vo.SecondhandDetailVO;
import com.yourschool.campussystem.vo.SecondhandGoodsVO;

import java.util.List;
import java.util.Map;

/**
 * 二手交易服务接口
 */
public interface SecondhandService {

    /**
     * 发布二手商品
     */
    SecondhandGoodsVO publishGoods(Long userId, SecondhandPublishDTO publishDTO);

    /**
     * 查询商品列表（分页）
     */
    Map<String, Object> getGoodsList(SecondhandQueryDTO queryDTO, Long currentUserId);

    /**
     * 获取商品详情
     */
    SecondhandDetailVO getGoodsDetail(Long goodsId, Long currentUserId);

    /**
     * 获取我的商品列表
     */
    Map<String, Object> getMyGoods(Long userId, Integer page, Integer size, String status);

    /**
     * 修改商品信息
     */
    SecondhandGoodsVO updateGoods(Long userId, Long goodsId, SecondhandPublishDTO updateDTO);

    /**
     * 下架商品
     */
    void offShelfGoods(Long userId, Long goodsId);

    /**
     * 重新上架商品
     */
    void onShelfGoods(Long userId, Long goodsId);

    /**
     * 删除商品
     */
    void deleteGoods(Long userId, Long goodsId);

    /**
     * 收藏商品
     */
    void favoriteGoods(Long userId, Long goodsId);

    /**
     * 取消收藏
     */
    void cancelFavorite(Long userId, Long goodsId);

    /**
     * 获取收藏列表
     */
    Map<String, Object> getFavorites(Long userId, Integer page, Integer size);

    /**
     * 获取商品分类列表
     */
    List<Map<String, Object>> getCategories();

    /**
     * 记录商品浏览行为
     */
    void recordBrowse(Long userId, Long goodsId);

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
