package com.wechat.controller;

import com.wechat.entity.Message;
import com.wechat.entity.GroupMessage;
import com.wechat.service.MessageService;
import com.wechat.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private GroupService groupService;

    // 发送私聊消息
    @MessageMapping("/message/send")
    public void sendPrivateMessage(Message message) {
        // 保存消息到数据库
        Message savedMessage = messageService.sendMessage(message);
        // 发送消息给接收者
        messagingTemplate.convertAndSend("/queue/messages/" + savedMessage.getReceiver().getId(), savedMessage);
        // 发送消息给发送者（用于确认发送成功）
        messagingTemplate.convertAndSend("/queue/messages/" + savedMessage.getSender().getId(), savedMessage);
    }

    // 发送群聊消息
    @MessageMapping("/group/send")
    public void sendGroupMessage(GroupMessage groupMessage) {
        // 保存群消息到数据库
        GroupMessage savedGroupMessage = groupService.sendGroupMessage(groupMessage);
        // 发送消息到群组
        messagingTemplate.convertAndSend("/topic/group/" + savedGroupMessage.getGroup().getId(), savedGroupMessage);
    }

    // 发送好友请求
    @MessageMapping("/friend/request")
    public void sendFriendRequest(Long senderId, Long receiverId) {
        // 这里可以调用FriendshipService发送好友请求
        // 然后发送通知给接收者
        messagingTemplate.convertAndSend("/queue/notifications/" + receiverId, "收到好友请求");
    }
}
