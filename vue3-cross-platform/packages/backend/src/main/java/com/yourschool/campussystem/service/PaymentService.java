package com.yourschool.campussystem.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 创建支付订单并生成支付二维码
     * @param orderId 订单号
     * @param amount 支付金额
     * @param payMethod 支付方式（ALIPAY/WECHAT）
     * @return 支付信息（包含二维码URL、订单号等）
     */
    Map<String, Object> createPayment(String orderId, BigDecimal amount, String payMethod);

    /**
     * 刷新支付二维码
     * @param orderId 订单号
     * @return 新的支付二维码URL
     */
    Map<String, Object> refreshPaymentQrCode(String orderId);

    /**
     * 查询支付状态
     * @param orderId 订单号
     * @return 支付状态信息
     */
    Map<String, Object> queryPaymentStatus(String orderId);
}
