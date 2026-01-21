package com.yourschool.campussystem.service;

import com.yourschool.campussystem.dto.EcardConsumeDTO;
import com.yourschool.campussystem.dto.VisitorCardApplyDTO;
import com.yourschool.campussystem.vo.ConsumeRecordVO;
import com.yourschool.campussystem.vo.EcardVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 校园卡服务接口
 */
public interface EcardService {

    /**
     * 获取校园卡信息
     */
    EcardVO getCardInfo(Long userId);

    /**
     * 校园卡消费
     */
    ConsumeRecordVO consume(Long userId, EcardConsumeDTO consumeDTO);

    /**
     * 查询消费记录
     */
    List<ConsumeRecordVO> getConsumeRecords(Long userId, String startDate, String endDate, 
                                            String consumeType, Integer page, Integer size);

    /**
     * 申请游客临时卡
     */
    EcardVO applyVisitorCard(VisitorCardApplyDTO applyDTO);

    /**
     * 挂失校园卡
     */
    void reportLoss(Long userId);

    /**
     * 解挂校园卡
     */
    void cancelLoss(Long userId);

    /**
     * 充值校园卡
     */
    EcardVO recharge(Long userId, BigDecimal amount);

    /**
     * 校验定位是否在校内
     */
    boolean checkLocation(Long schoolId, BigDecimal longitude, BigDecimal latitude);

    /**
     * 获取今日消费统计
     */
    Object getTodayStatistics(Long userId);

    /**
     * 生成动态学生码
     */
    Object generateDynamicCode(Long userId);

    /**
     * 获取月度消费统计
     */
    Object getMonthStatistics(Long userId, Integer year, Integer month);

    /**
     * 设置消费目标
     */
    Object setConsumeGoal(Long userId, java.math.BigDecimal monthlyGoal);

    /**
     * 获取消费目标
     */
    Object getConsumeGoal(Long userId);

    /**
     * 设置余额提醒
     */
    Object setBalanceReminder(Long userId, java.math.BigDecimal threshold, Boolean enabled);

    /**
     * 获取余额提醒设置
     */
    Object getBalanceReminder(Long userId);

    /**
     * 转账功能
     */
    Object transfer(Long userId, String targetCardNo, java.math.BigDecimal amount);
}
