package com.wechat.service;

import com.wechat.entity.Message;
import com.wechat.entity.User;
import java.util.List;

public interface MessageService {

    Message sendMessage(Long senderId, Long receiverId, String content, String type);

    Message sendMessage(Message message);

    List<Message> getChatHistory(Long user1Id, Long user2Id);

    List<Message> getUnreadMessages(Long userId);

    long countUnreadMessages(Long userId);

    void markMessageAsRead(Long messageId);

    void markAllMessagesAsRead(Long senderId, Long receiverId);

    List<User> getRecentContacts(Long userId);

    Message sendFileMessage(Long senderId, Long receiverId, String content, String type, String filePath, String fileName, Long fileSize);
}
