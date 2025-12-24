package com.yourschool.campussystem.service.impl; // 定义当前类所在的包名

import com.google.zxing.BarcodeFormat; // 引入ZXing条码格式枚举，用于生成二维码
import com.google.zxing.EncodeHintType; // 引入二维码编码参数枚举
import com.google.zxing.WriterException; // 引入WriterException异常类，用于处理二维码生成异常
import com.google.zxing.common.BitMatrix; // 引入BitMatrix类，表示二维码的像素矩阵
import com.google.zxing.qrcode.QRCodeWriter; // 引入QRCodeWriter类，用于生成二维码数据
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel; // 引入ErrorCorrectionLevel枚举，用于设置二维码纠错级别
import com.yourschool.campussystem.common.ErrorCode; // 引入自定义错误码枚举，统一异常状态码
import com.yourschool.campussystem.config.PaymentProperties; // 引入支付配置类，承载支付宝/微信等配置
import com.yourschool.campussystem.exception.BusinessException; // 引入业务异常类，用于在业务逻辑中抛出统一异常
import com.yourschool.campussystem.service.PaymentService; // 引入支付服务接口
import lombok.RequiredArgsConstructor; // 引入@RequiredArgsConstructor注解，用于生成包含final字段的构造方法
import lombok.extern.slf4j.Slf4j; // 引入Slf4j注解，方便记录日志
import org.springframework.beans.factory.annotation.Value; // 引入@Value注解，用于注入简单配置项
import org.springframework.stereotype.Service; // 引入@Service注解，将当前类标记为Spring服务组件

import javax.imageio.ImageIO; // 引入ImageIO类，用于把图片写入字节流
import java.awt.*; // 引入Awt图形库中的绘图相关类
import java.awt.image.BufferedImage; // 引入BufferedImage类，用于在内存中构造图片
import java.io.ByteArrayOutputStream; // 引入ByteArrayOutputStream类，用于接收图片字节
import java.io.IOException; // 引入IOException异常类
import java.math.BigDecimal; // 引入BigDecimal类，用于表示金额
import java.util.Base64; // 引入Base64工具类，用于将图片二进制转换为Base64字符串
import java.util.HashMap; // 引入HashMap类，用于构建返回结果
import java.util.Map; // 引入Map接口
import java.util.concurrent.ConcurrentHashMap; // 引入ConcurrentHashMap类，用于线程安全地缓存订单信息

/**
 * 支付服务实现类
 * 目前默认仍使用“模拟二维码”方式生成支付链接，但已预留支付宝/微信真实支付接口调用的骨架，
 * 方便后续在拿到商户号和密钥后直接在指定方法中补充真实下单逻辑。
 */
@Slf4j // 启用Lombok的日志功能，生成log字段
@Service // 将本类标记为Spring的Service组件
@RequiredArgsConstructor // 为所有final字段（如paymentProperties）生成构造函数，方便依赖注入
public class PaymentServiceImpl implements PaymentService { // 定义支付服务实现类并实现PaymentService接口

    @Value("${server.port:8080}") // 从配置文件中读取服务端口，默认值为8080
    private int serverPort; // 保存服务端口号，用于拼接模拟支付URL

    @Value("${server.address:localhost}") // 从配置文件中读取服务地址，默认值为localhost
    private String serverAddress; // 保存服务地址，用于拼接模拟支付URL

    private final PaymentProperties paymentProperties; // 注入支付配置类，用于判断是否启用真实支付通道等

    // 存储订单信息（实际应该使用Redis或数据库）
    private final Map<String, PaymentOrder> orderCache = new ConcurrentHashMap<>(); // 使用线程安全的Map缓存订单信息

