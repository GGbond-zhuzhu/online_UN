package com.yourschool.campussystem.config; // 定义所属的包名，位于config配置包下

import lombok.Data; // 引入Lombok的@Data注解，用于自动生成Getter/Setter等方法
import org.springframework.boot.context.properties.ConfigurationProperties; // 引入Spring Boot的@ConfigurationProperties注解，用于将配置文件中的属性映射到Java对象
import org.springframework.stereotype.Component; // 引入@Component注解，将本类交给Spring容器管理，方便在其他地方注入使用

/**
 * 支付相关配置类
 * 用于承载 application.properties 中以 payment.* 开头的配置项，
 * 为后续接入支付宝、微信真实支付接口预留统一的配置入口。
 */
@Data // 使用Lombok自动生成Getter/Setter、toString等方法，减少样板代码
@Component // 标记为Spring组件，使其可以通过依赖注入的方式在其他类中使用
@ConfigurationProperties(prefix = "payment") // 指定配置前缀为 payment，表示读取 payment.* 下的所有配置
public class PaymentProperties { // 定义PaymentProperties配置类的开始

    /**
     * 是否启用模拟支付模式
     * true 表示始终使用当前的“二维码模拟支付”（不真正调用支付宝/微信接口）
     * false 表示允许根据具体支付方式切换到真实支付实现（前提是对应的channel开启了enabled）
     */
    private boolean mockEnabled = true; // 默认开启模拟模式，保证在未配置商户信息时系统仍可正常运行

    /**
     * 支付宝相关配置
     */
    private Alipay alipay = new Alipay(); // 声明并初始化支付宝配置对象，避免空指针

    /**
     * 微信支付相关配置
     */
    private Wechat wechat = new Wechat(); // 声明并初始化微信支付配置对象，避免空指针

    /**
     * 支付宝配置内部类
     * 用于承载 payment.alipay.* 相关配置项
     */
    @Data // 为内部类同样生成Getter/Setter等方法
    public static class Alipay { // 定义支付宝配置内部类

        /**
         * 是否启用支付宝真实支付通道
         * 当为 false 时，即便配置了商户号等，也不会真正调用支付宝接口，仅用于占位
         */
        private boolean enabled = false; // 默认关闭，防止误触发真实支付逻辑

        /**
         * 支付宝开放平台应用的AppId
         * 来自 支付宝开放平台 控制台
         */
        private String appId; // 保存支付宝应用AppId

        /**
         * 商户应用私钥（用于生成请求签名）
         * 注意：生产环境请使用更安全的方式存储，例如环境变量或密钥管理服务
         */
        private String merchantPrivateKey; // 保存商户私钥字符串

        /**
         * 支付宝公钥（用于验签）
         * 来自 支付宝开放平台 公钥配置页
         */
        private String alipayPublicKey; // 保存支付宝公钥字符串

        /**
         * 支付结果异步通知地址
         * 支付宝会在用户支付完成后，以服务器回调的方式通知该URL
         */
        private String notifyUrl; // 保存支付宝异步通知回调地址

        /**
         * 支付完成后前端跳转地址
         * 用户支付结束后浏览器跳转到此地址，常用于H5场景的结果展示
         */
        private String returnUrl; // 保存支付宝前端回跳地址

        /**
         * 支付宝网关地址
         * 正式环境通常为 https://openapi.alipay.com/gateway.do
         * 沙箱环境通常为 https://openapi-sandbox.dl.alipaydev.com/gateway.do
         */
        private String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do"; // 默认使用沙箱网关，避免误连正式环境
    }

    /**
     * 微信支付配置内部类
     * 用于承载 payment.wechat.* 相关配置项
     */
    @Data // 为内部类同样生成Getter/Setter等方法
    public static class Wechat { // 定义微信支付配置内部类

        /**
         * 是否启用微信真实支付通道
         * 当为 false 时，仅作为配置预留占位，不会真正向微信支付发起下单请求
         */
        private boolean enabled = false; // 默认关闭真实微信支付

        /**
         * 微信支付商户号（mchid）
         */
        private String mchId; // 保存微信支付商户号

        /**
         * 微信公众平台/小程序/APP 的AppId
         * 根据业务类型选择对应的AppId
         */
        private String appId; // 保存微信支付关联的AppId

        /**
         * API密钥（V2版本）或APIv3密钥
         * 具体使用哪一种取决于后续接入的签名方式
         */
        private String apiKey; // 保存微信支付API密钥

        /**
         * 支付结果异步通知地址
         * 微信支付在用户完成支付后，会以回调的方式通知该URL
         */
        private String notifyUrl; // 保存微信支付异步通知回调地址

        /**
         * 统一下单接口URL
         * V2版本通常为 https://api.mch.weixin.qq.com/pay/unifiedorder
         */
        private String unifiedOrderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder"; // 默认使用微信支付V2统一下单地址
    }
}
