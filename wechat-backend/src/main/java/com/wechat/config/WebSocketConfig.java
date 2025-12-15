package com.wechat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单消息代理，用于将消息广播到客户端
        config.enableSimpleBroker("/topic", "/queue");
        // 设置应用程序目的地前缀，用于处理客户端发送的消息
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册WebSocket端点，客户端将使用此端点进行连接
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 添加认证拦截器
        registration.interceptors(webSocketAuthInterceptor);
    }
}
