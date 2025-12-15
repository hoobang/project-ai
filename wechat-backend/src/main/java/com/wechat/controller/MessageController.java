package com.wechat.controller;

import com.wechat.config.CustomUserDetails;
import com.wechat.entity.Message;
import com.wechat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // 获取聊天历史记录
    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Message>> getChatHistory(
            @RequestParam Long receiverId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Long currentUserId = currentUser.getUser().getId();
        List<Message> messages = messageService.getChatHistory(currentUserId, receiverId);
        return ResponseEntity.ok(messages);
    }

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 发送消息
    @PostMapping("/send")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Message> sendMessage(
            @RequestBody Message message,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        // 确保消息的发送者是当前登录用户
        message.getSender().setId(currentUser.getUser().getId());
        // 保存消息到数据库
        Message savedMessage = messageService.sendMessage(message);
        // 通过WebSocket发送消息给接收者
        messagingTemplate.convertAndSend("/queue/messages/" + savedMessage.getReceiver().getId(), savedMessage);
        // 通过WebSocket发送消息给发送者（用于确认发送成功）
        messagingTemplate.convertAndSend("/queue/messages/" + savedMessage.getSender().getId(), savedMessage);
        return ResponseEntity.ok(savedMessage);
    }

    // 获取未读消息
    @GetMapping("/unread")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Message>> getUnreadMessages(@AuthenticationPrincipal CustomUserDetails currentUser) {
        Long currentUserId = currentUser.getUser().getId();
        List<Message> messages = messageService.getUnreadMessages(currentUserId);
        return ResponseEntity.ok(messages);
    }

    // 标记消息为已读
    @PutMapping("/read/{messageId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long messageId) {
        messageService.markMessageAsRead(messageId);
        return ResponseEntity.ok().build();
    }

    // 标记所有消息为已读
    @PutMapping("/read/all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> markAllMessagesAsRead(
            @RequestParam Long senderId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Long currentUserId = currentUser.getUser().getId();
        messageService.markAllMessagesAsRead(senderId, currentUserId);
        return ResponseEntity.ok().build();
    }
}
