package com.yourschool.campussystem.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 支付服务实现类（模拟实现，使用无效二维码）
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${server.address:localhost}")
    private String serverAddress;

    // 存储订单信息（实际应该使用Redis或数据库）
    private final Map<String, PaymentOrder> orderCache = new ConcurrentHashMap<>();

    @Override
    public Map<String, Object> createPayment(String orderId, BigDecimal amount, String payMethod) {
        // 生成支付二维码内容（模拟的无效二维码）
        String qrCodeContent = generateMockQrCodeContent(orderId, amount, payMethod);
        
        // 生成二维码图片（Base64编码）
        String qrCodeBase64 = generateQrCodeImage(qrCodeContent);
        
        // 保存订单信息
        PaymentOrder order = new PaymentOrder();
        order.setOrderId(orderId);
        order.setAmount(amount);
        order.setPayMethod(payMethod);
        order.setStatus("PENDING");
        order.setQrCodeContent(qrCodeContent);
        order.setCreateTime(System.currentTimeMillis());
        orderCache.put(orderId, order);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("amount", amount);
        response.put("payMethod", payMethod);
        response.put("qrCodeUrl", "data:image/png;base64," + qrCodeBase64);
        response.put("qrCodeContent", qrCodeContent); // 可选：返回二维码内容供前端生成
        response.put("status", "PENDING");
        response.put("expireTime", System.currentTimeMillis() + 30 * 60 * 1000); // 30分钟过期
        response.put("message", "请使用" + (payMethod.equals("ALIPAY") ? "支付宝" : "微信") + "扫描二维码完成支付");

        log.info("创建支付订单: orderId={}, amount={}, payMethod={}", orderId, amount, payMethod);
        return response;
    }

    @Override
    public Map<String, Object> refreshPaymentQrCode(String orderId) {
        PaymentOrder order = orderCache.get(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }

        // 检查订单状态
        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "订单已支付或已取消，无法刷新二维码");
        }

        // 检查是否过期
        long expireTime = order.getCreateTime() + 30 * 60 * 1000; // 30分钟
        if (System.currentTimeMillis() > expireTime) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "订单已过期，请重新创建订单");
        }

        // 生成新的二维码内容（可以包含时间戳确保唯一性）
        String newQrCodeContent = generateMockQrCodeContent(orderId, order.getAmount(), order.getPayMethod());
        order.setQrCodeContent(newQrCodeContent);
        
        // 生成新的二维码图片
        String qrCodeBase64 = generateQrCodeImage(newQrCodeContent);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("qrCodeUrl", "data:image/png;base64," + qrCodeBase64);
        response.put("qrCodeContent", newQrCodeContent);
        response.put("expireTime", expireTime);
        response.put("message", "二维码已刷新，请重新扫描");

        log.info("刷新支付二维码: orderId={}", orderId);
        return response;
    }

    @Override
    public Map<String, Object> queryPaymentStatus(String orderId) {
        PaymentOrder order = orderCache.get(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("status", order.getStatus());
        response.put("amount", order.getAmount());
        response.put("payMethod", order.getPayMethod());
        response.put("createTime", order.getCreateTime());

        // 检查是否过期
        long expireTime = order.getCreateTime() + 30 * 60 * 1000;
        response.put("isExpired", System.currentTimeMillis() > expireTime);
        response.put("expireTime", expireTime);

        return response;
    }

    /**
     * 生成模拟的二维码内容（无效的支付链接）
     */
    private String generateMockQrCodeContent(String orderId, BigDecimal amount, String payMethod) {
        // 生成模拟的支付URL（无效，仅用于测试）
        String baseUrl = "http://" + serverAddress + ":" + serverPort;
        if (payMethod.equals("ALIPAY")) {
            // 模拟支付宝支付链接
            return baseUrl + "/mock/alipay/pay?orderId=" + orderId + "&amount=" + amount + "&timestamp=" + System.currentTimeMillis();
        } else if (payMethod.equals("WECHAT")) {
            // 模拟微信支付链接
            return baseUrl + "/mock/wechat/pay?orderId=" + orderId + "&amount=" + amount + "&timestamp=" + System.currentTimeMillis();
        } else {
            // 其他支付方式
            return baseUrl + "/mock/pay?orderId=" + orderId + "&amount=" + amount + "&payMethod=" + payMethod + "&timestamp=" + System.currentTimeMillis();
        }
    }

    /**
     * 生成二维码图片（Base64编码）
     */
    private String generateQrCodeImage(String content) {
        try {
            int width = 300;
            int height = 300;
            
            // 设置二维码参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);

            // 生成二维码
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            // 转换为图片
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.BLACK);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (bitMatrix.get(x, y)) {
                        graphics.fillRect(x, y, 1, 1);
                    }
                }
            }
            graphics.dispose();

            // 转换为Base64
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);

        } catch (WriterException | IOException e) {
            log.error("生成二维码失败", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "生成二维码失败: " + e.getMessage());
        }
    }

    /**
     * 支付订单信息（内部类）
     */
    private static class PaymentOrder {
        private String orderId;
        private BigDecimal amount;
        private String payMethod;
        private String status;
        private String qrCodeContent;
        private long createTime;

        // Getters and Setters
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getPayMethod() { return payMethod; }
        public void setPayMethod(String payMethod) { this.payMethod = payMethod; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getQrCodeContent() { return qrCodeContent; }
        public void setQrCodeContent(String qrCodeContent) { this.qrCodeContent = qrCodeContent; }
        public long getCreateTime() { return createTime; }
        public void setCreateTime(long createTime) { this.createTime = createTime; }
    }
}
