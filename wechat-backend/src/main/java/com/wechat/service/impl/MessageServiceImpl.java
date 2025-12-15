package com.wechat.service.impl;

import com.wechat.entity.Message;
import com.wechat.entity.User;
import com.wechat.repository.MessageRepository;
import com.wechat.repository.UserRepository;
import com.wechat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 私聊消息服务实现。
 * <p>
 * 逻辑说明：
 * - 发送消息时校验用户存在性并填充默认已读状态；
 * - 历史与未读查询依赖仓储层的组合查询；
 * - 已读标记提供单条与批量两种模式；
 * - 最近联系人查询基于接收者维度的去重结果。
 * </p>
 * 事务：
 * - 类级别使用 {@link org.springframework.transaction.annotation.Transactional}，方法在同一事务中运行。
 */
@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Message sendMessage(Long senderId, Long receiverId, String content, String type) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NoSuchElementException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NoSuchElementException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setType(type);
        message.setRead(false);

        return messageRepository.save(message);
    }

    @Override
    public Message sendMessage(Message message) {
        // 验证发送者和接收者是否存在
        User sender = userRepository.findById(message.getSender().getId())
                .orElseThrow(() -> new NoSuchElementException("Sender not found"));
        User receiver = userRepository.findById(message.getReceiver().getId())
                .orElseThrow(() -> new NoSuchElementException("Receiver not found"));

        // 设置发送者和接收者实体
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setRead(false);

        return messageRepository.save(message);
    }

    @Override
    public List<Message> getChatHistory(Long user1Id, Long user2Id) {
        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new NoSuchElementException("User 1 not found"));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new NoSuchElementException("User 2 not found"));

        return messageRepository.findChatHistoryBetweenUsers(user1, user2);
    }

    @Override
    public List<Message> getUnreadMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return messageRepository.findByReceiverAndReadFalse(user);
    }

    @Override
    public long countUnreadMessages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return messageRepository.countUnreadMessagesByReceiver(user);
    }

    @Override
    public void markMessageAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found"));
        message.setRead(true);
        messageRepository.save(message);
    }

    @Override
    public void markAllMessagesAsRead(Long senderId, Long receiverId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NoSuchElementException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NoSuchElementException("Receiver not found"));

        List<Message> messages = messageRepository.findBySenderAndReceiverOrderByCreatedAt(sender, receiver);
        for (Message message : messages) {
            if (!message.getRead()) {
                message.setRead(true);
                messageRepository.save(message);
            }
        }
    }

    @Override
    public List<User> getRecentContacts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return messageRepository.findRecentContacts(user);
    }

    @Override
    public Message sendFileMessage(Long senderId, Long receiverId, String content, String type, String filePath, String fileName, Long fileSize) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NoSuchElementException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NoSuchElementException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setType(type);
        message.setFilePath(filePath);
        message.setFileName(fileName);
        message.setFileSize(fileSize);
        message.setRead(false);

        return messageRepository.save(message);
    }
}
