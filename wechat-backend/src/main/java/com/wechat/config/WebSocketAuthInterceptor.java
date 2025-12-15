package com.wechat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * WebSocket 入站认证拦截器。
 * <p>
 * 在 STOMP CONNECT 阶段读取 {@code Authorization} 头的 Bearer Token，
 * 校验后将认证信息写入 {@link SecurityContextHolder} 与会话。
 * </p>
 * 注意：
 * - 仅在连接阶段处理认证，不影响后续消息分发；
 * - 与 HTTP JWT 过滤器逻辑保持一致。
 */
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorization = accessor.getNativeHeader("Authorization");
            if (authorization != null && !authorization.isEmpty()) {
                String token = authorization.get(0).substring(7); // 去掉 "Bearer " 前缀
                String username = jwtTokenUtil.getUsernameFromToken(token);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtTokenUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        accessor.setUser(authentication);
                    }
                }
            }
        }
        return message;
    }
}
