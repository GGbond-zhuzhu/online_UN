package com.yourschool.campussystem.service;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 商户管理服务接口
 * 注意：当前实现为简化版本，实际业务中需要创建merchant表和deposit_record表
 */
public interface MerchantService {

    /**
     * 商户入驻申请
     */
    Map<String, Object> applyMerchant(Long userId, String merchantName, String contactName,
                                     String contactPhone, String contactEmail,
                                     MultipartFile businessLicense, MultipartFile legalIdCard,
                                     BigDecimal depositAmount, String merchantType, String businessScope);

    /**
     * 公司资格智能校验
     */
    Map<String, Object> verifyCompany(String creditCode, String companyName);

    /**
     * 获取商户信息
     */
    Map<String, Object> getMerchantInfo(Long userId);

    /**
     * 缴纳保证金（创建支付订单，返回支付二维码）
     */
    Map<String, Object> payDeposit(Long userId, BigDecimal amount, String payMethod);

    /**
     * 刷新支付二维码
     */
    Map<String, Object> refreshPaymentQrCode(String orderId);

    /**
     * 查询支付状态
     */
    Map<String, Object> queryPaymentStatus(String orderId);

    /**
     * 查询保证金记录
     */
    Map<String, Object> getDepositRecords(Long userId, Integer page, Integer size);

    /**
     * 获取商户发布的兼职列表
     */
    Map<String, Object> getMerchantJobs(Long userId, Integer page, Integer size);

    /**
     * 获取商户统计数据
     */
    Map<String, Object> getMerchantStatistics(Long userId);
}