    @Override
    public Map<String, Object> createPayment(String orderId, BigDecimal amount, String payMethod) { // 创建支付订单并生成二维码
        // 规范化支付方式字符串，避免大小写问题
        String normalizedPayMethod = payMethod != null ? payMethod.toUpperCase() : "UNKNOWN"; // 将支付方式统一转换为大写

        // 根据配置和支付方式决定使用真实支付骨架还是继续使用模拟支付链接
        String qrCodeContent; // 保存最终用于生成二维码的内容（URL或code_url）
        if ("ALIPAY".equals(normalizedPayMethod)
                && paymentProperties.getAlipay().isEnabled()
                && !paymentProperties.isMockEnabled()) { // 当为支付宝且配置中启用了支付宝通道，并且全局mock关闭
            qrCodeContent = buildAlipayPaymentUrl(orderId, amount); // 调用预留的支付宝支付URL构建方法（当前仍返回模拟链接）
        } else if ("WECHAT".equals(normalizedPayMethod)
                && paymentProperties.getWechat().isEnabled()
                && !paymentProperties.isMockEnabled()) { // 当为微信且配置中启用了微信通道，并且全局mock关闭
            qrCodeContent = buildWechatPaymentUrl(orderId, amount); // 调用预留的微信支付URL构建方法（当前仍返回模拟链接）
        } else { // 其他情况（包括未配置真实通道或仍处于mock模式）
            // 继续使用已有的模拟二维码逻辑，保证功能可用
            qrCodeContent = generateMockQrCodeContent(orderId, amount, normalizedPayMethod); // 调用模拟二维码内容生成方法
        }
        
        // 生成二维码图片（Base64编码）
        String qrCodeBase64 = generateQrCodeImage(qrCodeContent); // 根据最终内容生成二维码图片的Base64编码
        
        // 保存订单信息到本地缓存（生产环境建议使用Redis或数据库）
        PaymentOrder order = new PaymentOrder(); // 创建内部订单对象
        order.setOrderId(orderId); // 设置订单号
        order.setAmount(amount); // 设置订单金额
        order.setPayMethod(normalizedPayMethod); // 设置支付方式（使用规范化后的字符串）
        order.setStatus("PENDING"); // 初始状态设置为PENDING（待支付）
        order.setQrCodeContent(qrCodeContent); // 保存二维码原始内容
        order.setCreateTime(System.currentTimeMillis()); // 记录创建时间戳
        orderCache.put(orderId, order); // 将订单放入缓存Map中

        Map<String, Object> response = new HashMap<>(); // 准备返回给Controller的结果Map
        response.put("orderId", orderId); // 返回订单号
        response.put("amount", amount); // 返回订单金额
        response.put("payMethod", normalizedPayMethod); // 返回支付方式
        response.put("qrCodeUrl", "data:image/png;base64," + qrCodeBase64); // 返回可直接在前端展示的Base64二维码
        response.put("qrCodeContent", qrCodeContent); // 可选：返回二维码内容，前端也可以自行生成二维码
        response.put("status", "PENDING"); // 返回当前订单状态
        response.put("expireTime", System.currentTimeMillis() + 30 * 60 * 1000); // 设置30分钟后过期的时间戳
        response.put("message", "请使用" + ("ALIPAY".equals(normalizedPayMethod) ? "支付宝" : "微信") + "扫描二维码完成支付"); // 根据支付方式返回不同提示语

        log.info("创建支付订单: orderId={}, amount={}, payMethod={}", orderId, amount, normalizedPayMethod); // 记录创建订单日志
        return response; // 返回结果
    }

