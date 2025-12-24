package com.yourschool.campussystem.config; // 声明当前类所在的包名，位于config配置包下

import org.springframework.context.annotation.Configuration; // 引入Configuration注解，用于标记配置类
import org.springframework.messaging.simp.config.MessageBrokerRegistry; // 引入消息代理配置类，用于配置消息转发规则
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker; // 引入启用WebSocket消息代理的注解
import org.springframework.web.socket.config.annotation.StompEndpointRegistry; // 引入STOMP端点注册器，用于暴露WebSocket连接端点
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer; // 引入WebSocket消息代理配置接口

/**
 * WebSocket 配置类
 * 开启基于 STOMP 协议的 WebSocket 支持，实现聊天等“即时消息提醒”功能
 */
@Configuration // 标记这是一个Spring配置类，会在项目启动时被自动加载
@EnableWebSocketMessageBroker // 启用基于消息代理的WebSocket功能（支持/topic等目的地）
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer { // 定义WebSocket配置类并实现配置接口

    @Override // 重写配置消息代理的方法
    public void configureMessageBroker(MessageBrokerRegistry config) { // 使用传入的config对象配置消息代理
        config.enableSimpleBroker("/topic"); // 启用简单内存消息代理，前端可订阅以/topic开头的目的地
        config.setApplicationDestinationPrefixes("/app"); // 设置应用消息前缀，前端发送到/app前缀的消息会路由到后端@MessageMapping
    } // 结束configureMessageBroker方法

    @Override // 重写注册STOMP端点的方法
    public void registerStompEndpoints(StompEndpointRegistry registry) { // 使用传入的registry对象注册WebSocket端点
        registry.addEndpoint("/ws") // 注册一个名为/ws的WebSocket端点，前端通过该地址建立连接
                .setAllowedOriginPatterns("*") // 允许所有来源的前端访问（开发环境方便调试，生产可按域名收紧）
                .withSockJS(); // 启用SockJS支持，兼容不支持原生WebSocket的浏览器或网络环境
    } // 结束registerStompEndpoints方法
} // 结束WebSocketConfig配置类定义

