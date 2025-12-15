package com.wechat.repository;

import com.wechat.entity.Message;
import com.wechat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderAndReceiverOrderByCreatedAt(User sender, User receiver);

    List<Message> findByReceiverAndReadFalse(User receiver);

    @Query("SELECT m FROM Message m WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) ORDER BY m.createdAt")
    List<Message> findChatHistoryBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver = :user AND m.read = false")
    long countUnreadMessagesByReceiver(@Param("user") User user);

    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.receiver = :user ORDER BY m.createdAt DESC")
    List<User> findRecentContacts(@Param("user") User user);
}
