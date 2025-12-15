package com.wechat.repository;

import com.wechat.entity.Friendship;
import com.wechat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 好友关系仓储接口。
 * <p>
 * 提供双向好友关系的查询、计数与审批状态过滤；
 * 支持基于用户对的唯一关系查找与列表统计。
 * </p>
 */
@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Optional<Friendship> findByUserAndFriend(User user, User friend);

    List<Friendship> findByUserAndStatus(User user, String status);

    List<Friendship> findByFriendAndStatus(User friend, String status);

    @Query("SELECT f.friend FROM Friendship f WHERE f.user = :user AND f.status = 'ACCEPTED'")
    List<User> findAcceptedFriendsByUser(@Param("user") User user);

    @Query("SELECT f.user FROM Friendship f WHERE f.friend = :user AND f.status = 'ACCEPTED'")
    List<User> findAcceptedFriendsByFriend(@Param("user") User user);

    @Query("SELECT f FROM Friendship f WHERE (f.user = :user AND f.friend = :friend) OR (f.user = :friend AND f.friend = :user)")
    Optional<Friendship> findFriendshipBetweenUsers(@Param("user") User user, @Param("friend") User friend);

    long countByUserAndStatus(User user, String status);

    long countByFriendAndStatus(User friend, String status);
}
