package com.wechat.service;

import com.wechat.entity.Message;
import com.wechat.entity.User;
import java.util.List;

/**
 * 私聊消息领域服务接口。
 * <p>
 * 职责：
 * - 发送文本或文件消息；
 * - 查询聊天历史、未读消息与最近联系人；
 * - 标记单条或整段会话为已读。
 * </p>
 */
public interface MessageService {

    /**
     * 发送文本消息。
     * @param senderId 发送者用户ID
     * @param receiverId 接收者用户ID
     * @param content 文本内容
     * @param type 类型（如 TEXT）
     * @return 保存后的消息
     * @throws NoSuchElementException 当用户不存在时
     */
    Message sendMessage(Long senderId, Long receiverId, String content, String type);

    /**
     * 发送消息（包含完整实体）。
     * @param message 消息实体（需包含发送者与接收者）
     * @return 保存后的消息
     * @throws NoSuchElementException 当用户不存在时
     */
    Message sendMessage(Message message);

    /**
     * 查询双方的历史消息。
     * @param user1Id 用户1
     * @param user2Id 用户2
     * @return 历史消息列表（按时间升序）
     */
    List<Message> getChatHistory(Long user1Id, Long user2Id);

    /**
     * 查询某用户的未读消息。
     * @param userId 用户ID
     * @return 未读消息列表
     */
    List<Message> getUnreadMessages(Long userId);

    /**
     * 统计某用户的未读消息数量。
     * @param userId 用户ID
     * @return 未读数量
     */
    long countUnreadMessages(Long userId);

    /**
     * 将指定消息标记为已读。
     * @param messageId 消息ID
     */
    void markMessageAsRead(Long messageId);

    /**
     * 将会话的所有消息标记为已读。
     * @param senderId 会话的对方用户ID
     * @param receiverId 当前用户ID
     */
    void markAllMessagesAsRead(Long senderId, Long receiverId);

    /**
     * 查询最近联系人列表（去重）。
     * @param userId 用户ID
     * @return 最近联系人（按最近消息时间）
     */
    List<User> getRecentContacts(Long userId);

    /**
     * 发送文件消息。
     * @param senderId 发送者用户ID
     * @param receiverId 接收者用户ID
     * @param content 文本内容或文件说明
     * @param type 类型（FILE/IMAGE/VIDEO/VOICE等）
     * @param filePath 文件存储路径
     * @param fileName 文件名
     * @param fileSize 文件大小（字节）
     * @return 保存后的消息
     * @throws NoSuchElementException 当用户不存在时
     */
    Message sendFileMessage(Long senderId, Long receiverId, String content, String type, String filePath, String fileName, Long fileSize);
}