    @Override
    public Map<String, Object> refreshPaymentQrCode(String orderId) { // 刷新支付二维码
        PaymentOrder order = orderCache.get(orderId); // 从本地缓存中根据订单号获取订单
        if (order == null) { // 如果订单不存在
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在"); // 抛出“订单不存在”的业务异常
        }

        // 检查订单状态是否仍为待支付
        if (!"PENDING".equals(order.getStatus())) { // 如果订单状态不是PENDING
            throw new BusinessException(ErrorCode.BAD_REQUEST, "订单已支付或已取消，无法刷新二维码"); // 抛出业务异常提示不能刷新
        }

        // 检查订单是否已经过期（创建时间 + 30分钟）
        long expireTime = order.getCreateTime() + 30 * 60 * 1000; // 计算订单过期时间
        if (System.currentTimeMillis() > expireTime) { // 如果当前时间已经超过过期时间
            throw new BusinessException(ErrorCode.BAD_REQUEST, "订单已过期，请重新创建订单"); // 抛出业务异常提示订单过期
        }

        // 目前刷新二维码仍然统一走模拟逻辑（后续对接真实支付时，可在这里拆分支付宝/微信的刷新方式）
        String newQrCodeContent = generateMockQrCodeContent(orderId, order.getAmount(), order.getPayMethod()); // 生成新的模拟支付链接
        order.setQrCodeContent(newQrCodeContent); // 更新订单中的二维码内容
        
        // 根据新的内容生成二维码图片
        String qrCodeBase64 = generateQrCodeImage(newQrCodeContent); // 生成新的二维码Base64图片

        Map<String, Object> response = new HashMap<>(); // 构建返回结果
        response.put("orderId", orderId); // 返回订单号
        response.put("qrCodeUrl", "data:image/png;base64," + qrCodeBase64); // 返回二维码图片Base64
        response.put("qrCodeContent", newQrCodeContent); // 返回新的二维码内容
        response.put("expireTime", expireTime); // 返回订单过期时间
        response.put("message", "二维码已刷新，请重新扫描"); // 返回提示信息

        log.info("刷新支付二维码: orderId={}", orderId); // 记录刷新二维码日志
        return response; // 返回结果
    }

    @Override
    public Map<String, Object> queryPaymentStatus(String orderId) { // 查询支付状态
        PaymentOrder order = orderCache.get(orderId); // 根据订单号从本地缓存中查询订单
        if (order == null) { // 如果订单不存在
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在"); // 抛出“订单不存在”的业务异常
        }

        Map<String, Object> response = new HashMap<>(); // 构建返回结果
        response.put("orderId", orderId); // 返回订单号
        response.put("status", order.getStatus()); // 返回订单状态
        response.put("amount", order.getAmount()); // 返回订单金额
        response.put("payMethod", order.getPayMethod()); // 返回支付方式
        response.put("createTime", order.getCreateTime()); // 返回订单创建时间

        // 计算订单是否过期（创建时间 + 30分钟）
        long expireTime = order.getCreateTime() + 30 * 60 * 1000; // 计算过期时间
        response.put("isExpired", System.currentTimeMillis() > expireTime); // 标记当前是否已过期
        response.put("expireTime", expireTime); // 返回过期时间戳

        return response; // 返回支付状态信息
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
     * 预留：构建支付宝支付URL（真实支付骨架）
     * 后续接入时，可以在这里调用支付宝的统一收单下单接口，获取真实的支付链接或二维码URL。
     */
    private String buildAlipayPaymentUrl(String orderId, BigDecimal amount) {
        PaymentProperties.Alipay alipay = paymentProperties.getAlipay(); // 从支付配置中读取支付宝相关配置
        log.info("[支付骨架] 准备为订单创建支付宝支付链接: orderId={}, amount={}, appId={}", orderId, amount, alipay.getAppId()); // 打印当前准备调用支付宝的关键信息

        // TODO: 在这里接入支付宝SDK或HTTP调用支付宝统一下单接口，生成真实支付链接
        // 当前版本为了不影响现有功能，依然返回模拟二维码链接，仅作为真实支付逻辑的占位。

        return generateMockQrCodeContent(orderId, amount, "ALIPAY"); // 暂时复用模拟二维码生成逻辑，保证功能可用
    }

    /**
     * 预留：构建微信支付URL（真实支付骨架）
     * 后续接入时，可以在这里调用微信支付统一下单接口，获取code_url用于生成二维码。
     */
    private String buildWechatPaymentUrl(String orderId, BigDecimal amount) {
        PaymentProperties.Wechat wechat = paymentProperties.getWechat(); // 从支付配置中读取微信支付相关配置
        log.info("[支付骨架] 准备为订单创建微信支付链接: orderId={}, amount={}, mchId={}", orderId, amount, wechat.getMchId()); // 打印当前准备调用微信的关键信息

        // TODO: 在这里接入微信支付SDK或HTTP调用统一下单接口，生成真实的code_url
        // 当前版本为了不影响现有功能，依然返回模拟二维码链接，仅作为真实支付逻辑的占位。

        return generateMockQrCodeContent(orderId, amount, "WECHAT"); // 暂时复用模拟二维码生成逻辑，保证功能可用
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
